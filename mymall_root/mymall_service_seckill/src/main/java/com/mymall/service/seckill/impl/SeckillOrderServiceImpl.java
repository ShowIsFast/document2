package com.mymall.service.seckill.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.contract.seckill.SeckillOrderService;
import com.mymall.service.seckill.dao.SeckillOrderMapper;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.entity.Result;
import com.mymall.pojo.seckill.SeckillOrder;
import com.mymall.pojo.entity.SeckillStatus;
import com.mymall.service.seckill.task.MultiThreadingCreateOrder;

import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@DubboService
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    private static Logger log = LoggerFactory.getLogger(SeckillOrderServiceImpl.class);

    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderMapper.selectAll();
    }

    @Override
    public PageResult<SeckillOrder> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<SeckillOrder> seckillOrder = (Page<SeckillOrder>) seckillOrderMapper.selectAll();

        return new PageResult<SeckillOrder>(seckillOrder.getTotal(),seckillOrder.getResult());
    }

    @Override
    public List<SeckillOrder> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);

        return seckillOrderMapper.selectByExample(example);
    }

    @Override
    public PageResult<SeckillOrder> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<SeckillOrder> seckillOrder = (Page<SeckillOrder>) seckillOrderMapper.selectByExample(example);

        return new PageResult<SeckillOrder>(seckillOrder.getTotal(),seckillOrder.getResult());
    }

    @Override
    public SeckillOrder findById(String id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(SeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }

    @Override
    public void update(SeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKeySelective(seckillOrder);
    }

    @Override
    public void delete(String id) {
        seckillOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据用户名查询订单
     * @param username
     * @return
     */
    @Override
    public SeckillOrder queryByUserName(String username) {
        return (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
    }

    /**
     * 修改订单
     * @param outtradeno
     * @param username
     * @param transactionid
     */
    @Override
    public void updateStatus(String outtradeno, String username, String transactionid) {
        //根据用户名查询订单数据
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);

        if(seckillOrder!=null){
            //修改订单->持久化到MySQL中
            seckillOrder.setPayTime(new Date());
            seckillOrder.setStatus("1");        //已支付
            seckillOrderMapper.insertSelective(seckillOrder);

            //Redis中的订单
            redisTemplate.boundHashOps("SeckillOrder").delete(username);

            //清理用户排队信息
            redisTemplate.boundHashOps("UserQueueCount").delete(username);

            //清理排队存储信息
            redisTemplate.boundHashOps("UserQueueStatus").delete(username);
        }
    }

    /**
     * 查询抢单状态
     * @param username
     * @return
     */
    @Override
    public SeckillStatus queryStatus(String username) {
        return (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);
    }

    /**
     * 秒杀下单
     * @param id:商品ID
     * @param time:商品时区
     * @param username:用户名
     * @return
     * 秒杀下单所有请求全部进入队列，然后通过 multiThreadingCreateOrder.createOrder() 异步抢单
     */
    @Override
    public Result add(Long id, String time, String username) {
        log.info("[mymall_service_seckill][SeckillOrderServiceImpl][add][username]:      "+username);

        Result result = new Result();

        //用户每秒杀抢单一次，redis 中就记录一次，解决用户多次抢单问题
        Long userQueueCount = redisTemplate.boundHashOps("UserQueueCount").increment(username, 1);
        if(userQueueCount>1){
            log.info("[mymall_service_seckill][SeckillOrderServiceImpl][add][重复抢单!!!!!!]");

            result.setCode(100);
            result.setMessage("重复抢单.....");

            return result;
        }

        //判断是否还有库存
        Long size = redisTemplate.boundListOps("SeckillGoodsCountList_" + id).size();
        if(size<=0){
            log.info("[mymall_service_seckill][SeckillOrderServiceImpl][add][库存不足!!!!!!]");

            result.setCode(101);
            result.setMessage("库存不足.....");

            return result;
        }

        //组装请求的秒杀状态
        SeckillStatus seckillStatus = new SeckillStatus(username, new Date(),1, id, time);

        //将秒杀请求写入 list
        redisTemplate.boundListOps("SeckillOrderQueue").leftPush(seckillStatus);

        //通过 hash 存储已经排队的请求
        redisTemplate.boundHashOps("UserQueueStatus").put(username, seckillStatus);

        log.info("[mymall_service_seckill][SeckillOrderServiceImpl][add][异步操作调用---createOrder!!!!!!]");

        //异步操作调用
        multiThreadingCreateOrder.createOrder();

        result.setCode(0);
        result.setMessage("抢单成功....");

        return result;
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(SeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            if(searchMap.get("userId")!=null && !"".equals(searchMap.get("userId"))){
                criteria.andLike("userId","%"+searchMap.get("userId")+"%");
            }
        }

        return example;
    }

}
