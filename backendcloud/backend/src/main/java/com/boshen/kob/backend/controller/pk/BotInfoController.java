package com.boshen.kob.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pk")
public class BotInfoController {
    @RequestMapping("/getBotInfo")
    public String getBotInfo(){
        return "hhahah";
    }
    @RequestMapping("/getBotInfoList")
    public List hetBotList(){
        ArrayList<Integer> returnList = new ArrayList<Integer>();
        returnList.add(1);
        return returnList;
    }
}
