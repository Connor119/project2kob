package com.boshen.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.boshen.kob.backend.mapper.BotMapper;
import com.boshen.kob.backend.pojo.Bot;
import com.boshen.kob.backend.pojo.User;
import com.boshen.kob.backend.service.impl.utils.UserDetailsImpl;
import com.boshen.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
//        首先应该知道插入的当前的是谁
//        所有获取用户的操作，都是SpringSecurity的一套操作，先获得UPAT之后用这个对象getPrincipal
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl)authentication.getPrincipal();
        User user = loginUser.getUser();
//        查看数据表，对比数据表来看前端给我们传来了什么数据
        String title = data.get("title");
        String description =  data.get("description");
        String content = data.get("content");

        Map<String,String> map = new HashMap<>();

        if(title == null || title.length() == 0) {// 不在数据库里设置死，可以为以后草稿功能留接口
            map.put("error_message", "Title can't be blanked");
            return map;
        }

        if(title.length() > 100) {
            map.put("error_message", "Title can't be longer than 100");
            return map;
        }

        if(description == null || description.length() == 0) {
            description = "This user is too lazy to leave anything~";
        }

        if(description.length() > 300) {
            map.put("error_message", "Description can't be longer than 300");
            return map;
        }

        if(content == null || content.length() == 0) {
            map.put("error_message", "Code can't be blanked");
            return map;
        }

        if(content.length() > 10000) {
            map.put("error_message", "Code can't be longer than 10000");
            return map;
        }

        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        if(botMapper.selectCount(queryWrapper) >= 10) {
            map.put("error_message", "Bot number can't be greater than 10");
            return map;
        }


//做完判断后将所有的信息通过mapper放入数据库中
        Date now = new Date();
        Bot bot = new Bot(null, user.getId(), title, description, content, now, now);

        botMapper.insert(bot);
        map.put("error_message", "success");

        return map;
    }
}
