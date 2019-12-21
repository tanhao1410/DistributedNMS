package com.tanhao.collection;

import com.tanhao.bean.CollectionServer;
import com.tanhao.collection.dao.RedisDao;
import com.tanhao.collection.dao.DaoFactory;
import com.tanhao.collection.task.CollectionServerTask;

/**
 * 采集服务器的启动类
 */
public class Main {

    public static void main(String[] args) {

        if (args.length < 1) {
            //提示用法采集服务器启动方式为：后面加一个命令行启动参数即代表本采集服务器名称
            return;
        }

        //与redis数据库进行交互的DAO
        RedisDao redisDao = DaoFactory.getRedisDao();

        //向注册中心注册
        CollectionServer server = new CollectionServer(args[0]);
        redisDao.saveCollectionServer(server);

        //开启采集服务器任务线程
        new Thread(new CollectionServerTask(args[0])).start();
    }
}
