package com.boshen.kob.backend.service.impl.ranklist;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boshen.kob.backend.mapper.UserMappper;
import com.boshen.kob.backend.pojo.User;
import com.boshen.kob.backend.service.ranklist.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRanklistServiceImpl implements GetRankListService {
    @Autowired
    private UserMappper userMapper;

    @Override
    public JSONObject getList(Integer pageNum) {
        IPage<User> userIPage = new Page<>(pageNum, 2);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("rating");
        List<User> users = userMapper.selectPage(userIPage, queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        for(User user : users) {
            user.setPassword("");
        }
        resp.put("users", users);
        resp.put("users_count", userMapper.selectCount(null));
        return resp;
    }
}
