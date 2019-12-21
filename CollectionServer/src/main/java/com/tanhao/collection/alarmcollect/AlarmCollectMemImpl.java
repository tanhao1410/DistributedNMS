package com.tanhao.collection.alarmcollect;
import com.tanhao.bean.Alarm;
import com.tanhao.bean.Node;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.util.Vector;

public class AlarmCollectMemImpl implements AlarmCollect {

    protected String getSingleValueByOid(Node node,String oid){

        String ipAddress = node.getIp();
        short port = node.getPort();
        //目标机器地址，SNMP默认端口号为161
        Address targetAddress = GenericAddress.parse("udp:"+ipAddress +"/"+port);

        try{
            //构建SNMP对象，该对象中有用于SNMP的相关方法
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            transport.listen();

            CommunityTarget target = new CommunityTarget();
            //在Linux机器上配置好的团体字
            target.setCommunity(new OctetString("SA19225338"));

            //设置基本信息
            target.setAddress(targetAddress);
            target.setRetries(2);//重试次数
            target.setTimeout(1500);//超时时间
            target.setVersion(SnmpConstants.version2c);//SNMP版本

            PDU pdu = new PDU();
            int[] oids = spitStringOID2IntArray(oid);
            pdu.add(new VariableBinding(new OID(oids)));
            pdu.setType(PDU.GET);
            ResponseEvent event = snmp.send(pdu, target);

            if (event != null && event.getResponse() != null) {

                //得到返回信息的集合
                Vector<VariableBinding> recVBs = (Vector<VariableBinding>) event.getResponse().getVariableBindings();

                if(recVBs.size() > 0){
                    return String.valueOf(recVBs.elementAt(0).getVariable());
                }
            }

            snmp.close();
        }catch (Exception e){

        }
        return null;
    }

    private int[] spitStringOID2IntArray(String oid){
        String [] oids = oid.split("\\.");
        int[] intOids = new int[oids.length];

        for(int i =0;i < oids.length;i ++){
            intOids[i] = Integer.parseInt(oids[i]);
        }
        return intOids;
    }

    @Override
    public String getAlarmCollectName() {
        return null;
    }

    @Override
    public Alarm getAlarm(Node node) {
        return null;
    }

    @Override
    public boolean isContinue(Alarm alarm) {
        return false;
    }
}
