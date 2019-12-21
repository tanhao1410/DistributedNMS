package com.tanhao.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tanhao.bean.Network;
import com.tanhao.bean.Node;
import com.tanhao.dao.NetworkMapper;
import com.tanhao.dao.NodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NetworkServiceImpl implements NetworkService {

    @Autowired
    private NetworkMapper networkMapper;

    @Autowired
    private NodeMapper nodeMapper;

    @Override
    public Network getNetworkById(String networkId) throws Exception {
        return networkMapper.findNetworkById(networkId);
    }

    @Override
    public Network createNetwork(Network network) throws Exception {
        //生成主键
        String id = UUID.randomUUID().toString();
        network.setId(id);
        networkMapper.insertNetwork(network);
        return network;
    }

    @Override
    public void updateNetwork(Network network) throws Exception {

    }

    @Override
    public List<Network> getAllNetwork() throws Exception {
        return networkMapper.getAllNetwork();
    }

    @Override
    public void saveLocation(String jsonParam) throws Exception {
        JSONArray jsonArray = JSONObject.parseArray(jsonParam);
        for(int i = 0;i < jsonArray.size();i ++){
           JSONObject obj =  jsonArray.getJSONObject(i);
           if("node".equals(obj.getString("type"))){
               //说明要保存的节点是设备节点
               Node node = nodeMapper.findNodeById(obj.getString("id"));
               node.setLocationX(obj.getInteger("x"));
               node.setLocationY(obj.getInteger("y"));
               nodeMapper.updateNode(node);
           }else{
               Network network = networkMapper.findNetworkById(obj.getString("id"));
               network.setLocationX(obj.getInteger("x"));
               network.setLocationY(obj.getInteger("y"));

               networkMapper.updateNetwork(network);
           }
        }
    }

    @Override
    public void deleteNetworkById(String id) throws Exception {
        networkMapper.deleteNetworkById(id);
    }
}
