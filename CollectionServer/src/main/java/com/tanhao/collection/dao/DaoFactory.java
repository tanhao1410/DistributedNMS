package com.tanhao.collection.dao;

public class DaoFactory {

    private static RedisDao redisDao;

    /**
     * 懒汉单例模式
     */
    private DaoFactory(){

    }

    public static RedisDao getRedisDao(){

        if(null == redisDao){
            //防止并发下创建多个，双重判断
            synchronized (DaoFactory.class){
                if(null == redisDao) redisDao = new RedisDaoImpl();
            }

        }
        return redisDao;
    }
}
