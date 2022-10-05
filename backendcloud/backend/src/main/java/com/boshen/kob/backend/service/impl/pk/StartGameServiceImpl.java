package com.boshen.kob.backend.service.impl.pk;

import com.boshen.kob.backend.consumer.WebSocketServer;
import com.boshen.kob.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer aId, Integer bId) {
//      接收到消息之后需要创建游戏先输出一个调试信息
        System.out.println("start game" + aId + " " + bId);
        WebSocketServer.startGame(aId, bId);
        return "start game success";
    }
}