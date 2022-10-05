package com.boshen.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cell {
    /*
    * 这个类用来标注身体的每一个单员
    * */
    int x;
    int y;
}