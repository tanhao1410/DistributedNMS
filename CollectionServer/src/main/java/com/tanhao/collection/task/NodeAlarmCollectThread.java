package com.tanhao.collection.task;

import com.tanhao.bean.Alarm;
import com.tanhao.bean.Node;
import com.tanhao.collection.dao.RedisDao;
import com.tanhao.collection.dao.DaoFactory;
import com.tanhao.collection.alarmcollect.AlarmCollect;
import com.tanhao.collection.alarmcollect.AlarmCollectManager;

import java.util.List;

/**
 * 设备检测线程，一个线程负责一个设备
 */
public class NodeAlarmCollectThread extends Thread {

    private String nodeId;
    private Node node;

    public String getNodeId(){
        return  this.nodeId;
    }
    /**
     * 设备任务线程，和设备绑定的，构造出来时，必须有设备
     * @param node
     */
    public NodeAlarmCollectThread(Node node){
        this.nodeId = node.getId();
        this.node = node;
    }

    @Override
    public void run() {

        //从redis中获取上一次的告警，已进行对比，是否发生改变
        RedisDao redisDao =  DaoFactory.getRedisDao();

        while (true){

            //获取每次监控轮询的时间间隔
            int intervalTime = redisDao.getIntervalTime();
            //本次监测开始时间
            long startTime = System.currentTimeMillis();

            //得到需要被监测的所有项目类
            List<AlarmCollect> list = AlarmCollectManager.list;
            for(AlarmCollect alarmCollect:list){

                Alarm alarm = alarmCollect.getAlarm(node);

                Alarm oldAlarm = redisDao.getAlarmByNodeAndAlarmCollect(node.getId(),alarmCollect.getAlarmCollectName());

                if(!alarm.equals(oldAlarm)){
                    //如果告警信息发生了改变，则通过Dao层将该告警发布到相应的队列
                    redisDao.publishAlarm(alarm);
                }

                //如果前一个告警认为可以不能继续下去
                if(!alarmCollect.isContinue(alarm)){
                    break;
                }

            }

            //本次监测结束时间
            long endTime = System.currentTimeMillis();

            //线程需要等待的时间
            long needSleepTime = intervalTime - (endTime - startTime);
            if(needSleepTime>0){
                try {
                    Thread.sleep(needSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
