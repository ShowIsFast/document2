package com.mymall.contract.user;

import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.user.UserLog;

import java.util.List;
import java.util.Map;

public interface UserLogService {

    public List<UserLog> findAll();

    public PageResult<UserLog> findPage(int page, int size);

    public List<UserLog> findList(Map<String,Object> searchMap);

    public PageResult<UserLog> findPage(Map<String,Object> searchMap,int page, int size);

    public UserLog findById(Long id);

    public void add(UserLog userLog);

    public void update(UserLog userLog);

    public void delete(Long id);
}
