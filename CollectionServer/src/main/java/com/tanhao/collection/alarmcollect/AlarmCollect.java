package com.tanhao.collection.alarmcollect;

import com.tanhao.bean.Alarm;
import com.tanhao.bean.Node;

/**
 * 设备信息采集接口
 */
public interface AlarmCollect {

    /**
     * 获取当前监测项目的名称
     * @return
     */
    String getAlarmCollectName();

    /**
     * 获取node节点的该监测项目的告警信息
     * @param node
     * @return
     */
    Alarm getAlarm(Node node);

    /**
     * 根据当前的告警，来决定是否进行接下来的告警项目的获取；比如，如果ping不同，连接失败，再去获取CPU等信息毫无意义
     * @return
     */
    boolean isContinue(Alarm alarm);

}
