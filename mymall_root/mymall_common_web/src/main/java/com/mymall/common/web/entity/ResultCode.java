package com.mymall.common.web.entity;

public enum ResultCode {

    SUCCESS(1,"操作成功"),
    FAIL(0,"操作失败");
    private int state;
    private String message;

     ResultCode(int state) {
        this.state = state;
    }

     ResultCode(int state, String message) {
        this.state = state;
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public String getMessage() {
        return message;
    }

}
