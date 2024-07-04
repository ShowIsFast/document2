package com.mymall.service.user.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.service.user.dao.UserLogMapper;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.user.UserLog;
import com.mymall.contract.user.UserLogService;

import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@DubboService
public class UserLogServiceImpl implements UserLogService {

    private static Logger log = LoggerFactory.getLogger(UserLogServiceImpl.class);

    @Autowired
    private UserLogMapper userLogMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<UserLog> findAll() {
        return userLogMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<UserLog> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<UserLog> userLog = (Page<UserLog>) userLogMapper.selectAll();

        return new PageResult<UserLog>(userLog.getTotal(),userLog.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<UserLog> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);

        return userLogMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<UserLog> findPage(Map<String, Object> searchMap, int page, int size) {
        log.info("[mymall_service_user][UserLogServiceImpl][findPage][searchMap:]"+searchMap);
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<UserLog> userLog = (Page<UserLog>) userLogMapper.selectByExample(example);

        return new PageResult<UserLog>(userLog.getTotal(),userLog.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public UserLog findById(Long id) {
        return userLogMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param userLog
     */
    public void add(UserLog userLog) {
        userLogMapper.insert(userLog);
    }

    /**
     * 修改
     * @param userLog
     */
    public void update(UserLog userLog) {
        userLogMapper.updateByPrimaryKeySelective(userLog);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Long id) {
        userLogMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(UserLog.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 用户
            if(searchMap.get("username")!=null && !"".equals(searchMap.get("username"))){
                criteria.andEqualTo("username",String.valueOf(searchMap.get("username")));
            }

            // 说明
            if(searchMap.get("instructions")!=null && !"".equals(searchMap.get("instructions"))){
                criteria.andLike("instructions","%"+searchMap.get("instructions")+"%");
            }

            //操作时间
            if(searchMap.get("updateTime")!=null && !"".equals(searchMap.get("updateTime"))){
                criteria.andEqualTo("updateTime",String.valueOf(searchMap.get("updateTime")));
            }
        }

        return example;
    }

}
