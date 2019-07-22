package com.jit.sports.controller;

import com.alibaba.fastjson.JSONObject;
import com.jit.sports.InfluxDB.InfluxDealData;
import com.jit.sports.entry.SportInfo;
import com.jit.sports.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@RequestMapping("/sport")
@RestController
public class sportController {
    @Resource
    UserService userService;

    private SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    //开始一次运动
    @RequestMapping("/startSport")
    public String startSport(@RequestParam (value = "userName")String userName) {
        long now = System.currentTimeMillis();
        String tag = userName + now;
        userService.insertSport(tag, userName, ft.format(now));
        return tag;
    }

    //结束一次运动
    @RequestMapping("/overSport")
    public void overSport(@RequestParam (value = "tag")String tag) {

        String time = ft.format(new Date());
        userService.updateSport(tag, time,        100, 20, 20,
                80, 20, 3 , 0);
    }

    //查看所有运动
    @RequestMapping("/mySports")
    public SportInfo[] mySports(@RequestParam (value = "userName")String userName) {
        return userService.selectSportByName(userName);
    }

    //查看运动详情
    @RequestMapping("/detail")
    public JSONObject detail(@RequestParam (value = "tag")String tag) {
        return InfluxDealData.getSportDetailByTag(tag);
    }


}
