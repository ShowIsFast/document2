package com.mymall.service.goods.dao;

import com.mymall.pojo.goods.Spu;

import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

public interface SpuMapper extends Mapper<Spu> {

    @Select("SELECT * FROM tb_spu ORDER BY id DESC LIMIT 1")
    public Map<String,Object> selectNewByOne();

}
