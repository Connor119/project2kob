package com.boshen.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//这个类用来维护玩家的位置信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer botId;//-1表示人工操作，其它的bot表示bot
    private String botCode;
//    玩家所在的行
    private  Integer sx;
//    玩家所在的列
    private Integer sy;
//存储玩家每一次指令是什么
    private List<Integer> steps;

    private boolean check_tail_increasing(int step) { // 检验当前回合蛇的身体会不会变长
        if(step <= 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for(int d : steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if(!check_tail_increasing(++ step)) {
                res.remove(0);
            }
        }
        return res;
    }
    public String getStepsString() {
        StringBuilder res = new StringBuilder();
        for(int d : steps) {
            res.append(d);
        }
        return res.toString();
    }


}
