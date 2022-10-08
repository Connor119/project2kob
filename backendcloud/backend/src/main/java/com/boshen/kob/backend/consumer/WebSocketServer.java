package com.boshen.kob.backend.consumer;

//import com.kob.backend.mapper.UserMapper;
//import com.kob.backend.pojo.User;
import com.alibaba.fastjson2.JSONObject;
import com.boshen.kob.backend.consumer.utils.Game;
import com.boshen.kob.backend.consumer.utils.JwtAuthentication;
import com.boshen.kob.backend.mapper.BotMapper;
import com.boshen.kob.backend.mapper.RecordMapper;
import com.boshen.kob.backend.mapper.UserMappper;
import com.boshen.kob.backend.pojo.Bot;
import com.boshen.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();// final 修饰对象时只是引用不可变
//    final private static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();
    private User user;
    private Session session = null;

    private static UserMappper userMapper;
    public static RecordMapper recordMapper;
//从数据库中取出来东西
    private static BotMapper botMapper;

    private Game game = null;

    private final static String addPlayerUrl = "http://127.0.0.1:3006/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3006/player/remove/";

    public static RestTemplate restTemplate;

    @Autowired
    public void setUserMapper(UserMappper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper)  { WebSocketServer.recordMapper = recordMapper; }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate = restTemplate;
    }

    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        System.out.println("connected");
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if(this.user != null) {
            users.put(userId, this);
        }
        else {
            this.session.close();
        }
        users.put(userId, this);
        System.out.println(users);
    }
    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected");
        if(this.user != null) {
            users.remove(this.user.getId());
        }
    }

//这里需要在接收到匹配系统传递过来的消息之后进行调用，（需要在StartGameServiceImpl中进行调用所以应该变为public的）
    public static void startGame(Integer aId,Integer aBotId ,Integer bId,Integer bBotId) {
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId), botB = botMapper.selectById(bBotId);
        Game game = new Game(
                13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB
        );
        game.createMap();
        if(users.get(a.getId()) != null)
            users.get(a.getId()).game = game;
        if(users.get(b.getId()) != null)
            users.get(b.getId()).game = game;

        game.start();

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);
        if(users.get(a.getId()) != null){
            users.get(a.getId()).sendMessage(respA.toJSONString());}

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        if(users.get(b.getId()) != null){
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }


//在这里向matchingServicer发送http请求
//    向后端发请求需要用到restTemplate
    private void startMatching(Integer botId) {
        System.out.println("start matching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
//        增加了一个机器人，所以这里还要增加发一个botid
        data.add("bot_id",botId.toString());
//        restTemplate的作用是发送请求（参数1：url,参数2：请求携带的参数，参数3：返回值的类型）
        restTemplate.postForObject(addPlayerUrl, data, String.class);

    }


    private void stopMatching() {
        System.out.println("stop macthing");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);

    }
    private void move(int direction) {
        if(game.getPlayerA().getId().equals(user.getId())) {
//            game.setNextStepA(direction);
            if(game.getPlayerA().getBotId().equals(-1)) // 人工操作
                game.setNextStepA(direction);

        } else if(game.getPlayerB().getId().equals(user.getId())) {
//            game.setNextStepB(direction);
            if(game.getPlayerB().getBotId().equals(-1))
                game.setNextStepB(direction);
        }
    }
    @OnMessage
    public void onMessage(String message, Session session) { // 当作路由
        // 从Client接收消息
        System.out.println("receive message");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) { // revent为空时，.equals 可能报错，所以用"start-matching".equals(event)
            startMatching(data.getInteger("bot_id"));
        } else if("stop-matching".equals(event)) {
            stopMatching();
        } else if("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}