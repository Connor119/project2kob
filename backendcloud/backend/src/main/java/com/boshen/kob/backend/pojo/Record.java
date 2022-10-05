package com.boshen.kob.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record {
    /*
    * 对应记录表创建pojo这个表用来记录两个玩家的数据
    * */
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer aId;// 数据库变量名称为下划线，pojo里面必须用驼峰命名对应
    private Integer aSx;
    private Integer aSy;
    private Integer bId;
    private Integer bSx;
    private Integer bSy;
    private String aSteps;
    private String bSteps;
    private String map;
    private String loser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Dublin")// HH 表示24小时
    private Date createtime;
}
