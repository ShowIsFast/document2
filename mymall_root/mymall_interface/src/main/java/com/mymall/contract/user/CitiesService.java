package com.mymall.contract.user;

import com.mymall.pojo.user.Cities;
import com.mymall.pojo.entity.PageResult;

import java.util.*;

/**
 * cities业务逻辑层
 */
public interface CitiesService {

    public List<Cities> findAll();

    public PageResult<Cities> findPage(int page, int size);

    public List<Cities> findList(Map<String, Object> searchMap);

    public PageResult<Cities> findPage(Map<String, Object> searchMap, int page, int size);

    public Cities findById(String cityid);

    public void add(Cities cities);

    public void update(Cities cities);

    public void delete(String cityid);

}
