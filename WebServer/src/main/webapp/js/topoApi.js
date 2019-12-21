var topoApi = {
    createNetwork:createNetwork
}

function createNetwork(data){
    var node = new JTopo.Node(data.name);
    node.setImage('../img/group.png', true);
    node.setLocation(parseInt(data.locationX), parseInt(data.locationY));
    node.id = data.id;
    node.type = "network";
    node.addEventListener("dbclick",networkDBclick);
    return node;
}

function createNode(data){
    var node = new JTopo.Node(data.name);
    node.setImage('../img/server.png', true);
    node.setLocation(parseInt(data.locationX), parseInt(data.locationY));
    node.id = data.id;
    node.type = "node";
    return node;
}

function getNodeById(id){
    var nodes = scene.childs;
    for(var i = 0;i < nodes.length;i ++){
        if(nodes[i].id == id){
            return nodes[i];
        }
    }
}

function setNodeAlarm(node,data){

    if(data.level == 0){
        node.alarm = null;
        node.alarmInfo=data;
    }else{
        node.alarm = data.content;
        node.alarmInfo = data;
    }

}

function initNetworkAndNode(){


    //菜单隐藏和调整
    $("#returnRootNetworkButton").hide();
    $("#addNetworkButton").show();

    //清空拓扑图，以备重新绘制
    scene.clear();
    currentNetwork = "0";
    //获取所有的网络
    $.ajax({
        type : "GET",
        dataType : "json",
        url : "/network",
        data : "",
        success : function(dataResult, textStatus) {
            for(var i =0;i <dataResult.length;i ++){
                var node = createNetwork(dataResult[i]);
                scene.add(node);
            }
        },
        error : function(data) {
            console.info(data);
        }
    });

    //获取根下面的所有的设备
    $.ajax({
        type : "GET",
        dataType : "json",
        url : "/node",
        data : "networkId=0",
        success : function(data) {
            for(var i =0;i <data.length;i ++){
                var node = createNode(data[i]);
                scene.add(node);
            }

            //展示告警
            $.ajax({
                type : "GET",
                dataType : "json",
                url : "/alarm",
                data : "networkId=0",
                success : function(data) {
                    for(var i =0;i <data.length;i ++){

                        var node = getNodeById(data[i].node.id);
                        //setAlarm(node);
                        if(data[i].level > 0){

                            setNodeAlarm(node,data[i]);

                        }
                    }
                },
                error : function(data) {
                    console.info(data);
                }
            });


        },
        error : function(data) {
            console.info(data);
        }
    });

}

function networkDBclick(event){



    var networkId = event.target.id;

    currentNetwork =networkId;
    //清空拓扑图，以备重新绘制
    scene.clear();
    //获取根下面的所有的设备
    $.ajax({
        type : "GET",
        dataType : "json",
        url : "/node",
        data : "networkId="+networkId,
        success : function(data) {
            for(var i =0;i <data.length;i ++){
                var node = createNode(data[i]);
                scene.add(node);
            }
            $("#returnRootNetworkButton").show();
            $("#addNetworkButton").hide();
        },
        error : function(data) {
            console.info(data);
        }
    });

}