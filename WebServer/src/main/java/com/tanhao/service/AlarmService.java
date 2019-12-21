package com.tanhao.service;

import com.tanhao.bean.Alarm;

import java.util.List;

public interface AlarmService {
    List<Alarm> getAllAlarm(String networkId) throws Exception;
}
