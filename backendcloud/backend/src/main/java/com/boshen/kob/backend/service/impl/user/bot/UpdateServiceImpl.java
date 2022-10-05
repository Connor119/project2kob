package com.boshen.kob.backend.service.impl.user.bot;

import com.boshen.kob.backend.mapper.BotMapper;
import com.boshen.kob.backend.pojo.Bot;
import com.boshen.kob.backend.pojo.User;
import com.boshen.kob.backend.service.impl.utils.UserDetailsImpl;
import com.boshen.kob.backend.service.user.bot.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateServiceImpl implements UpdateService {
//    改数据库需要Mapper对象
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> update(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
//        这个botid就是我们数据库中传入的id
//        从前端传入过来的mapper中解析我们的id

        int bot_id = Integer.parseInt(data.get("bot_id"));

//        重新更新一遍信息，前端直接获取所有，没改变的也重新复制
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");
//        定义返回信息的mapper
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

        Bot bot = botMapper.selectById(bot_id);

        if(bot == null) {
            map.put("error_message", "Bot doesn't exist or is already deleted");
            return map;
        }

        if(bot.getUserId() != user.getId()) {
            map.put("error_message", "You don't have the right to update the bot");
            return map;
        }

        Bot new_bot = new Bot(
                bot.getId(),
                user.getId(),
                title,
                description,
                content,
                bot.getCreatetime(),
                new Date()
        );
        botMapper.updateById(new_bot);
        map.put("error_message", "success");
        return map;
    }
}
