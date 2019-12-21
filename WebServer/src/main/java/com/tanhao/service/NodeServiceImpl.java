package com.tanhao.service;

import com.tanhao.bean.Node;
import com.tanhao.dao.NodeMapper;
import com.tanhao.task.WebServerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class NodeServiceImpl implements NodeService {

    @Autowired
    private NodeMapper nodeMapper;

    @Value("${webserver}")
    private String webserverName;

    public String getWebserverName(){
        return this.webserverName;
    }

    @Override
    public void deleteNodeById(String id) throws Exception {

        //表明修改过设备
        WebServerTask.NODE_UPDATE_FLAG = true;
        nodeMapper.deleteNodeById(id);
    }

    @Override
    public List<Node> getAllNetwork(String networkId) throws Exception {
        return nodeMapper.selectByNetworkId(networkId);
    }

    @Override
    public Node createNode(Node node) throws Exception {
        //表明修改过设备
        WebServerTask.NODE_UPDATE_FLAG = true;
        //生成主键
        String id = UUID.randomUUID().toString();
        node.setId(id);
        nodeMapper.insertNode(node);
        return node;
    }

    @Override
    public Set<Node> getAllNetwork() throws Exception {
        return nodeMapper.selectAllNode();
    }
}
