package com.tanhao.constant;

/**
 * 一些公共变量的定义处
 */
public interface Constant {

    //所有采集服务器集合 的key
    public static String COLLECTIONSERVER_SET_KEY = "COLLECTIONSERVER_SET";

    //所有web服务器集合 的key
    public static String WEBSERVER_SET_KEY = "WEBSERVER_SET";

    //采集服务器轮询时间
    public static String INTERVAL_TIME_KEY = "INTERVAL_TIME";

    //轮询线程时间间隔
    public static int SLEEP_TIME = 1000;

    //超时时间，多长时间未向注册中心注册，算挂了
    public static int MAX_NotUPDATE_TIME = 30000;


}
