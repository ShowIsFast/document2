package com.mymall.service.system.dao;

import com.mymall.pojo.system.Menu;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MenuMapper extends Mapper<Menu> {

    @Select("SELECT * FROM tb_menu WHERE id IN ( SELECT menu_id FROM tb_role_menu WHERE role_id IN ( SELECT role_id FROM tb_admin_role WHERE admin_id IN  (SELECT id FROM tb_admin WHERE login_name=#{loginName} ) ) )")
    public List<Menu> findMenuListByLoginName(@Param("loginName") String  loginName);

    @Select("SELECT CONCAT(rm.menu_id,'') menu_id FROM tb_admin a  LEFT JOIN tb_admin_role ar ON a.id = ar.admin_id LEFT JOIN tb_role_resource rr ON ar.role_id = rr.role_id LEFT JOIN tb_resource_menu rm ON rr.resource_id = rm.resource_id WHERE  a.login_name = #{username}")
    public List<String> findUserMenu(@Param("username") String username);

    @Select("SELECT CONCAT( rm.menu_id, '' ) menu_id FROM tb_admin_role ar LEFT JOIN tb_role_resource rr ON ar.role_id = rr.role_id LEFT JOIN tb_resource_menu rm ON rr.resource_id = rm.resource_id  WHERE ar.role_id = #{roleId}")
    public  List<String> findMenuByRole(@Param("roleId") String roleId);

    @Select("SELECT m.id,m.`name`,m.icon,m.url,m.parent_id parentId FROM  tb_menu m WHERE m.id in (SELECT CONCAT(rm.menu_id,'') menu_id FROM tb_admin a  LEFT JOIN tb_admin_role ar ON a.id = ar.admin_id LEFT JOIN tb_role_resource rr ON ar.role_id = rr.role_id LEFT JOIN tb_resource_menu rm ON rr.resource_id = rm.resource_id WHERE  a.login_name = #{username})")
    public List<Menu> selectUserRoleList(@Param("username") String username);

}
