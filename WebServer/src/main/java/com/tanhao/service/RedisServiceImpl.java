package com.tanhao.service;

import com.alibaba.fastjson.JSONObject;
import com.tanhao.bean.Alarm;
import com.tanhao.bean.CollectionServer;
import com.tanhao.bean.Node;
import com.tanhao.bean.WebServer;
import com.tanhao.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RedisServiceImpl implements RedisService{

    @Autowired//从Spring容器中查找bean，找到就注入，找不到就忽略
    private ShardedJedisPool shardedJedisPool;

    @Override
    public List<CollectionServer> getAllCollectionServer() throws Exception {
        //从服务器获取所有采集服务器的集合
        String allCollectionServers = get(Constant.COLLECTIONSERVER_SET_KEY);
        if(allCollectionServers == null || allCollectionServers.isEmpty()){
            return new ArrayList<>();
        }
        List<CollectionServer> list = JSONObject.parseArray(allCollectionServers, CollectionServer.class);
        return list;
    }

    @Override
    public void saveAllCollectionServer(List<CollectionServer> servers) throws Exception {
        String jsonString = JSONObject.toJSONString(servers);
        set(Constant.COLLECTIONSERVER_SET_KEY,jsonString);
    }

    @Override
    public void setWebServer(WebServer webServer) throws Exception {
        //首先获取所有的WebServer，查询自己是否在其中
        String webServersString = get(Constant.WEBSERVER_SET_KEY);
        List<WebServer> webServers =JSONObject.parseArray(webServersString,WebServer.class);

        webServers.add(webServer);
        String newWebServersString = JSONObject.toJSONString(webServers);
        set(Constant.WEBSERVER_SET_KEY,newWebServersString);
    }

    @Override
    public void updateWebServer(String webServerName) {
        //首先获取所有的WebServer，查询自己是否在其中
        String webServersString = get(Constant.WEBSERVER_SET_KEY);
        List<WebServer> webServers =JSONObject.parseArray(webServersString,WebServer.class);
        for(WebServer webServer:webServers){
            if(webServer.getName().equals(webServerName)){
                webServer.setTimeStampe(System.currentTimeMillis());
                //重新写入Redis
                String newWebServersString = JSONObject.toJSONString(webServers);
                set(Constant.WEBSERVER_SET_KEY,newWebServersString);
                return;
            }
        }
    }

    @Override
    public List<Alarm> getAlarmsByNodes(List<Node> nodes) throws Exception {

        List<Alarm> result = new ArrayList();
        for(Node node:nodes){

            final String nodeId = node.getId();
            //获取该节点的所有告警
            Map<String,String> alarmMap = this.execute(new Function<Map<String,String>, ShardedJedis>() {
                @Override
                public Map<String,String> callback(ShardedJedis e) {
                   return e.hgetAll(nodeId);
                }
            });

            Set<Map.Entry<String,String>> alarmEntrySet = alarmMap.entrySet();
            for(Map.Entry<String,String> entry:alarmEntrySet){
                Alarm alarm = JSONObject.parseObject(entry.getValue(),Alarm.class);
                result.add(alarm);
            }
        }

        return result;
    }

    //rpop即可出队
    @Override
    public List<Alarm> getQueueAlarm(final String webServerName) throws Exception {

        return this.execute(new Function<List<Alarm>, ShardedJedis>() {
            @Override
            public List<Alarm> callback(ShardedJedis e) {

                List<Alarm> result = new ArrayList<>();
                String s = e.rpop(webServerName);
                while(s != null){
                    Alarm alarm = JSONObject.parseObject(s,Alarm.class);
                    result.add(alarm);
                    s=e.rpop(webServerName);
                }
                return result;
            }
        });
    }

    private <T> T execute(Function<T, ShardedJedis> fun) {
        ShardedJedis shardedJedis = null;
        try {
            // 从连接池中获取到jedis分片对象
            shardedJedis = shardedJedisPool.getResource();

            return fun.callback(shardedJedis);
        } finally {
            if (null != shardedJedis) {
                // 关闭，检测连接是否有效，有效则放回到连接池中，无效则重置状态
                shardedJedis.close();
            }
        }
    }

    /**
     * 执行set操作
     *
     * @param key
     * @param value
     * @return
     */
    public String set(final String key, final String value) {
        return this.execute(new Function<String, ShardedJedis>() {
            @Override
            public String callback(ShardedJedis e) {
                return e.set(key, value);
            }
        });
    }

    /**
     * 执行get操作
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        return this.execute(new Function<String, ShardedJedis>() {
            @Override
            public String callback(ShardedJedis e) {
                return e.get(key);
            }
        });
    }

    /**
     * 执行删除操作
     *
     * @param key
     * @return
     */
    public Long del(final String key) {
        return this.execute(new Function<Long, ShardedJedis>() {
            @Override
            public Long callback(ShardedJedis e) {
                return e.del(key);
            }
        });
    }

    /**
     * 设置生存时间，单位为：秒
     *
     * @param key
     * @param seconds
     * @return
     */
    public Long expire(final String key, final Integer seconds) {
        return this.execute(new Function<Long, ShardedJedis>() {
            @Override
            public Long callback(ShardedJedis e) {
                return e.expire(key, seconds);
            }
        });
    }

    /**
     * 执行set操作并且设置生存时间，单位为：秒
     *
     * @param key
     * @param value
     * @return
     */
    public String set(final String key, final String value, final Integer seconds) {
        return this.execute(new Function<String, ShardedJedis>() {
            @Override
            public String callback(ShardedJedis e) {
                String str = e.set(key, value);
                e.expire(key, seconds);
                return str;
            }
        });
    }

    public interface Function<T, E> {

        public T callback(E e);

    }
}

