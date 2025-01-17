package com.mymall.contract.system;

import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.system.Menu;

import java.util.*;

/**
 * menu业务逻辑层
 */
public interface MenuService {

    public List<Menu> findAll();

    public PageResult<Menu> findPage(int page, int size);

    public List<Menu> findList(Map<String,Object> searchMap);

    public PageResult<Menu> findPage(Map<String,Object> searchMap,int page, int size);

    public Menu findById(String id);

    public void add(Menu menu);

    public void update(Menu menu);

    public void delete(String id);

    public List<Map> findAllMenu(String username);

    public List<Map> findMenuListByLoginName(String loginName);

    public List findMenuByRole(String username);

}
