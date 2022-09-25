package com.boshen.kob.backend.service.user.bot;


import java.util.Map;

public interface AddService {
//    service接口大部分都是返回一个Map的
    Map<String,String> add(Map<String,String> data);
}
