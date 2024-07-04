package com.mymall.contract.goods;

import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.goods.AlbumItem;

import java.util.List;
import java.util.Map;

/**
 * 业务逻辑层
 */
public interface AlbumItemService {

    public List<AlbumItem> findAll();

    public PageResult<AlbumItem> findPage(int page, int size);

    public List<AlbumItem> findList(Map<String,Object> searchMap);

    public PageResult<AlbumItem> findPage(Map<String,Object> searchMap,int page, int size);

    public AlbumItem findById(Long id);

    public void add(AlbumItem albumItem);

    public void update(AlbumItem albumItem);

    public void delete(Long id);

    public Map<String,Object> selectNewByOne();

}
