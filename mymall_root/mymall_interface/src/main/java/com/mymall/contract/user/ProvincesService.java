package com.mymall.contract.user;

import com.mymall.pojo.user.Provinces;
import com.mymall.pojo.entity.PageResult;

import java.util.*;

/**
 * provinces业务逻辑层
 */
public interface ProvincesService {

    public List<Provinces> findAll();

    public PageResult<Provinces> findPage(int page, int size);

    public List<Provinces> findList(Map<String, Object> searchMap);

    public PageResult<Provinces> findPage(Map<String, Object> searchMap, int page, int size);

    public Provinces findById(String provinceid);

    public void add(Provinces provinces);

    public void update(Provinces provinces);

    public void delete(String provinceid);

}
