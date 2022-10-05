package com.boshen.kob.backend.controller.user.account;

import com.boshen.kob.backend.service.impl.user.accound.InfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InfoServiceController {
    @Autowired
    private InfoServiceImpl infoService;

    @GetMapping("/user/account/info/")
    public Map<String,String> getInfo(){
        return infoService.getinfo();
    }
}
