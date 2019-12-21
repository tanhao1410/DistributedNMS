package com.tanhao.collection.task;

import com.tanhao.bean.Node;
import java.util.HashSet;
import java.util.Set;

/**
 * 设备线程管理类
 */
public class NodeTaskSet {

    private static HashSet<NodeAlarmCollectThread> nodeTaskSet = new HashSet<>();

    public static void stopNotExistNodeThread(Set<Node> nodes){
        for(NodeAlarmCollectThread thread :nodeTaskSet){
            boolean needStop = true;
            for(Node node:nodes){
                if(thread.getNodeId().equals(node.getId())){
                    needStop = false;
                    break;
                }
            }
            ;
            //如果需要停止，则停止线程，并移除
            if(needStop) {
                nodeTaskSet.remove(thread);
                thread.destroy();
            }
        }
    }

    public static void startNodeThread(Set<Node> nodes) {
        //将没有开启线程的node开辟检测线程
        for(Node node:nodes){
            if(!NodeTaskSet.isNodeStartThread(node)){//还没有线程，说明是新分配的设备
                NodeAlarmCollectThread task =  new NodeAlarmCollectThread(node);
                System.out.println("创建了一个设备线程：");
                task.start();
                NodeTaskSet.addTask(task);
            }
        }
    }

    /**
     * 判断该节点是否已经存在对应的监测线程
     * @param node
     * @return
     */
    private static boolean isNodeStartThread(Node node){

        for(NodeAlarmCollectThread thread :nodeTaskSet){
            if(node.getId().equals(thread.getNodeId())){
                return true;
            }
        }
        return false;
    }

    private static void addTask(NodeAlarmCollectThread task) {
        NodeTaskSet.nodeTaskSet.add(task);
    }
}
