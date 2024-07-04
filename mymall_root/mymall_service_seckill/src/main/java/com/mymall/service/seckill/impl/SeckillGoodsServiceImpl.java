package com.mymall.service.seckill.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.contract.seckill.SeckillGoodsService;
import com.mymall.service.seckill.dao.SeckillGoodsMapper;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.seckill.SeckillGoods;

import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@DubboService
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    private static Logger log = LoggerFactory.getLogger(SeckillGoodsServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    /****
     * 根据商品ID查询商品详情
     * @param time:商品秒杀时区
     * @param id:商品ID
     * @return
     */
    @Override
    public SeckillGoods one(String time, Long id) {
        return (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods_"+time).get(id);
    }

    /***
     * 根据时间区间查询秒杀商品列表
     * @param time
     * @return
     */
    @Override
    public List<SeckillGoods> list(String time) {
        //组装key
        String key = "SeckillGoods_"+time;

        return redisTemplate.boundHashOps(key).values();
    }


    @Override
    public Map<String,Object> selectNewByOne() {
        return seckillGoodsMapper.selectNewByOne();
    }

    /**
     * 返回全部记录
     * @return
     */
    public List<SeckillGoods> findAll() {
        return seckillGoodsMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<SeckillGoods> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<SeckillGoods> seckillGoods = (Page<SeckillGoods>) seckillGoodsMapper.selectAll();

        return new PageResult<SeckillGoods>(seckillGoods.getTotal(),seckillGoods.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<SeckillGoods> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);

        return seckillGoodsMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<SeckillGoods> findPage(Map<String, Object> searchMap, int page, int size) {
        log.info("[mymall_service_goods][AlbumItemImpl][findPage][searchMap:]"+searchMap);
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<SeckillGoods> seckillGoods = (Page<SeckillGoods>) seckillGoodsMapper.selectByExample(example);

        return new PageResult<SeckillGoods>(seckillGoods.getTotal(),seckillGoods.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public SeckillGoods findById(Long id) {
        return seckillGoodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param seckillGoods
     */
    public void add(SeckillGoods seckillGoods) {
        seckillGoodsMapper.insert(seckillGoods);
    }

    /**
     * 修改
     * @param seckillGoods
     */
    public void update(SeckillGoods seckillGoods) {
        seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Long id) {
        seckillGoodsMapper.deleteByPrimaryKey(id);
    }


    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(SeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // id
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",String.valueOf(searchMap.get("id")));
            }

            // spu ID
            if(searchMap.get("goodsId")!=null && !"".equals(searchMap.get("goodsId"))){
                criteria.andEqualTo("goodsId",String.valueOf(searchMap.get("goodsId")));
            }

            // sku ID
            if(searchMap.get("itemId")!=null && !"".equals(searchMap.get("itemId"))){
                criteria.andLike("itemId",String.valueOf(searchMap.get("itemId")));
            }

            // 标题
            if(searchMap.get("title")!=null && !"".equals(searchMap.get("title"))){
                criteria.andEqualTo("title","%"+searchMap.get("title")+"%");
            }

            // 原价格
            if(searchMap.get("price")!=null && !"".equals(searchMap.get("price"))){
                criteria.andEqualTo("price",String.valueOf(searchMap.get("price")));
            }

            // 添加日期
            if(searchMap.get("createTime")!=null && !"".equals(searchMap.get("createTime"))){
                criteria.andEqualTo("createTime",String.valueOf(searchMap.get("createTime")));
            }

            //  审核日期
            if(searchMap.get("checkTime")!=null && !"".equals(searchMap.get("checkTime"))){
                criteria.andEqualTo("checkTime",String.valueOf(searchMap.get("checkTime")));
            }

            //  审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",String.valueOf(searchMap.get("status")));
            }

            // 描述
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andEqualTo("introduction","%"+searchMap.get("introduction")+"%");
            }
        }

        return example;
    }

}
