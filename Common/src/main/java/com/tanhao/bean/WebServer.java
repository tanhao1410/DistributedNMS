package com.tanhao.bean;

import java.io.Serializable;

public class WebServer implements Serializable {

    /**
     * 服务器名称
     */
    private String name;
    /**
     * 存活时间戳，表明在该时间，通知redis，本服务器更新过
     */
    private long timeStampe;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeStampe() {
        return timeStampe;
    }

    public void setTimeStampe(long timeStampe) {
        this.timeStampe = timeStampe;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof WebServer){
            return this.name.equals(((WebServer) obj).getName());
        }
        return false;
    }
}
