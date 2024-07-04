package com.mymall.service.system.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.contract.system.AdminService;
import com.mymall.service.system.dao.RoleResourceMapper;
import com.mymall.service.system.dao.AdminMapper;
import com.mymall.service.system.dao.AdminRoleMapper;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.system.Admin;
import com.mymall.pojo.system.AdminRole;

import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
@Transactional
public class AdminServiceImpl implements AdminService {

    private static Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RoleResourceMapper roleResourceMapper;

    @Autowired
    private AdminRoleMapper adminRoleMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Admin> findAll() {
        return adminMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Admin> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Admin> admins = (Page<Admin>) adminMapper.selectAll();

        return new PageResult<Admin>(admins.getTotal(),admins.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Admin> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);

        return adminMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Admin> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Admin> admins = (Page<Admin>) adminMapper.selectByExample(example);

        return new PageResult<Admin>(admins.getTotal(),admins.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Admin findById(Integer id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param admin
     */
    public void add(Admin admin) {
        adminMapper.insert(admin);
    }

    /**
     * 修改
     * @param admin
     */
    public void update(Admin admin) {
        adminMapper.updateByPrimaryKeySelective(admin);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        adminMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void saveAdminRoles(Integer adminId, Integer[] roleIds) {
        //删除用户原来的角色列表
        Example example= new Example(AdminRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("adminId",adminId);
        adminRoleMapper.deleteByExample(example);
        //循环添加角色
        for (Integer roleId:roleIds){
            AdminRole adminRole=new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            adminRoleMapper.insert(adminRole);
        }
    }

    @Override
    public List<Map> adminList(int page, int size) {
        return adminMapper.adminList(page,size);
    }

    @Override
    public String treeRole() {
        List<Map<String,Object>> maps = adminMapper.treeRole();
        return treeData(maps);
    }


    @Override
    public List<String> findAmindRoleByRolds(String roles) {
        return adminMapper.findAmindRoleByRolds(roles);//添加的权限ID
    }

    @Override
    public void saveNewAdmin(String logName, String passWord, String roleId) {
        //插入新数据
        Admin admin = new Admin();
        admin.setLoginName(logName);
        admin.setPassword(passWord);
        admin.setStatus("1");
        add(admin);

        //查询新插入ID
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("loginName",logName);
        List<Admin> list = findList(searchMap);
        Integer id = list.get(0).getId();

        //插入角色信息
        AdminRole adminRole = new AdminRole();
        adminRole.setAdminId(id);
        adminRole.setRoleId(Integer.parseInt(roleId));
        adminRoleMapper.insert(adminRole);
    }

    @Override
    public void saveOldAdmin(String adminId, String passWord, String roleId) {
        Admin admin = new Admin();
        admin.setId(Integer.parseInt(adminId));
        admin.setPassword(passWord);
        adminMapper.updateByPrimaryKeySelective(admin);

        //更新角色信息;
        adminRoleMapper.updateByAdmin(roleId,adminId);
    }

    @Override
    public String findRoleByAdminId(String adminId) {
        return adminMapper.findRoleByAdminId(adminId);
    }


    public static String treeData(List<Map<String, Object>> records){
        Map<String, Map<String, Object>> tree = new HashMap<>();
        // init head data
        Map<String, Object> head = new HashMap<>();
        head.put("id", "0");
        head.put("children", new ArrayList<Map<String, Object>>());
        tree.put("0", head);

        for (int i = 0, l = records.size(); i < l; i++) {
            Map<String, Object> record = records.get(i);
            String parentId = record.get("parentid").toString();

            // set default childern
            record.put("children", new ArrayList<Map<String, Object>>());
            if (tree.containsKey(parentId)) {
                // add children
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) tree.get(parentId).get("children");
                children.add(record);

                // add record
                tree.put(record.get("id").toString(), record);
            }
        }
        JSONObject result = new JSONObject();
        result.putAll(tree.get("0"));
        log.info("处理后数据:");
        log.info(result.toString());

        return result.toString();
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 用户名
            if(searchMap.get("loginName")!=null && !"".equals(searchMap.get("loginName"))){
                criteria.andLike("loginName","%"+searchMap.get("loginName")+"%");
            }

            // 密码
            if(searchMap.get("password")!=null && !"".equals(searchMap.get("password"))){
                criteria.andLike("password","%"+searchMap.get("password")+"%");
            }

            // 状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }

            // id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
        }

        return example;
    }

}
