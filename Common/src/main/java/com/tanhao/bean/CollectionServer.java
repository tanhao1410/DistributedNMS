package com.tanhao.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 代表一个采集服务器，存储在Redis服务器中
 */
public class CollectionServer implements Serializable{

    /**
     * 服务器名称
     */
    private String name;
    /**
     * 存活时间戳，表明在该时间，通知redis，本服务器更新过
     */
    private long timeStampe;
    /**
     * 本采集服务器所分配的监控节点
     */
    private Set<Node> assignedNodes = new HashSet<Node>();

    public CollectionServer() {
    }
    public CollectionServer(String name) {
        this.name = name;
    }

    public void setAssignedNodes(Set<Node> assignedNodes) {
        this.assignedNodes = assignedNodes;
    }

    public Set<Node> getAllAssignedNodes() {
        return this.assignedNodes;
    }

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
        if(obj instanceof CollectionServer){
            return this.name.equals(((CollectionServer) obj).getName());
        }
        return false;
    }
}
