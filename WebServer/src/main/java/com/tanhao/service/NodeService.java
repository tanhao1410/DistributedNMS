package com.tanhao.service;

import com.tanhao.bean.Network;
import com.tanhao.bean.Node;

import java.util.List;
import java.util.Set;

public interface NodeService {

    List<Node> getAllNetwork(String networkId) throws Exception;

    Node createNode(Node node) throws Exception;

    Set<Node> getAllNetwork() throws Exception;

    String getWebserverName();

    void deleteNodeById(String id)throws Exception;
}
