package com.jit.sports.InfluxDB;
import com.alibaba.fastjson.JSONObject;
import com.jit.sports.Utils.PropertiesUtil;
import com.jit.sports.extra.MyLatLngPoint;
import com.jit.sports.extra.SpeedElevation;
import org.influxdb.dto.QueryResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InfluxDealData {

    //    private static final String InfluxOpenUrl = PropertiesUtil.getProperty("influxDB.url");
    private static final String InfluxOpenUrl = "http://47.102.152.12:8086";
    private static final String InfluxUsername = "root";
    private static final String InfluxPasswd = "123456";
    private static final String InfluxDatabase = "sports";
    private static InfluxDBConnection influxDBConnection = new InfluxDBConnection(InfluxUsername,
            InfluxPasswd, InfluxOpenUrl,InfluxDatabase, null);

    //插入数据

    //插入原始运动数据
    public static void writeSportInfoIntoDB(String sportTag, double longitude, double latitude,
                                            double altitude, double speed, double azimuth,
                                            double pitch, double roll,
                                            double accelerated_x, double accelerated_y,
                                            double accelerated_z, int steps) {
        Map<String, String> tags = new HashMap<String, String>();
        Map<String, Object> fields = new HashMap<String, Object>();
        tags.put("sportTag", sportTag);
        fields.put("longitude", longitude);
        fields.put("latitude", latitude);
        fields.put("altitude", altitude);
        fields.put("speed", speed);
        fields.put("azimuth", azimuth);
        fields.put("pitch", pitch);
        fields.put("roll", roll);
        fields.put("accelerated_x", accelerated_x);
        fields.put("accelerated_y", accelerated_y);
        fields.put("accelerated_z", accelerated_z);
        fields.put("steps", steps);

        influxDBConnection.insert("sportDetail", tags, fields, System.currentTimeMillis(),
                TimeUnit.MILLISECONDS);

        System.out.println(sportTag + "插入一条记录。");
    }

    //插入抽稀后的位置信息
    public static void insertLocationProcessedMsg(String sportTag, double longitude,double latitude){
        Map<String, String> tags = new HashMap<String, String>();
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("longitude", longitude);
        fields.put("latitude", latitude);
        tags.put("sportTag", sportTag);

        influxDBConnection.insert("locationDetail", tags, fields,System.currentTimeMillis(),
                TimeUnit.MILLISECONDS);
    }

    //插入聚合后的速度、海拔信息
    public static void insertspeedAltitudeProcessedMsg(String sportTag, long time, double speed,double altitude){
        Map<String, String> tags = new HashMap<String, String>();
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("speed", speed);
        fields.put("altitude", altitude);
        tags.put("sportTag", sportTag);

        influxDBConnection.insert("speedAltitudeDetail", tags, fields, time, TimeUnit.MILLISECONDS);
    }

    //读取数据

    //取出结果集中的数据
    public static JSONObject packageMsg(QueryResult results) {
        QueryResult.Result oneResult = results.getResults().get(0);
        List<QueryResult.Series> series = oneResult.getSeries();

        JSONObject res = new JSONObject();
        if(series == null) {
            return res;
        }
        for(QueryResult.Series series1 : series) {
            res.put("columns", series1.getColumns());
            res.put("values", series1.getValues());
        }
        return res;
    }

    //取出处理后的信息
    public static JSONObject getProcessedMsgByTag(String sportTag) {
        JSONObject res = new JSONObject();
        QueryResult results = influxDBConnection
                .query("SELECT time,longitude,latitude FROM locationDetail " +
                        "where sportTag = '"+ sportTag +"'  order by time asc");
        res.put("location", packageMsg(results));
        QueryResult results2 = influxDBConnection
                .query("SELECT time,speed,altitude FROM speedAltitudeDetail " +
                        "where sportTag = '"+ sportTag +"'  order by time asc");
        res.put("speedAltitude", packageMsg(results2));
        return res;
    }

    //取出未处理的信息
    public static JSONObject getSportDetailByTag(String sportTag) {
        QueryResult results = influxDBConnection
                .query("SELECT time,longitude,latitude,altitude,speed,steps FROM sportDetail " +
                        "where sportTag = '"+ sportTag +"'  order by time asc");
        return packageMsg(results);
    }

    //取出位置信息
    public static List<MyLatLngPoint> getSprace(String sportTag) {
        QueryResult results = influxDBConnection
                .query("SELECT longitude,latitude FROM sportDetail " +
                        "where sportTag = '"+ sportTag +"'  order by time asc");

        QueryResult.Result oneResult = results.getResults().get(0);
        //List<QueryResult.Series> series = oneResult.getSeries();
        int i=0;
        List<List<Object>> valueList = null;
        List<MyLatLngPoint>res = new ArrayList<>();
        if (oneResult.getSeries() != null)
        {
            valueList = oneResult.getSeries().stream().map(QueryResult.Series::getValues)
                    .collect(Collectors.toList()).get(0);
            if (valueList != null && valueList.size() > 0) {
                for (List<Object> value : valueList) {

                    MyLatLngPoint a = new MyLatLngPoint();
                    String longitude = value.get(1) == null ? null : value.get(1).toString();
                    String latitude = value.get(2) == null ? null : value.get(2).toString();
                    a.setId(i);
                    //System.out.println(latitude);
                    //System.out.println(longitude);
                    a.setLatitude(Double.valueOf(latitude));
                    a.setLongitude(Double.valueOf(longitude));
                    res.add(a);
                    i=i+1;

                }
            }
        }
        return res;

    }

    //取出某项信息（速度、海拔）
    public static List<Double> getOneValue(String sportTag, String key) {
        QueryResult results = influxDBConnection
                .query("SELECT "+ key +" FROM sportDetail " +
                        "where sportTag = '"+ sportTag +"'  order by time asc");

        QueryResult.Result oneResult = results.getResults().get(0);
        List<List<Object>> valueList = null;
        List<Double>res = new ArrayList<>();
        if (oneResult.getSeries() != null)
        {
            valueList = oneResult.getSeries().stream().map(QueryResult.Series::getValues)
                    .collect(Collectors.toList()).get(0);
            if (valueList != null && valueList.size() > 0) {
                for (List<Object> value : valueList) {

                    String result = value.get(1) == null ? null : value.get(1).toString();

                    res.add(Double.valueOf(result));
                }
            }
        }
        return res;
    }
    public static List<SpeedElevation> getSpeedElevation(String sportTag) {
        QueryResult results = influxDBConnection
                .query("SELECT altitude,speed FROM sportDetail " +
                        "where sportTag = '"+ sportTag +"'  order by time asc");

        QueryResult.Result oneResult = results.getResults().get(0);
        List<List<Object>> valueList = null;
        List<SpeedElevation>res = new ArrayList<>();
        if (oneResult.getSeries() != null)
        {
            valueList = oneResult.getSeries().stream().map(QueryResult.Series::getValues)
                    .collect(Collectors.toList()).get(0);
            if (valueList != null && valueList.size() > 0) {
                for (List<Object> value : valueList) {
                    SpeedElevation speedElevation =new SpeedElevation();
                    String time = value.get(0) == null ? null : value.get(0).toString();
                    String altitude = value.get(1) == null ? null : value.get(1).toString();
                    String speed = value.get(2) == null ? null : value.get(2).toString();
                    speedElevation.setElevation(Double.valueOf(altitude));
                    speedElevation.setSpeed(Double.valueOf(speed));
                    speedElevation.setTime(time);
                    res.add(speedElevation);
                }
            }
        }
        return res;
    }

}
