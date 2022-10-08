package com.kob.botruningsystem.service.impl;

import com.kob.botruningsystem.service.BotRunningService;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    @Override
    public String addBot(Integer userId, String botCode, String input) {
        System.out.println("add bot:" + userId + " " + botCode + " " + input);
        return "add bot success";
    }
}
