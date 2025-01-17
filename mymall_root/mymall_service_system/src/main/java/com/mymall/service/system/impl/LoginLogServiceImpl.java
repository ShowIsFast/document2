package com.mymall.service.system.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.contract.system.LoginLogService;
import com.mymall.service.system.dao.LoginLogMapper;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.system.LoginLog;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@DubboService
public class LoginLogServiceImpl implements LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<LoginLog> findAll() {
        return loginLogMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<LoginLog> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<LoginLog> loginLogs = (Page<LoginLog>) loginLogMapper.selectAll();

        return new PageResult<LoginLog>(loginLogs.getTotal(),loginLogs.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<LoginLog> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);

        return loginLogMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<LoginLog> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<LoginLog> loginLogs = (Page<LoginLog>) loginLogMapper.selectByExample(example);

        return new PageResult<LoginLog>(loginLogs.getTotal(),loginLogs.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public LoginLog findById(Integer id) {
        return loginLogMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param loginLog
     */
    public void add(LoginLog loginLog) {
        loginLogMapper.insert(loginLog);
    }

    /**
     * 修改
     * @param loginLog
     */
    public void update(LoginLog loginLog) {
        loginLogMapper.updateByPrimaryKeySelective(loginLog);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        loginLogMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(LoginLog.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // ip
            if(searchMap.get("ip")!=null && !"".equals(searchMap.get("ip"))){
                criteria.andLike("ip","%"+searchMap.get("ip")+"%");
            }

            // 地区
            if(searchMap.get("location")!=null && !"".equals(searchMap.get("location"))){
                criteria.andLike("location","%"+searchMap.get("location")+"%");
            }

            // id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
        }

        return example;
    }

}
