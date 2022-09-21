package com.boshen.kob.backend.controller.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.boshen.kob.backend.mapper.UserMappper;
import com.boshen.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserMappper userMappper;

    @GetMapping("/user/all/")
    public List<User> getAll(){
        return userMappper.selectList(null);
    }

    @GetMapping("/user/{userId}")
    public User getUserById(@PathVariable int userId){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",userId);
        return userMappper.selectOne(queryWrapper);
    }

    @GetMapping("/user/add/{userId}/{username}/{password}")
    public String addUser(@PathVariable int userId,@PathVariable String username,@PathVariable String password){
        PasswordEncoder pe = new BCryptPasswordEncoder();
        String encodedPassWord = pe.encode(password);
        User u = new User(userId,username,encodedPassWord);
        userMappper.insert(u);
        return "Add User Successfully";
    }


}
