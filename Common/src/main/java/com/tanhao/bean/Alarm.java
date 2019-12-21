package com.tanhao.bean;

import java.io.Serializable;
import java.sql.Time;

public class Alarm implements Serializable {

    private Node node;
    private String collectItemName;
    /**
     * 告警级别，0为正常
     */
    private int level;
    private String content;
    private Time time;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String getCollectItemName() {
        return collectItemName;
    }

    public void setCollectItemName(String collectItemName) {
        this.collectItemName = collectItemName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }


    @Override
    public int hashCode() {
        return getCollectItemName().hashCode();
    }

    /**
     * 重写判断两个告警是否相同的方法，若告警的设备和告警的类型以及告警的等级和内容相同，则认为是同一个告警
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {

        //
        if (obj instanceof Alarm) {
            if (((Alarm) obj).getNode().getId().equals(this.getNode().getId())
                    && ((Alarm) obj).getCollectItemName().equals(this.getCollectItemName())
                    &&((Alarm) obj).getLevel() == this.getLevel()) {
                return true;
            }
        }

        return false;
    }
}
