package com.boshen.kob.backend.service.impl.user.accound;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.boshen.kob.backend.mapper.UserMappper;
import com.boshen.kob.backend.pojo.User;
import com.boshen.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserMappper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        if(username == null) {// 是否有参数
            map.put("error_message", "Username can't be missing");
            return map;
        }
        if(password == null || confirmedPassword == null) {
            map.put("error_message", "Password can't be missing");
            return map;
        }

        username = username.trim();// 删掉首尾的空白字符
        if(username.length() == 0) {
            map.put("error_message", "Username can't be blanked");
            return map;
        }

        if(password.length() == 0 || confirmedPassword.length() == 0) {
            map.put("error_message", "Password can't be blanked");
            return map;
        }

        if(username.length() > 100) {
            map.put("error_message", "Username can't be longer than 100");
            return map;
        }

        if(password.length() > 100 || confirmedPassword.length() > 100) {
            map.put("error_message", "Password can't be longer than 100");
            return map;
        }

        if(!password.equals(confirmedPassword)) {
            map.put("error_message", "Two Passwords don't match");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if(!users.isEmpty()) {
            map.put("error_message", "Username already exists");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://www.behance.net/search/projects/?search=%E6%89%8B%E7%BB%98%E5%A4%B4%E5%83%8F";
        User user = new User(null, username, encodedPassword, photo,1500);
        userMapper.insert(user);


        map.put("error_message","success");
        return map;
    }
}
