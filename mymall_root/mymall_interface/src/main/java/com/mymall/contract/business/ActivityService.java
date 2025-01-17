package com.mymall.contract.business;


import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.business.Activity;

import java.util.*;

/**
 * activity业务逻辑层
 */
public interface ActivityService {

    public List<Activity> findAll();

    public PageResult<Activity> findPage(int page, int size);

    public List<Activity> findList(Map<String, Object> searchMap);

    public PageResult<Activity> findPage(Map<String, Object> searchMap, int page, int size);

    public Activity findById(Integer id);

    public void add(Activity activity);

    public void update(Activity activity);

    public void delete(Integer id);

}
