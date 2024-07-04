package com.mymall.service.seckill.task;

import com.mymall.service.seckill.dao.SeckillGoodsMapper;
import com.mymall.service.seckill.dao.SeckillOrderMapper;
import com.mymall.pojo.seckill.SeckillGoods;
import com.mymall.pojo.seckill.SeckillOrder;
import com.mymall.pojo.entity.SeckillStatus;
import com.mymall.common.service.util.IdWorker;

import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Component
public class MultiThreadingCreateOrder {

    private static Logger log = LoggerFactory.getLogger(MultiThreadingCreateOrder.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private IdWorker idWorker;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 异步操作方法
     */
    @Async
    public void createOrder(){
        try {
            log.info("----准备@Async执行----");

            SeckillStatus seckillStauts = (SeckillStatus) redisTemplate.boundListOps("SeckillOrderQueue").rightPop();

            // 获取用户抢单时的请求数据
            String username=seckillStauts.getUsername();
            String time=seckillStauts.getTime();
            Long id=seckillStauts.getGoodsId();

            // 用来解决超卖问题的 redis list
            // 从 list 中获取 商品id
            Object sid = redisTemplate.boundListOps("SeckillGoodsCountList_" + id).rightPop();

            // 如果秒杀商品被抢完，则清理相关排队信息
            if(sid==null){
                clearQueue(seckillStauts);

                return;
            }

            //查询商品详情
            SeckillGoods goods = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_"+time).get(id);

            log.info(username+":"+Thread.currentThread().getId()+"----查询到 的商品库存："+goods.getStockCount());
            if(goods!=null && goods.getStockCount()>0){
                // 组装订单
                SeckillOrder seckillOrder = new SeckillOrder();
                seckillOrder.setId(idWorker.nextId());
                seckillOrder.setSeckillId(id);
                seckillOrder.setMoney(goods.getCostPrice());
                seckillOrder.setUserId(username);
                seckillOrder.setSellerId(goods.getSellerId());
                seckillOrder.setCreateTime(new Date());
                seckillOrder.setStatus("0");
                seckillOrderMapper.insertSelective(seckillOrder);   //秒杀订单入库
                redisTemplate.boundHashOps("SeckillOrder").put(username,seckillOrder);

                Integer stockCount = goods.getStockCount()-1;
                Long surplusCount = stockCount.longValue();
                log.info("库存消减后剩余:"+surplusCount);

                goods.setStockCount(surplusCount.intValue());
                // 如果秒杀商品库存是0，则将数据同步到秒杀商品表中
                if(surplusCount<=0){
                    seckillGoodsMapper.updateByPrimaryKeySelective(goods);
                    //清理Redis缓存
                    redisTemplate.boundHashOps("SeckillGoods_"+time).delete(id);
                }else{
                    //将数据同步到Redis
                    redisTemplate.boundHashOps("SeckillGoods_"+time).put(id,goods);
                }

                log.info("----准备更新seckillStauts----");
                // 更新抢单状态
                seckillStauts.setOrderId(seckillOrder.getId());
                seckillStauts.setMoney(seckillOrder.getMoney().floatValue());
                seckillStauts.setStatus(2); // 2表示抢单成功，待支付
                redisTemplate.boundHashOps("UserQueueStatus").put(username,seckillStauts);
                log.info("----准备更新seckillStauts完毕----");

                // 发送延迟消息，处理未支付的秒杀订单
                // 延时时间是 30m，级别是16
                SendResult result=rocketMQTemplate.syncSend("seckill_order:delay", MessageBuilder.withPayload(seckillStauts).build(),2000,16);
                System.out.println(result.toString());
            }

            log.info("----正在执行----");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清理用户排队信息
     * @param seckillStauts
     */
    private void  clearQueue(SeckillStatus seckillStauts) {
        // 清理限制用户只抢一次的 hash 结构
        redisTemplate.boundHashOps("UserQueueCount").delete(seckillStauts.getUsername());

        // 清理用户已经排队的 hash 结构
        redisTemplate.boundHashOps("UserQueueStatus").delete(seckillStauts.getUsername());
    }

}
