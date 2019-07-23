package com.jit.sports;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {



    public static void main(String[] args) {
        String s = "{\"sportTag\":123,\"latitude\":31.910022,\"longitude\":118.90806,\"altitude\":20.2,\"speed\":0,\"direction\":-1,\"azimuth\":251,\"pitch\":-17,\"roll\":9,\"accelerated_x\":-1,\"accelerated_y\":2,\"accelerated_z\":9,\"steps\":162}";
        System.out.println(JSON.parseObject(s));

    }
}
