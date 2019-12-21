package com.tanhao.collection.task;

import com.tanhao.bean.CollectionServer;
import com.tanhao.bean.Node;
import com.tanhao.bean.WebServer;
import com.tanhao.collection.dao.RedisDao;
import com.tanhao.collection.dao.DaoFactory;
import com.tanhao.constant.Constant;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 采集服务器任务线程类
 */
public class CollectionServerTask implements Runnable{

    private String serverName;

    public CollectionServerTask(String servname){
        this.serverName = servname;
    }

    @Override
    public void run() {

        //int sleepTime =Integer.parseInt(PropertiesUtil.getPropertiesByName("collectionServerTaskTimeInterval"));
        int sleepTime = Constant.SLEEP_TIME;
        RedisDao redisDao = DaoFactory.getRedisDao();

        while (true){

            //从注册中心得到本采集服务器所分配的所有设备
            CollectionServer server = redisDao.getCollectionServerByName(serverName);
            Set<Node> nodes = server.getAllAssignedNodes();

            //启动未启动监测线程的设备
            NodeTaskSet.startNodeThread(nodes);

            //停止不在的设备集中的线程
            NodeTaskSet.stopNotExistNodeThread(nodes);

            //更新采集服务器时间戳，表明存活
            CollectionServer collectionServer = redisDao.getCollectionServerByName(serverName);
            redisDao.saveCollectionServer(collectionServer);

            //检测是否有不在线的WebServer,如果有，从集合中删除它
            List<WebServer> webServers = redisDao.getWebServer();
            Iterator<WebServer> webServersI = webServers.iterator();
            while(webServersI.hasNext()){
                WebServer ws =  webServersI.next();
                if(System.currentTimeMillis() - ws.getTimeStampe() > Constant.MAX_NotUPDATE_TIME){//一分钟未报告，即认为掉线
                    //删除对应的队列
                    redisDao.removeAlarmQueue(ws.getName());
                    webServersI.remove();
                }
            }
            redisDao.saveWebServers(webServers);

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
