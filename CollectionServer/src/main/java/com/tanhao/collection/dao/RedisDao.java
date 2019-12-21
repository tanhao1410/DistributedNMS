package com.tanhao.collection.dao;

import com.tanhao.bean.Alarm;
import com.tanhao.bean.CollectionServer;
import com.tanhao.bean.Node;
import com.tanhao.bean.WebServer;

import java.util.List;
import java.util.Set;

public interface RedisDao {

    /**
     * 保存服务器信息到指定存储
     */
    void saveCollectionServer(CollectionServer server);

    /**
     * 根据采集服务器名称获取采集服务器对象
     */
    CollectionServer getCollectionServerByName(String serverName);

    /**
     * 根据设备和监控项目从数据层获取对应的告警信息
     */
    Alarm getAlarmByNodeAndAlarmCollect(String nodeId, String alarmCollectName);

    /**
     * 获取每次监控轮询的时间间隔
     */
    int getIntervalTime();

    /**
     * 将告警发布到相应的队列中
     */
    void publishAlarm(Alarm alarm);

    public String get(final String key);

    List<WebServer> getWebServer();

    void saveWebServers(List<WebServer> webServers);

    void removeAlarmQueue(String name);
}
