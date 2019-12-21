package com.tanhao.collection.alarmcollect;


import com.tanhao.collection.utils.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

/**
 * 存放所有需要被采集的项目类，自动从配置文件中获取，并加入到系统中
 */
public class AlarmCollectManager {

    public static List<AlarmCollect> list = new ArrayList<>();

    //初始化采集的项目类
    static{
        int i = 1;
        String itemName = PropertiesUtil.getPropertiesByName("AlarmCollectItem."+i);
        while(null != itemName && (!itemName.isEmpty())){
            try {
                AlarmCollect alarmCollect = (AlarmCollect)Class.forName(itemName).newInstance();
                list.add(alarmCollect);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try{
                itemName = PropertiesUtil.getPropertiesByName("AlarmCollectItem."+(++i));
            }catch (MissingResourceException e){
                break;
            }

        }
    }

}
