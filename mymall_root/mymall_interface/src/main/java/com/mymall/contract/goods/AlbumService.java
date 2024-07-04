package com.mymall.contract.goods;

import com.mymall.pojo.goods.Album;
import com.mymall.pojo.entity.PageResult;

import java.util.*;

/**
 * 业务逻辑层
 */
public interface AlbumService {

    public List<Album> findAll();

    public PageResult<Album> findPage(int page, int size);

    public List<Album> findList(Map<String,Object> searchMap);

    public PageResult<Album> findPage(Map<String,Object> searchMap,int page, int size);

    public Album findById(Long id);

    public void add(Album album);

    public void update(Album album);

    public void delete(Long id);

}
