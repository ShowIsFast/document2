package com.mymall.service.order.dao;

import com.mymall.pojo.order.Preferential;

import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

public interface PreferentialMapper extends Mapper<Preferential> {

    @Select("SELECT * FROM tb_preferential ORDER BY id DESC LIMIT 1")
    public Map<String,Object> selectNewByOne();

}
