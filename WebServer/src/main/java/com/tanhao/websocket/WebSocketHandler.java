package com.tanhao.websocket;

import java.io.IOException;
import java.util.ArrayList;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandler extends TextWebSocketHandler {
    private static final ArrayList<WebSocketSession> users;//这个会出现性能问题，最好用Map来存储，key用userid
    static {
        users = new ArrayList<WebSocketSession>();
    }
    
    public WebSocketHandler() {
    }

    /**
     * 连接成功时候，会触发页面上onopen方法
     */
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        users.add(session);
        System.out.println("websocket用户加入，总:"+users.size());

    }
    
    /**
     * 关闭连接时触发
     */
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        users.remove(session);
        System.out.println("websocket用户退出，总:"+users.size());
    }

    /**
     * js调用websocket.send时候，会调用该方法
     */
    @Override    
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        users.remove(session);
    }

    /**
     * 给所有在线用户发送消息
     */
    public void sendMessageToUsers(TextMessage message) {
        for (WebSocketSession user : users) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean supportsPartialMessages() {
        return false;
    }
    


}