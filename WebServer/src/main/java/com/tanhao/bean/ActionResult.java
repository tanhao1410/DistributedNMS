package com.tanhao.bean;

/**
 * 对于没有请求的返回此信息
 */
public class ActionResult {
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;
}
