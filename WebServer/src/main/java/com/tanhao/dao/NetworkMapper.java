package com.tanhao.dao;

import com.tanhao.bean.Network;

import java.util.List;


public interface NetworkMapper {

	// 根据网络id查询网络信息
	public Network findNetworkById(String id) throws Exception;

	// 更新网络
	public void updateNetwork(Network Network) throws Exception;
	
	// 插入网络
	public void insertNetwork(Network Network) throws Exception;

	//删除指定的网络
	void deleteNetworkById(String id)throws Exception;

    List<Network> getAllNetwork()throws Exception;

}
