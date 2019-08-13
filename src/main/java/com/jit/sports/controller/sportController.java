package com.jit.sports.controller;

import com.alibaba.fastjson.JSONObject;
import com.jit.sports.Dao.RedisDao;
import com.jit.sports.entry.SportInfo;
import com.jit.sports.extra.MyLatLngPoint;
import com.jit.sports.extra.Sparse;
import com.jit.sports.extra.SpeedElevation;
import com.jit.sports.service.InfluxService;
import com.jit.sports.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RequestMapping("/sport")
@RestController
public class sportController {
    @Resource
    UserService userService;
    @Resource
    InfluxService influxService;
    @Resource
    RedisDao redisDao;

    private SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Logger logger = LoggerFactory.getLogger(sportController.class);
    //开始一次运动
    @RequestMapping("/startSport")
    public String startSport(@RequestParam (value = "userName")String userName,
                             @RequestParam(value = "sportTag")String sportTag) {
        long now = System.currentTimeMillis();
        userService.insertSport(sportTag, userName, ft.format(now));
        //Redis redis = new Redis(sportTag,0,0,0,0);
        Map<String,String>a=new HashMap<>();
        a.put("startSteps","0");
        a.put("longitude","0" );
        a.put("latitude", "0");
        a.put("altitude", "0");
//        a.put("startSteps","0");
//        a.put("currentMileage", "0");
//        a.put("currentTime", "0");
//        a.put("currentUp", "0");
//        a.put("currentDown", "0");
//        a.put("maxSpeed","0");
//        a.put("maxAltitude","0");
//        a.put("minAltitude","0");
        redisDao.putHashTable(sportTag,a);
        return "true";
    }

    //结束一次运动
    @RequestMapping("/overSport")
    public void overSport(@RequestParam (value = "sportTag")String sportTag,
                          @RequestParam (value = "mode") String mode,
                          @RequestParam (value = "sportTitle") String sportTitle) {
        String time = ft.format(new Date());
        logger.info("in overSport");
        Map<String, String> laterRedis = new HashMap<>();
        laterRedis = redisDao.getHashMap(sportTag);
        if(laterRedis==null)
            return;
//        logger.info(laterRedis.toString());
        userService.updateSport(sportTag, time,myMathRound(Double.valueOf(laterRedis.get("currentMileage")))
                ,myMathRound(Double.valueOf(laterRedis.get("currentUp")))
                ,myMathRound(Double.valueOf(laterRedis.get("currentDown")))
                ,myMathRound(Double.valueOf(laterRedis.get("currentMileage"))/Integer.valueOf(laterRedis.get("currentTime")))
                ,myMathRound(Double.valueOf(laterRedis.get("maxSpeed")))
                ,myMathRound(Double.valueOf(laterRedis.get("maxAltitude")))
                ,myMathRound(Double.valueOf(laterRedis.get("minAltitude")))
                , mode, sportTitle);
        //抽稀加聚合
        List<SpeedElevation> aa = influxService.getSpeedElevation(sportTag);
        List<SpeedElevation> bb = SpeedElevation.polymerization(aa,60);
        List<MyLatLngPoint> cc  = influxService.getSprace(sportTag);
        Sparse sparse = new Sparse(cc,10);
        List<MyLatLngPoint> dd  = sparse.compress();

        influxService.insertspeedAltitudeProcessedMsg(bb,sportTag);
        influxService.insertLocationProcessedMsg(dd,sportTag);
        redisDao.deletekey(sportTag);
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
        return influxService.getProcessedMsgByTag(sportTag);
    }

    static double myMathRound(double num) {
        return (double)Math.round(num*100)/100;
    }
}
