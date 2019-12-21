package com.tanhao.service;

import com.tanhao.bean.Network;

import java.util.List;

public interface NetworkService {

    Network getNetworkById(String networkId) throws Exception;

    Network createNetwork(Network network) throws Exception;

    void updateNetwork(Network network) throws Exception;

    List<Network> getAllNetwork() throws Exception;

    void saveLocation(String jsonParam) throws Exception;

    void deleteNetworkById(String id)throws Exception;
}
