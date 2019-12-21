function buttonInit(){
    //增加网络
    $("#createNetworkButton")[0].onclick = function(){

        var param = {
            id:"",
            name:$("#networkName").val(),
            locationX:Math.floor(Math.random()*1000+10),
            locationY:Math.floor(Math.random()*300+10),
        };
        console.info(JSON.stringify(param))
        $.ajax({
            type: "POST",
            dataType: "json",
            url: "/network",
            data: JSON.stringify(param),
            contentType:"application/json;charset=utf-8",
            success: function(data) {
                scene.add(createNetwork(data))
                $("#myModal").modal("hide");
            },
            error: function(data) {
                console.info(data)
                return;
            }
        });
    }

    $("#addNetworkButton")[0].onclick = function(){
        $("#myModal").modal("show");
    }

    $("#addNodeButton")[0].onclick = function(){
        $("#addNodeModal").modal("show");
    }

    $("#createNodeButton")[0].onclick = function(){
        var param = {
            id:"",
            name:$("#nodeName").val(),
            locationX:Math.floor(Math.random()*1000),
            locationY:Math.floor(Math.random()*500),
            ip:$("#nodeIp").val(),
            password:$("#passwordInput").val(),
            networkId:currentNetwork,
        };
        $.ajax({
            type: "POST",
            dataType: "json",
            url: "/node",
            data: JSON.stringify(param),
            contentType:"application/json;charset=utf-8",
            success: function(data) {
                scene.add(createNode(data))
                $("#addNodeModal").modal("hide");
            },
            error: function(data) {
                console.info(data)
            }
        });
    }

    $("#returnRootNetworkButton")[0].onclick = function(){
        initNetworkAndNode();
    }


    $("#saveLocationButton")[0].onclick = function(){
        var params= [];
        //获取当前scene下的所有节点
        var nodes = scene.childs;

        for(var i = 0;i < nodes.length;i++){
            var node = nodes[i];
            var param = {
                id:node.id,
                type:node.type,
                x:node.x,
                y:node.y
            };
            params.push(param);
        }
        //更新位置信息
        //格式：[{},{}]
        $.ajax({
            type: "POST",
            dataType: "json",
            url: "/network/saveLocation",
            data: JSON.stringify(params),
            contentType:"application/json;charset=utf-8",
            success: function(data) {
                //alert("保存成功！");
            },
            error: function(data) {
                console.info(data)
            }
        });
    }

    $("#deleteButton")[0].onclick = function(){
        var eles = scene.selectedElements;
        if(eles.length != 1){
            alert("请单击选择一个要删除的网络或设备！")
            return;
        }

        var url ;
        if(eles[0].type == "network"){
            url = "/network/delete";
        }else{
            url = "/node/delete";
        }
        $.ajax({
            type : "GET",
            dataType : "json",
            url :url ,
            data : "id="+eles[0].id,
            success : function(dataResult, textStatus) {
                scene.remove(scene.selectedElements[0]);
            },
            error : function(data) {
                console.info(data);
            }
        });
    }


}
