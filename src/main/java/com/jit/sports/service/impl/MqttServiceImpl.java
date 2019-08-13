package com.jit.sports.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jit.sports.Dao.RedisDao;
import com.jit.sports.config.ApplicationContextProvider;
import com.jit.sports.Utils.MQTTClient;
import com.jit.sports.extra.Sparse;
import com.jit.sports.service.InfluxService;
import com.jit.sports.service.MqttService;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xushangyu on 2019/8/9.
 */
@Service
public class MqttServiceImpl implements MqttService {
    @Resource
    RedisDao redisDao;
    private InfluxService influxService = ApplicationContextProvider.getBean(InfluxService.class);
    @Autowired
    private MQTTClient mqttClient;

    private static final Logger logger = LoggerFactory.getLogger(MqttServiceImpl.class);
    @Override
    public void connectionLost(Throwable cause) {
        logger.error("MQTT : connectionLost\n" + cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        dealMsg(topic, message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token)	{ return; }

    private void dealMsg(String topic, MqttMessage message) {
//        logger.info("payload:"+new String(message.getPayload()));
        if (message.isRetained()) {
            return;
        }

        if(topic.startsWith("sports/sportInfo")) {
            dealSportInfo(topic, message);
        }else if(topic.equals( "Will")){
            dealWill(message);
        }else{

        }
    }

    //处理运动信息
    private void dealSportInfo(String topic, MqttMessage message) {
        String userName = topic.substring(topic.lastIndexOf('/') + 1);
//        mqttClient.publish("sports/processedInfo/test", userName);
        JSONObject obj = JSON.parseObject(new String(message.getPayload()));

        if(obj.getString("altitude").contains("E")) {
            logger.info("altitude:"+obj.getString("altitude"));
            return;
        }

        //处理信息后返回给客户
        try{
            //JSONObject nowMessage =new JSONObject();
            JSONObject nowMessage =  getRedisData(obj);
            mqttClient.publish("sports/processedInfo/" + userName, nowMessage.toString());
        }catch (Exception e){
            logger.error("mqtt error：Radis");
        }
        try {
            //写入数据库
            influxService.writeSportInfoIntoDB(obj.getString("sportTag"), obj.getDoubleValue("longitude"),
                    obj.getDoubleValue("latitude"), obj.getDoubleValue("altitude"),
                    obj.getDoubleValue("speed"), obj.getDoubleValue("azimuth"),
                    obj.getDoubleValue("pitch"), obj.getDoubleValue("roll"),
                    obj.getDoubleValue("accelerated_x"), obj.getDoubleValue("accelerated_y"),
                    obj.getDoubleValue("accelerated_z"), obj.getInteger("steps"));
        }catch (Exception e){
            logger.error("mqtt error：InfluxDB");
        }

    }

    //记录异常掉线
    private void dealWill(MqttMessage message) 	{
        logger.info("in Will:"+new String(message.getPayload()));
    }


    //这个函数是Redis里的GetData()，该函数内部有逻辑错误,请修改
    private JSONObject getRedisData(JSONObject obj) {


        JSONObject nowMessage = new JSONObject();

        String userTag = obj.getString("sportTag");
        double longitude = obj.getDoubleValue("longitude");
        double latitude = obj.getDoubleValue("latitude");
        double altitude = obj.getDoubleValue("altitude");
        Map<String, String> laterRedis = new HashMap<>();
        laterRedis =redisDao.getHashMap(userTag);

        if(redisDao.existTable(userTag,"startSteps")) {
            if (laterRedis.get("startSteps").equals("0") )
            {
                laterRedis.put("longitude", String.valueOf(longitude));
                laterRedis.put("latitude", String.valueOf(latitude));
                laterRedis.put("altitude", String.valueOf(altitude));
                laterRedis.put("startSteps",String.valueOf(obj.getInteger("steps")));

                laterRedis.put("currentMileage", "0");
                laterRedis.put("currentTime", "0");
                laterRedis.put("currentUp", "0");
                laterRedis.put("currentDown", "0");
                laterRedis.put("maxSpeed","0");
                laterRedis.put("maxAltitude","0");
                laterRedis.put("minAltitude", "100000");
            }
            //计算返回信息
            Double distance = Double.valueOf(laterRedis.get("currentMileage")) + Sparse.getDistance(Double.valueOf(laterRedis.get("latitude")), Double.valueOf(laterRedis.get("longitude")), latitude, longitude);
            long time = Long.valueOf(laterRedis.get("currentTime")) + 1;
            double speed = distance / time;
            double xspeed = 0;
            double xsteps = 0;
            double currentDown = Double.valueOf(laterRedis.get("currentDown"));
            double currentUp = Double.valueOf(laterRedis.get("currentUp"));
            if ( Double.valueOf(laterRedis.get("altitude")) > altitude) {
                currentDown = Double.valueOf(laterRedis.get("currentDown"))+ Double.valueOf(laterRedis.get("altitude"))-altitude;
            } else {
                currentUp = Double.valueOf(laterRedis.get("currentUp")) + altitude-Double.valueOf(laterRedis.get("altitude"));
            }

            //跟新redis
            laterRedis.put("longitude", String.valueOf(longitude));
            laterRedis.put("latitude", String.valueOf(latitude));
            laterRedis.put("altitude", String.valueOf(altitude));
            laterRedis.put("currentMileage", String.valueOf(distance));
            laterRedis.put("currentTime", String.valueOf(time));
            laterRedis.put("currentUp", String.valueOf(currentUp));
            laterRedis.put("currentDown", String.valueOf(currentDown));
            laterRedis.put("maxSpeed",String.valueOf(Math.max(Double.valueOf(laterRedis.get("maxSpeed")),obj.getDoubleValue("speed"))));
            laterRedis.put("maxAltitude",String.valueOf(Math.max(Double.valueOf(laterRedis.get("maxAltitude")), altitude)));
            laterRedis.put("minAltitude", String.valueOf(Math.min(Double.valueOf(laterRedis.get("minAltitude")), altitude)));
            //jedis.hmset(obj.getString("sportTag"), laterRedis);
            redisDao.putHashTable(userTag, laterRedis);
            //给返回赋值信息

            nowMessage.put("currentMileage", Math.round(distance * 100.0) / 100.0);
            nowMessage.put("currentSpeed", Math.round((obj.getDoubleValue("speed")) * 100.0) / 100.0);
            nowMessage.put("averageSpeed", Math.round(speed * 100.0) / 100.0);
            nowMessage.put("xSpeed", Math.round(xspeed * 100.0) / 100.0);
            nowMessage.put("currentUp", Math.round(currentUp * 100.0) / 100.0);
            nowMessage.put("currentDown", Math.round(currentDown * 100.0) / 100.0);
            nowMessage.put("currentSteps", obj.getInteger("steps") - Long.valueOf(laterRedis.get("startSteps")));
            nowMessage.put("xSteps", xsteps);

            //System.out.println("2222222222222222222222222222222222222");
            // System.out.println(nowMessage.toString());
            // System.out.println("3333333333333333333333333333333333333");

        }

        if(nowMessage == null){
            return new JSONObject();
        }
        return nowMessage;
    }

}
