package com.mymall.service.seckill.dao;

import com.mymall.pojo.seckill.SeckillGoods;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SeckillGoodsMapper extends Mapper<SeckillGoods> {

    @Select("SELECT * FROM tb_seckill_goods ORDER BY id DESC LIMIT 1")
    public Map<String,Object> selectNewByOne();

    @Select("${sql}")
    public List<SeckillGoods> selectSeckillGoodsList(@Param("sql") String sql);

}
