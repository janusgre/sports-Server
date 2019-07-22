package com.jit.sports;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.util.List;

public class test {

    public static void main(String[] args) {
        String s = "{\"columns\":[\"time\",\"longitude\",\"latitude\",\"elevation\",\"speed\",\"azimuth\",\"pitch\",\"roll\",\"accelerated_x\",\"accelerated_y\",\"accelerated_z\",\"steps\"],\"values\":[[\"2019-07-22T10:58:54.598Z\",118.89717,31.916263,33.8,0,233,-4,57,-8,0,5,12219],[\"2019-07-22T10:58:55.758Z\",118.89717,31.916263,33.8,0,233,-4,57,-8,0,5,12219],[\"2019-07-22T10:58:56.603Z\",118.89717,31.916263,33.8,0,233,-4,57,-8,0,5,12219]]}";
        JSONObject obj = JSON.parseObject(s);
        System.out.println(obj);
//        System.out.println(obj.get("columns"));
        List<String> columns = (List<String>) obj.get("columns");
        List<List<Object>> values = (List<List<Object>>) obj.get("values");
        System.out.println(columns);
        System.out.println(values);
    }
}