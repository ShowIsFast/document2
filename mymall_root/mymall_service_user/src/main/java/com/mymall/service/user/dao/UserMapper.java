package com.mymall.service.user.dao;

import com.mymall.pojo.user.User;

import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface UserMapper extends Mapper<User> {

    @Select("SELECT CASE WHEN sex = 0 OR sex IS NULL THEN '女人' ELSE '男人' END 'categoryName',SUM( CASE WHEN sex = 0 OR sex IS NULL THEN 1 ELSE 1 END ) 'userCount' FROM `tb_user` GROUP BY sex")
    public List<Map<String,Object>> categoryUser();

    @Select("SELECT * FROM tb_user ORDER BY id DESC LIMIT 1")
    public Map<String,Object> selectNewByOne();

}
