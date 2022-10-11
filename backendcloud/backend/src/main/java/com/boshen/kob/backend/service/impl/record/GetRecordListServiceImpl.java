package com.boshen.kob.backend.service.impl.record;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.boshen.kob.backend.mapper.RecordMapper;
import com.boshen.kob.backend.mapper.UserMappper;
import com.boshen.kob.backend.pojo.Record;
import com.boshen.kob.backend.pojo.User;
import com.boshen.kob.backend.service.record.GetRecordListService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class GetRecordListServiceImpl implements GetRecordListService {
    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private UserMappper userMapper;

    @Override
    public JSONObject getList(Integer pageNum) {
//        增加完刚才的配置类之后直接这样写就可以做到分页了，规定一下当前展示哪个页面，和页面有多少条信息即可
        IPage<Record> recordIPage = new Page<>(pageNum, 10);
//        定义根据什么将搜友的记录排序
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
//        用上两个信息得到页面
        List<Record> records = recordMapper.selectPage(recordIPage, queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for(Record record : records) {
//            记录中没有用户的用户名和头像，这时候就涉及到了连表查询
            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());
            JSONObject item = new JSONObject();
            item.put("a_photo", userA.getPhoto());
            item.put("a_username", userA.getUsername());
            item.put("b_photo", userB.getPhoto());
            item.put("b_username", userB.getUsername());
//            如果某些需要展示的结果并没有在数据库中存储，前端需要展示，后端可以直接做处理，返回结果给前端即可
            String result = "draw";
            if("A".equals(record.getLoser())){
                result = "B win";
            }else if("B".equals(record.getLoser())){
                result = "A win";
            }
            item.put("result",result);
            item.put("record", record);
            items.add(item);
        }

        resp.put("records", items);
        //        这里需要告诉前端一共有多少页
        resp.put("records_count", recordMapper.selectCount(null));
        return resp;
    }
}