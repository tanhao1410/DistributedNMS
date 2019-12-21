package com.tanhao.dao;

import com.tanhao.bean.Node;

import java.util.List;
import java.util.Set;


public interface NodeMapper {

	// 根据设备id查询设备信息
	public Node findNodeById(String id) throws Exception;

	// 更新设备
	public void updateNode(Node node) throws Exception;
	
	// 插入设备
	public void insertNode(Node node) throws Exception;

	//获取指定网络下的所有设备
	List<Node> selectByNetworkId(String networkId)throws Exception;

	//删除指定的设备
	void deleteNodeById(String id)throws Exception;

	//查找所有的设备
    Set<Node> selectAllNode()throws Exception;
}
