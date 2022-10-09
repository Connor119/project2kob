package com.boshen.kob.backend.service.pk;

public interface ReceiveBotMoveService {
//    主服务器用来接收bot代码执行后返回的direction
    String receiveBotMove(Integer userId, Integer direction);

}
