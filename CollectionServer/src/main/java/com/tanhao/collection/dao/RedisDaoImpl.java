package com.tanhao.collection.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tanhao.bean.Alarm;
import com.tanhao.bean.CollectionServer;

import java.util.ArrayList;
import java.util.List;

import com.tanhao.bean.WebServer;
import com.tanhao.constant.Constant;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class RedisDaoImpl implements RedisDao {

    private ShardedJedisPool shardedJedisPool;

    public RedisDaoImpl() {

        // 构建连接池配置信息
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 设置最大连接数
        poolConfig.setMaxTotal(50);

        // 定义集群信息
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        shards.add(new JedisShardInfo("127.0.0.1", 6379));
        //shards.add(new JedisShardInfo("192.168.29.112", 6379));

        // 定义集群连接池
        shardedJedisPool = new ShardedJedisPool(poolConfig, shards);

    }



    @Override
    public void saveCollectionServer(CollectionServer server) {
        //从服务器获取所有采集服务器的集合
        String allCollectionServers = get(Constant.COLLECTIONSERVER_SET_KEY);
        //数据格式：
        // [{"allAssignedNodes":[{"port":0},{"id":"2222中文","port":0}],"name":"bijc","timeStampe":1576585030271},...]
        //从中找到自己
        List<CollectionServer> list = JSONObject.parseArray(allCollectionServers, CollectionServer.class);

        if(list == null){
            list = new ArrayList();
        }
        int i = 0;
        CollectionServer cs = null;
        for (i = 0; i < list.size(); i++) {
            //名称相同即找到了
            if (list.get(i).getName().equals(server.getName())) {
                cs = list.get(i);
                break;
            }
        }

        server.setTimeStampe(System.currentTimeMillis());
        //没有找到
        if(cs == null){
            list.add(server);
        }else{
            list.set(i,server);
            //更新server
        }

        //提交
        setKeyValue(Constant.COLLECTIONSERVER_SET_KEY, JSON.toJSONString(list));
    }

    @Override
    public CollectionServer getCollectionServerByName(String serverName) {
        //从服务器获取所有采集服务器的集合
        String allCollectionServers = get(Constant.COLLECTIONSERVER_SET_KEY);
        //从中找到自己
        List<CollectionServer> list = JSONObject.parseArray(allCollectionServers, CollectionServer.class);

        int i = 0;
        for (i = 0; i < list.size(); i++) {
            //名称相同即找到了
            if (list.get(i).getName().equals(serverName)) {
                return list.get(i);
            }
        }
        return null;
    }

    @Override
    public Alarm getAlarmByNodeAndAlarmCollect(final String nodeId, final String alarmCollectName) {
        //从Redis中获取指定节点的告警信息，告警用redis的hash结构存储。
        // 告警的key设定的为：node的主键，field为监控的项目名称
        String alarmString = this.execute(new Function<String, ShardedJedis>() {
            @Override
            public String callback(ShardedJedis e) {
                return  e.hget(nodeId,alarmCollectName);
            }
        });

        return JSONObject.parseObject(alarmString,Alarm.class);
    }
    @Override
    public void publishAlarm(Alarm alarm) {

        final String alarmInfo = JSONObject.toJSONString(alarm);

        //获取所有的web服务器
        List<WebServer> webServers = getWebServer();
        //往队列中添加告警信息
        for(WebServer webServer:webServers){
            pushQueue(webServer.getName(),alarmInfo);
        }

        //将告警存放到Redis中
        //从Redis中获取指定节点的告警信息，告警用redis的hash结构存储。
        // 告警的key设定的为：node的主键，field为监控的项目名称
        final String nodeId = alarm.getNode().getId();
        final String alarmCollectName = alarm.getCollectItemName();
        this.execute(new Function<Long, ShardedJedis>() {
            @Override
            public Long callback(ShardedJedis e) {
                return  e.hset(nodeId,alarmCollectName,alarmInfo);
            }
        });
    }

    @Override
    public int getIntervalTime() {
        String time = get(Constant.INTERVAL_TIME_KEY);
        if(time == null ||time.isEmpty()){
            time = String.valueOf(5000);
            setKeyValue(Constant.INTERVAL_TIME_KEY,"5000");
        }
        return Integer.parseInt(time);
    }

    /**
     * 执行redis的进队操作操作,rpop即可出队
     */
    public Long pushQueue(final String queueName,final String info) {
        return this.execute(new Function<Long,ShardedJedis>() {
            @Override
            public Long callback(ShardedJedis e) {
                return e.lpush(queueName,info);
            }
        });
    }


    /**
     * 执行get操作
     */
    public String get(final String key) {
        return this.execute(new Function<String, ShardedJedis>() {
            @Override
            public String callback(ShardedJedis e) {
                return e.get(key);
            }
        });
    }

    @Override
    public List<WebServer> getWebServer() {
        String webServersString = get(Constant.WEBSERVER_SET_KEY);
        List<WebServer> webServers = JSONObject.parseArray(webServersString, WebServer.class);
        if(webServers == null){
            return new ArrayList();
        }
        return webServers;
    }

    @Override
    public void saveWebServers(List<WebServer> webServers) {
        String webServersString = JSONObject.toJSONString(webServers);
        setKeyValue(Constant.WEBSERVER_SET_KEY,webServersString);
    }

    @Override
    public void removeAlarmQueue(final String name) {
         this.execute(new Function<String, ShardedJedis>() {
            @Override
            public String callback(ShardedJedis e) {
                e.del(name);
                return null;
            }
        });
    }

    /**
     * 执行set操作
     */
    public String setKeyValue(final String key, final String value) {
        return this.execute(new Function<String, ShardedJedis>() {
            @Override
            public String callback(ShardedJedis e) {
                return e.set(key, value);
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
     * 回调方法
     */
    interface Function<T, E> {
        public T callback(E e);
    }
}
