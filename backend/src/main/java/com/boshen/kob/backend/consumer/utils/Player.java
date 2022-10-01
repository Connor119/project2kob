package com.boshen.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//这个类用来维护玩家的位置信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private int id;
//    玩家所在的行
    private  Integer sx;
//    玩家所在的列
    private Integer sy;
//存储玩家每一次指令是什么
    private List<Integer> steps;


}
