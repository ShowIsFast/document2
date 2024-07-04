package com.mymall.service.system.dao;

import com.mymall.pojo.system.Admin;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface AdminMapper extends Mapper<Admin> {

    @Select("SELECT  a.id,a.login_name,a.`status`, r.`name` FROM tb_admin a LEFT JOIN tb_admin_role ar ON a.id = ar.admin_id LEFT JOIN tb_role r ON ar.role_id = r.id LIMIT ${page},${size}")
    public List<Map> adminList(@Param("page") Integer page,@Param("size") Integer size);

    @Select("with cte as( select name,id,parent_id parentid from tb_menu WHERE parent_id = 0 )select * from cte")
    public List<Map<String,Object>> treeRole();

    @Delete("DELETE FROM tb_role_resource WHERE role_id = #{roleId}")
    public void delAdminRole(@Param("roleId") String roleId);

    @Select("select resource_id FROM tb_resource_menu WHERE menu_id in (${menus})GROUP BY resource_id")
    public List<String> findAmindRoleByRolds(@Param("menus") String menus);

    @Select("SELECT role_id FROM tb_admin_role WHERE admin_id = #{adminId} LIMIT 1")
    public String findRoleByAdminId(@Param("adminId") String adminId);

}
