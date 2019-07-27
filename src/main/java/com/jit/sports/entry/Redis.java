package com.jit.sports.entry;

import com.alibaba.fastjson.JSONObject;
import com.jit.sports.Utils.PropertiesUtil;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class Redis {

    public static JSONObject getDtae(JSONObject obj) {
        String JedisHost = PropertiesUtil.getProperty("redis.host");
        int port = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));
        Jedis jedis = new Jedis("47.102.152.12", 6379);
        JSONObject nowMessage = new JSONObject();
        String userTag = obj.getString("sportTag");
        double longitude = obj.getDoubleValue("longitude");
        double latitude = obj.getDoubleValue("latitude");
        double altitude = obj.getDoubleValue("longitude");
        Map<String, String> laterRedis = new HashMap<>();

        if (!jedis.exists(obj.getString("sportTag"))) {
            //System.out.println("111111111111111111111111111110");

            laterRedis.put("longitude", String.valueOf(longitude));
            laterRedis.put("latitude", String.valueOf(latitude));
            laterRedis.put("altitude", String.valueOf(altitude));
            laterRedis.put("currentMileage", "0");
            laterRedis.put("currentTime", "0");
            laterRedis.put("currentUp", "0");
            laterRedis.put("currentDown", "0");
            laterRedis.put("startSteps",String.valueOf(obj.getInteger("steps")));

            jedis.hmset(obj.getString("sportTag"), laterRedis);
            nowMessage.put("currentMileage", 0);
            nowMessage.put("currentSpeed", obj.getDoubleValue("speed"));
            nowMessage.put("averageSpeed", 0);
            nowMessage.put("xSpeed", 0);
            nowMessage.put("currentUp", 0);
            nowMessage.put("currentDown", 0);
            nowMessage.put("currentSteps", 0);
            nowMessage.put("xSteps", 0);

            //System.out.println("11111111111111111111111111111");

        } else {

            laterRedis = jedis.hgetAll(userTag);
            //计算返回信息
            Double distance = Double.valueOf(laterRedis.get("currentMileage")) + Sparse.getDistance(Double.valueOf(laterRedis.get("latitude")), Double.valueOf(laterRedis.get("longitude")), latitude,longitude );
            long time = Long.valueOf(laterRedis.get("currentTime")) + 1;
            double speed = distance / time;
            double xspeed = 0;
            double xsteps = 0;
            double currentDown = 0;
            double currentUp = 0;
            if (altitude > Double.valueOf(obj.getDoubleValue("altitude"))) {
                currentUp = Double.valueOf(obj.getDoubleValue("currentUp")) + altitude - obj.getDoubleValue("altitude");
            } else {
                currentDown = Double.valueOf(obj.getDoubleValue("currentUp")) + obj.getDoubleValue("altitude") - altitude;
            }
            //跟新redis
            laterRedis.put("longitude", String.valueOf(longitude));
            laterRedis.put("latitude", String.valueOf(latitude));
            laterRedis.put("altitude", String.valueOf(altitude));
            laterRedis.put("currentMileage", String.valueOf(distance));
            laterRedis.put("currentTime", String.valueOf(time));
            laterRedis.put("currentUp", String.valueOf(currentUp));
            laterRedis.put("currentDown", String.valueOf(currentDown));
            jedis.hmset(obj.getString("sportTag"), laterRedis);
            //给返回赋值信息

            nowMessage.put("currentMileage", Math.round(distance*100.0)/100.0);
            nowMessage.put("currentSpeed", obj.getDoubleValue("speed"));
            nowMessage.put("averageSpeed", speed);
            nowMessage.put("xSpeed", xspeed);
            nowMessage.put("currentUp", currentUp);
            nowMessage.put("currentDown", currentDown);
            nowMessage.put("currentSteps", obj.getInteger("steps")-Long.valueOf(laterRedis.get("startSteps")));
            nowMessage.put("xSteps", xsteps);

            //System.out.println("2222222222222222222222222222222222222");
           // System.out.println(nowMessage.toString());
           // System.out.println("3333333333333333333333333333333333333");

        }
        return nowMessage;
    }



}
