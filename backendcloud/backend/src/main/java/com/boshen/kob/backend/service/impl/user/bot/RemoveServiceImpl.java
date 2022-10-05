package com.boshen.kob.backend.service.impl.user.bot;

import com.boshen.kob.backend.mapper.BotMapper;
import com.boshen.kob.backend.pojo.Bot;
import com.boshen.kob.backend.pojo.User;
import com.boshen.kob.backend.service.impl.utils.UserDetailsImpl;
import com.boshen.kob.backend.service.user.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveServiceImpl implements RemoveService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> remove(Map<String, String> data) {
//        先加载user进来
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

//        拿到前端传入的bot id
        int bot_id = Integer.parseInt(data.get("bot_id"));
//        通过mapper拿到这个bot做逻辑判断
        Bot bot = botMapper.selectById(bot_id);
//        进行逻辑判断，如果bot对象不满足以下的if条件，按摩就要返回对应的信息
//        定义返回信息
        Map<String, String> map =  new HashMap<>();
        if(bot == null) {
            map.put("error_message", "Bot doesn't exist or is already deleted");
            return map;
        }
//看看是否作者的操作
        if(bot.getUserId() != user.getId()) {
            map.put("error_message", "You don't have the right to delete the bot");
            return map;
        }

//        判断完成后需要返回成功信息，并将这个bot删除
        botMapper.deleteById(bot_id);
        map.put("error_message", "success");
        return map;
    }
}
