package com.boshen.kob.backend.controller.ranklist;

import com.alibaba.fastjson2.JSONObject;
import com.boshen.kob.backend.service.ranklist.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetRanklistController {
    @Autowired
    private GetRankListService getRanklistService;

    @GetMapping("/ranklist/getlist/")
    public JSONObject getList(@RequestParam Map<String, String> data) {
        Integer pageNum = Integer.parseInt(data.get("page_num"));
        return getRanklistService.getList(pageNum);
    }
}
