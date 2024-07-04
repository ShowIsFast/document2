package com.mymall.pojo.user;

import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Date;

@Table(name = "tb_user_log")
public class UserLog implements Serializable {

    @Id
    private Long id;

    private String username;        //用户
    private String instructions;    //说明
    private String oldData;         //操作前数据
    private String newData;         //操作后数据
    private Date updateTime;        //操作时间

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getOldData() {
        return oldData;
    }

    public void setOldData(String oldData) {
        this.oldData = oldData;
    }

    public String getNewData() {
        return newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserLog{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", instructions='" + instructions + '\'' +
                ", oldData='" + oldData + '\'' +
                ", newData='" + newData + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }

}
