package com.mymall.service.system.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mymall.contract.system.RoleService;
import com.mymall.service.system.dao.AdminMapper;
import com.mymall.service.system.dao.RoleResourceMapper;
import com.mymall.pojo.system.RoleResource;
import com.mymall.service.system.dao.RoleMapper;
import com.mymall.pojo.entity.PageResult;
import com.mymall.pojo.system.Role;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RoleResourceMapper roleResourceMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Role> findAll() {
        return roleMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Role> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Role> roles = (Page<Role>) roleMapper.selectAll();

        return new PageResult<Role>(roles.getTotal(),roles.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Role> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);

        return roleMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Role> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Role> roles = (Page<Role>) roleMapper.selectByExample(example);

        return new PageResult<Role>(roles.getTotal(),roles.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Role findById(Integer id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param role
     */
    public void add(Role role) {
        roleMapper.insert(role);
    }

    /**
     * 修改
     * @param role
     */
    public void update(Role role) {
        roleMapper.updateByPrimaryKeySelective(role);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        roleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void save(String name, String meuns) {
        //新增角色
        Role role = new Role();
        role.setName(name);
        add(role);

        //查询新角色roleID
        Map<String,Object> searchMap = new HashMap<>();
        searchMap.put("name",name);
        List<Role> list = findList(searchMap);
        Integer roleId = list.get(0).getId();

        //查询新增权限的ID
        List<String> resourceIds = adminMapper.findAmindRoleByRolds(meuns);
        for (String resourceId: resourceIds) {//循环添加新的权限
            RoleResource roleResource = new RoleResource();
            roleResource.setResourceId(resourceId);
            roleResource.setRoleId(String.valueOf(roleId));
            roleResourceMapper.insert(roleResource);
        }
    }

    @Override
    public void updateRoleResource(String roleId, String menus,String name) {
        Role role = new Role();
        role.setId(Integer.parseInt(roleId));
        role.setName(name);
        roleMapper.updateByPrimaryKeySelective(role);//修改角色名称
        List<String> resourceIds = adminMapper.findAmindRoleByRolds(menus);//添加的权限ID
        if(resourceIds.size() > 0){
            adminMapper.delAdminRole(roleId);//删除旧的权限
            for (String resourceId: resourceIds) {//循环添加新的权限
                RoleResource roleResource = new RoleResource();
                roleResource.setResourceId(resourceId);
                roleResource.setRoleId(roleId);
                roleResourceMapper.insert(roleResource);
            }
        }
    }

    @Override
    public List<String> selectRoleMenu(String roleId) {
        return roleMapper.selectRoleMenu(roleId);
    }


    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 角色名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }

            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
        }

        return example;
    }

}
