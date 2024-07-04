package com.mymall.pojo.system;

import javax.persistence.Table;

import java.io.Serializable;

/**
 * admin实体类
 * @author Administrator
 *
 */
@Table(name="tb_role_resource")
public class RoleResource implements Serializable {

    private String roleId;

    private String resourceId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

}
