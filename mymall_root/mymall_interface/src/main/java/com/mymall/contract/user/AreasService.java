package com.mymall.contract.user;

import com.mymall.pojo.user.Areas;
import com.mymall.pojo.entity.PageResult;

import java.util.*;

/**
 * areas业务逻辑层
 */
public interface AreasService {

    public List<Areas> findAll();

    public PageResult<Areas> findPage(int page, int size);

    public List<Areas> findList(Map<String, Object> searchMap);

    public PageResult<Areas> findPage(Map<String, Object> searchMap, int page, int size);

    public Areas findById(String areaid);

    public void add(Areas areas);

    public void update(Areas areas);

    public void delete(String areaid);

}
