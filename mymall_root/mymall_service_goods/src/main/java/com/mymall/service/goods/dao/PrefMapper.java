package com.mymall.service.goods.dao;

import com.mymall.pojo.goods.Pref;

import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

public interface PrefMapper extends Mapper<Pref> {

    @Select("SELECT * FROM tb_pref ORDER BY id DESC LIMIT 1")
    public Map<String,Object> selectNewByOne();

}
