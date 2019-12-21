var ws;
function webSocketInit(){
    ws = new WebSocket("ws://localhost:8080/websocket");
    ws.onopen = function(evn){
    };

    ws.onmessage = function(evn){
        var data  = $.parseJSON(evn.data);
        for(var i =0;i <data.length;i ++){
            var node = getNodeById(data[i].node.id);
            //setAlarm(node);
            setNodeAlarm(node,data[i]);
        }
    };

    ws.onclose = function(){
    };

};
