var currentNetwork = "0";
var canvas ;
var stage;
var scene;
$(document).ready(function(){

    //按钮绑定事件
    buttonInit();

    //拓扑图初始化
    canvas = document.getElementById('canvas');
    stage = new JTopo.Stage(canvas);
    scene = new JTopo.Scene();
    stage.mode = "select";
    scene.mode = "select";
    scene.background = '../img/bg.jpg';
    stage.add(scene);

    //初始化网络和设备拓扑图
    initNetworkAndNode();

    //webSocketInit
    webSocketInit();

});
