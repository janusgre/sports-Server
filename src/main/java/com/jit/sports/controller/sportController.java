package com.jit.sports.controller;

import com.alibaba.fastjson.JSONObject;
import com.jit.sports.InfluxDB.InfluxDealData;
import com.jit.sports.entry.SportInfo;
import com.jit.sports.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@CrossOrigin
@RequestMapping("/sport")
@RestController
public class sportController {
    @Resource
    UserService userService;

    private SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    //开始一次运动
    @RequestMapping("/startSport")
    public String startSport(@RequestParam (value = "userName")String userName,
                             @RequestParam(value = "sportTag")String sportTag) {
        long now = System.currentTimeMillis();
//        userService.insertSport(sportTag, userName, ft.format(now));
        return "true";
    }

    //结束一次运动
    @RequestMapping("/overSport")
    public void overSport(@RequestParam (value = "sportTag")String sportTag) {

        String time = ft.format(new Date());
        userService.updateSport(sportTag, time,100, 20, 20,
                80, 20, 3 , 0);
    }

    //查看所有运动
    @RequestMapping("/mySports")
    public SportInfo[] mySports(@RequestParam (value = "userName")String userName) {
        return userService.selectSportByName(userName);
    }

    @RequestMapping("/oneSport")
    public SportInfo oneSport(@RequestParam (value = "sportTag")String sportTag) {
        return userService.selectSportByTag(sportTag);
    }

    //查看运动详情
    @RequestMapping("/detail")
    public JSONObject detail(@RequestParam (value = "sportTag")String sportTag) {
        return InfluxDealData.getSportDetailByTag(sportTag);
    }


}
