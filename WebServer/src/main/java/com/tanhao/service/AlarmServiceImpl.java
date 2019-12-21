package com.tanhao.service;

import com.tanhao.bean.Alarm;
import com.tanhao.bean.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    RedisService redisService;

    @Autowired
    NodeService nodeService;

    @Override
    public List<Alarm> getAllAlarm(String networkId) throws Exception {

        //根据networkID获知所有的设备
        List<Node> nodes = nodeService.getAllNetwork(networkId);
        //根据设备从Redis中查询出所有的告警
        return redisService.getAlarmsByNodes(nodes);
    }
}
