package com.boshen.kob.backend.consumer;

//import com.kob.backend.mapper.UserMapper;
//import com.kob.backend.pojo.User;
import com.boshen.kob.backend.consumer.utils.JwtAuthentication;
import com.boshen.kob.backend.mapper.UserMappper;
import com.boshen.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    private static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
//    用来直到匹配到一起的user是谁
    private User user;
//    后端给前端发信息需要使用的
    private Session session = null;
//由于webSocket在spring中并不是单例的，所以我们可以使用static+一个set函数上写Autowired
    private static UserMappper userMapper;

    @Autowired
    public void setUserMapper(UserMappper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

//    当连接建立的时候会调用
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
//        从session中获取到我们的user，之后放入全局map中
        this.session = session;
        System.out.println("connected");
        Integer userId = JwtAuthentication.getUserId(token);
        System.out.println(userId);
        this.user = userMapper.selectById(userId);

        if(this.user != null) {
            users.put(userId, this);
        }
        else {
            this.session.close();
        }

        users.put(userId, this);
        System.out.println(user);
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected");
//        当用户不在进行匹配断开连接了，我们需要将这个user从全局map中移除，保证之后的玩家不会匹配到已经退出的玩家
        if(this.user == null) {
            users.remove(this.user.getId());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.out.println("receive message");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

//    自己写的后端给前端写消息的函数
    public void sendMessage(String message) {
//        多个前端设备都会去发消息，一次只能允许一个用户进行消息的发送，所以需要加锁
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}