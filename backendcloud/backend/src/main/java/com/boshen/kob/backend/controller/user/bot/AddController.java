package com.boshen.kob.backend.controller.user.bot;

import com.boshen.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AddController {
    @Autowired
    private AddService addService;

//    需要修改数据库的就使用post请求
    @PostMapping("/user/bot/add/")
    public Map<String,String> add(@RequestParam Map<String,String> data){
        return addService.add(data);
    }

}
