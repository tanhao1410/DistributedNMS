package com.tanhao.service;

import com.tanhao.bean.Alarm;
import com.tanhao.bean.CollectionServer;
import com.tanhao.bean.Node;
import com.tanhao.bean.WebServer;

import java.util.List;

public interface RedisService {
    List<CollectionServer> getAllCollectionServer() throws Exception;

    void saveAllCollectionServer(List<CollectionServer> servers) throws Exception;

    void setWebServer(WebServer webServer) throws Exception;

    void updateWebServer(String webServerName)throws Exception;

    //根据设备从Redis中查询出所有的告警
    List<Alarm> getAlarmsByNodes(List<Node> nodes)throws Exception;

    List<Alarm> getQueueAlarm(String webServerName)throws Exception;
}
