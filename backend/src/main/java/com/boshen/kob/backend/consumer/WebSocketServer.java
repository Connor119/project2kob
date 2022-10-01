package com.boshen.kob.backend.consumer;

//import com.kob.backend.mapper.UserMapper;
//import com.kob.backend.pojo.User;
import com.alibaba.fastjson2.JSONObject;
import com.boshen.kob.backend.consumer.utils.Game;
import com.boshen.kob.backend.consumer.utils.JwtAuthentication;
import com.boshen.kob.backend.mapper.UserMappper;
import com.boshen.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {

    final private static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();// final 修饰对象时只是引用不可变
//    开匹配池
    final private static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();

//    用来直到匹配到一起的user是谁
    private User user;
//    后端给前端发信息需要使用的
    private Session session = null;
//由于webSocket在spring中并不是单例的，所以我们可以使用static+一个set函数上写Autowired
    private static UserMappper userMapper;

//    当匹配成功的时候需要给前端返回地图所以在这里地图需要存起来
    private Game game= null;

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
//        System.out.println(userId);
        this.user = userMapper.selectById(userId);

        if(this.user != null) {
            users.put(userId, this);
        }
        else {
            this.session.close();
        }

        users.put(userId, this);
//        System.out.println(user);
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected");
//        当用户不在进行匹配断开连接了，我们需要将这个user从全局map中移除，保证之后的玩家不会匹配到已经退出的玩家
        if(this.user == null) {
            users.remove(this.user.getId());
            matchpool.remove(this.user);
        }
    }

    private void startMatching() {
        System.out.println("start matching");
        matchpool.add(this.user);
        while(matchpool.size() >= 2) {
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);

//临时创建一个地图用于测试
            Game game = new Game(13,14,20,a.getId(),b.getId());
            game.createMap();

//生成一个json，这个json包含所有的地图信息和玩家的位置信息，之后将这个信息放入我们后面的两个json中
            JSONObject respGame = new JSONObject();
            respGame.put("a_id",game.getPlayerA().getId());
            respGame.put("a_sx",game.getPlayerA().getSx());
            respGame.put("a_sy",game.getPlayerA().getSy());
            respGame.put("b_id",game.getPlayerB().getId());
            respGame.put("b_sx",game.getPlayerB().getSx());
            respGame.put("b_sy",game.getPlayerB().getSy());
            respGame.put("map",game.getG());

            JSONObject respA = new JSONObject();
            respA.put("event", "start-matching");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("game",respGame);
//            这里的含义是将匹配成功的消息发送给前端
            users.get(a.getId()).sendMessage(respA.toJSONString());

//临时创建一个地图用于测试，也把这个地图发送给B
            JSONObject respB = new JSONObject();
            respB.put("event", "start-matching");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("game",respGame);
//            同上（给b发送消息）
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    private void stopMatching() {
        System.out.println("stop macthing");
        matchpool.remove(this.user);
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.out.println("receive message");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event)){
//            当前端发送来的是需要进行匹配的话那么我们需要在startMatching函数中进行处理

            startMatching();

        }else if("stop-matching".equals(event)){
//            当是stop的时候也调用相关函数来处理

            stopMatching();
        }
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