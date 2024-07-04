package com.mymall.contract.system;

import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.system.Admin;

import java.util.*;

/**
 * admin业务逻辑层
 */
public interface AdminService {

    public List<Admin> findAll();

    public PageResult<Admin> findPage(int page, int size);

    public List<Admin> findList(Map<String,Object> searchMap);

    public PageResult<Admin> findPage(Map<String,Object> searchMap,int page, int size);

    public Admin findById(Integer id);

    public void add(Admin admin);

    public void update(Admin admin);

    public void delete(Integer id);

    public void saveAdminRoles(Integer adminId, Integer[] roleIds);

    public List<Map> adminList(int page, int size);

    public String treeRole();

    public List<String> findAmindRoleByRolds(String roles);

    public void saveNewAdmin(String logName,String passWord,String roleId);

    public void saveOldAdmin(String adminId,String passWord,String roleId);

    public String findRoleByAdminId(String adminId);

}
