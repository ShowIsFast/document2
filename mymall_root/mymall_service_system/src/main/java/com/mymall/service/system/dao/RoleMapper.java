package com.mymall.service.system.dao;

import com.mymall.pojo.system.Role;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RoleMapper extends Mapper<Role> {

    @Select("SELECT rm.menu_id FROM tb_role_resource rr LEFT JOIN tb_resource_menu rm ON rr.resource_id = rm.resource_id WHERE rr.role_id = #{roleId}")
    public List<String> selectRoleMenu(@Param("roleId") String roleId);

}
