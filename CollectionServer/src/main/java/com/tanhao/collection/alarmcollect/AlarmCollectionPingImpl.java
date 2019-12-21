package com.tanhao.collection.alarmcollect;

import com.tanhao.bean.Alarm;
import com.tanhao.bean.Node;

import java.net.InetAddress;

public class AlarmCollectionPingImpl implements AlarmCollect {
    @Override
    public String getAlarmCollectName() {
        return "ping";
    }

    @Override
    public Alarm getAlarm(Node node) {

        Alarm alarm = new Alarm();
        alarm.setCollectItemName(getAlarmCollectName());
        alarm.setNode(node);

        boolean status = false;
        try {
            int timeOut = 1000;
            status = InetAddress.getByName(node.getIp()).isReachable(timeOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!status){
            alarm.setContent("未连接，设备掉线");
            alarm.setLevel(1);
        }else{
            alarm.setContent("设备在线");
            alarm.setLevel(0);
        }

        return alarm;
    }

    @Override
    public boolean isContinue(Alarm alarm) {

        if (alarm.getLevel() > 0) {
            return false;
        }
        return true;
    }
}
