package com.mymall.service.goods.dao;

import com.mymall.pojo.goods.AlbumItem;
import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

public interface AlbumItemMapper extends Mapper<AlbumItem> {

    @Select("SELECT * FROM tb_album_item ORDER BY id DESC LIMIT 1")
    public Map<String,Object> selectNewByOne();

}
