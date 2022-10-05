package com.boshen.kob.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.boshen.kob.backend.mapper.UserMappper;
import com.boshen.kob.backend.pojo.User;
import com.boshen.kob.backend.service.impl.utils.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
//    通过userName找到对应的信息

    @Autowired
    private UserMappper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        1.从数据库中拿到user
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
//        这个函数只管拿到user，而判断用户名密码是否匹配的逻辑是再LoinServiceImpl中去写的，而那个类中使用AuthenticationManager进行判断
        return new UserDetailsImpl(user);
    }
}
