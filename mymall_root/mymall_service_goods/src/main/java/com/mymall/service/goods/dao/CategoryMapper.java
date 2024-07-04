package com.mymall.service.goods.dao;

import com.mymall.pojo.goods.Category;

import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

public interface CategoryMapper extends Mapper<Category> {

    @Select("SELECT * FROM tb_category ORDER BY id DESC LIMIT 1")
    public Map<String,Object> selectNewByOne();

}
