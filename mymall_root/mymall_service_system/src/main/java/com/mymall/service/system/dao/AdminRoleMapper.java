package com.mymall.service.system.dao;

import com.mymall.pojo.system.AdminRole;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import tk.mybatis.mapper.common.Mapper;

public interface AdminRoleMapper extends Mapper<AdminRole> {

    @Update("UPDATE tb_admin_role SET role_id = #{roleId} WHERE admin_id = #{adminId}")
    public void updateByAdmin(@Param("roleId") String roleId,@Param("adminId") String adminId);

}
