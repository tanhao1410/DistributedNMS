package com.tanhao.controller;

import com.tanhao.bean.ActionResult;
import com.tanhao.bean.Alarm;
import com.tanhao.bean.Node;
import com.tanhao.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping("/alarm")
@Controller
public class AlarmController {

    @Autowired
    AlarmService alarmService;

    /**
     * 获取所有的告警
     */
    @ResponseBody
    @RequestMapping( method = RequestMethod.GET)
    public ResponseEntity getAllNetwork(@RequestParam("networkId") String networkId) {

        ActionResult result = new ActionResult();
        try {
            List<Alarm> list = alarmService.getAllAlarm(networkId);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            result.setMsg(e.toString());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }


}
