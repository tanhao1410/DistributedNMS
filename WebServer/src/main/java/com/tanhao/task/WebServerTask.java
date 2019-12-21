package com.tanhao.task;

import com.alibaba.fastjson.JSONObject;
import com.tanhao.bean.Alarm;
import com.tanhao.bean.CollectionServer;
import com.tanhao.bean.Node;
import com.tanhao.bean.WebServer;
import com.tanhao.constant.Constant;
import com.tanhao.service.NodeService;
import com.tanhao.service.NodeServiceImpl;
import com.tanhao.service.RedisService;
import com.tanhao.service.RedisServiceImpl;
import com.tanhao.websocket.WebSocketHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.socket.TextMessage;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 监控线程：
 * 1.每隔一小段时间从Redis中读出采集服务器上传的CollectionServer信息，判断有无采集服务器宕机，
 * 若出现，将其下面分配的设备重新分配给存活的CollectionServer
 * 2.判断是否存在严重失衡的任务分配，如果有，减少压力大的CollectionServer,增加至压力小的，应尽量避免
 * 重新分配
 * 3.
 */
public class WebServerTask implements Runnable,ServletContextListener{

    @Override
    public void run() {
        int sleepTime = Constant.SLEEP_TIME;//2s
        while (true) {
            try {

                //获取系统中所有的需要被检测的设备
                if(WebServerTask.NODE_UPDATE_FLAG){
                    this.setNodesCache(nodeService.getAllNetwork());
                    WebServerTask.NODE_UPDATE_FLAG = false;
                }
                Set<Node> nodes = this.getNodesCache();
                //简单缓存系统；实现方案：放置一个flag，系统对设备有修改的话，改变该flag TODO
                // 不必每次都从数据库获取了
                //其它方式：1.放到redis,每次从redis中读取
                //2.

                //获取所有的采集服务器
                List<CollectionServer> servers = redisService.getAllCollectionServer();

                //从采集服务器集中删除掉 长时间未上线的 采集服务器
                long nowTimeStampe = System.currentTimeMillis();
                Set<Node> needAssign = new HashSet<>();

                Iterator<CollectionServer> iterator = servers.iterator();
                while (iterator.hasNext()) {
                    CollectionServer cs = iterator.next();
                    nodes.removeAll(cs.getAllAssignedNodes());//去掉正常的被监控的设备
                    if ((nowTimeStampe - cs.getTimeStampe()) > 60 * 1000) {//一分钟未上线认为掉线
                        needAssign.addAll(cs.getAllAssignedNodes());//掉线的采集服务器上原来分配的设备集合
                    }
                }

                needAssign.addAll(nodes);
                //将需要被分配的设备平均分给余下的 采集服务器
                int needAssigedNum = needAssign.size();
                Iterator<Node> needAssignIterator = needAssign.iterator();
                int i = 0;
                while (needAssignIterator.hasNext()) {
                    Node needAssignNode = needAssignIterator.next();
                    servers.get(i).getAllAssignedNodes().add(needAssignNode);//添加到另一个采集服务器上
                    needAssignIterator.remove();
                    i = (++i)%servers.size();
                }

                //调整失衡的采集服务器，将任务量最大的分担给 任务量最小的，若差值小于2 ，则不进行任何改动。
                int minIndex=0,minNum=Integer.MAX_VALUE,maxIndex=0,maxNum=0,j=0;
                for(CollectionServer cs :servers){
                    int size = cs.getAllAssignedNodes().size();
                    if(size>maxNum) {
                        maxNum = size;
                        maxIndex = j;
                    }else if(size <minNum){
                        minNum = size;
                        minIndex = j;
                    }
                    j++;
                }

                j=1;
                int between = (maxNum - minNum) < 10?(maxNum - minNum):10;//重组时，最大不超过五个设备
                if(between>1){
                    //重组分配
                    Set<Node> more =  servers.get(maxIndex).getAllAssignedNodes();
                    Set<Node> less =  servers.get(minIndex).getAllAssignedNodes();

                    Iterator<Node> moreIterator = more.iterator();
                    while(moreIterator.hasNext()){
                        Node ajust = moreIterator.next();
                        less.add(ajust);
                        moreIterator.remove();
                        if(j > between/2){
                            break;
                        }
                        j++;
                    }
                }

                //将调整后的结果上传至redis中，各采集服务器将按此分配进行采集任务
                redisService.saveAllCollectionServer(servers);

                //更新自己，表明自己存活
                redisService.updateWebServer(this.webServerName);

                //websocket任务，从队列中读取数据，然后返回给前台
                List<Alarm> list = redisService.getQueueAlarm(this.webServerName);
                if(list.size() > 0){
                    TextMessage message = new TextMessage(JSONObject.toJSONString(list));
                    webSocketHandler.sendMessageToUsers(message);
                }

                Thread.sleep(sleepTime);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
    public WebServerTask(){}

    public WebServerTask(RedisService redisService, NodeService nodeService, WebSocketHandler webSocketHandler,String webServerName){
        this.redisService = redisService;
        this.nodeService = nodeService;
        this.webServerName = webServerName;
        this.webSocketHandler = webSocketHandler;
    }

    private RedisService redisService;

    private NodeService nodeService;

    private String webServerName;

    private WebSocketHandler webSocketHandler;
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContextEvent.getServletContext());
        redisService = context.getBean(RedisServiceImpl.class);
        nodeService = context.getBean(NodeServiceImpl.class);
        webSocketHandler = context.getBean(WebSocketHandler.class);


        //在Redis中注册，表示WebServer上线即生成一个队列
        WebServer webServer = new WebServer();
        String webServerName = nodeService.getWebserverName();
        webServer.setName(webServerName);
        webServer.setTimeStampe(System.currentTimeMillis());
        try{
            redisService.setWebServer(webServer);
        }catch (Exception e){
            e.printStackTrace();
        }

        new Thread(new WebServerTask(redisService,nodeService,webSocketHandler,webServerName)).start();
    }

    public static boolean NODE_UPDATE_FLAG = true;

    public Set<Node> getNodesCache() {
        return nodesCache;
    }

    public void setNodesCache(Set<Node> nodesCache) {
        this.nodesCache = nodesCache;
    }

    Set<Node> nodesCache;

}
