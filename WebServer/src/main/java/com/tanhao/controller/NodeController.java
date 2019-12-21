package com.tanhao.controller;

import com.alibaba.fastjson.JSONObject;
import com.tanhao.bean.ActionResult;
import com.tanhao.bean.Network;
import com.tanhao.bean.Node;
import com.tanhao.service.NodeService;
import com.tanhao.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequestMapping("/node")
@Controller
class NodeController {

    @Autowired
    NodeService nodeService;
    @Autowired
    RedisService redisService;

    /**
     * 获取所有的设备
     */
    @ResponseBody
    @RequestMapping( method = RequestMethod.GET)
    public ResponseEntity getAllNetwork(@RequestParam("networkId") String networkId) {

        ActionResult result = new ActionResult();
        try {
            List<Node> list = nodeService.getAllNetwork(networkId);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (Exception e) {
            result.setMsg(e.toString());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 删除指定设备
     */
    @ResponseBody
    @RequestMapping( "/delete")
    public ResponseEntity deleteNode(@RequestParam("id") String id) {

        ActionResult result = new ActionResult();
        try {
            nodeService.deleteNodeById(id);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            result.setMsg(e.toString());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 创建设备
     */
    @ResponseBody
    @RequestMapping( method = RequestMethod.POST)
    public ResponseEntity createNode(@RequestBody String json) {
        //把json串转换成对象
        Node node = JSONObject.parseObject(json,Node.class);
        ActionResult result = new ActionResult();
        try {
            Node node1 = nodeService.createNode(node);
            return ResponseEntity.status(HttpStatus.OK).body(node1);
        } catch (Exception e) {
            result.setMsg(e.toString());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 创建设备
     */
    @ResponseBody
    @RequestMapping("param")
    public ResponseEntity getNodeParam() {

        ActionResult result = new ActionResult();
        try {
            Map<String ,Integer> map = new HashMap();

            Set set =  nodeService.getAllNetwork();
            map.put("nodeNum",set.size());
            List list = redisService.getAllCollectionServer();
            map.put("colletionServerNum",list.size());

            return ResponseEntity.status(HttpStatus.OK).body(map);
        } catch (Exception e) {
            result.setMsg(e.toString());
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

}