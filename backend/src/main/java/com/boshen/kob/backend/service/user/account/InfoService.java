package com.boshen.kob.backend.service.user.account;

import java.util.Map;

//所有的API都返回一个Map
//service的设计原理就是想用户会传递过来什么数据，之后在形参中传递过来，之后想我们需要传递给用户什么（一边传递给用户一个map）
public interface InfoService {
    public Map<String,String> getinfo();
}
