package com.jit.sports.InfluxDB;
import com.alibaba.fastjson.JSONObject;
import com.jit.sports.Utils.PropertiesUtil;
import com.jit.sports.entry.SportDetailInfo;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InfluxDealData {

    private static final String InfluxOpenUrl = PropertiesUtil.getProperty("influxDB.url");
    private static final String InfluxUsername = "root";
    private static final String InfluxPasswd = "123456";
    private static final String InfluxDatabase = "sports";
    private static InfluxDBConnection influxDBConnection = new InfluxDBConnection(InfluxUsername,
            InfluxPasswd, InfluxOpenUrl,InfluxDatabase, null);

    public static void writeSportInfoIntoDB(String tag, double longitude, double latitude,
                                            double altitude, double speed, double azimuth,
                                            double pitch, double roll,
                                            double accelerated_x, double accelerated_y,
                                            double accelerated_z, int steps) {
        Map<String, String> tags = new HashMap<String, String>();
        Map<String, Object> fields = new HashMap<String, Object>();
        tags.put("sportTag", tag);
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

        System.out.println(tag + "插入一条记录。");
    }

    public static JSONObject getSportDetailByTag(String tag) {
        System.out.println("SELECT time,longitude,latitude,altitude,speed,azimuth,pitch,roll,accelerated_x," +
                "accelerated_y,accelerated_z,steps FROM sportDetail " +
                "where sportTag = '"+ tag +"'  order by time asc");
        QueryResult results = influxDBConnection
                .query("SELECT time,longitude,latitude,altitude,speed,azimuth,pitch,roll,accelerated_x," +
                        "accelerated_y,accelerated_z,steps FROM sportDetail " +
                        "where sportTag = '"+ tag +"'  order by time asc");

        QueryResult.Result oneResult = results.getResults().get(0);
        List<QueryResult.Series> series = oneResult.getSeries();
        JSONObject res = new JSONObject();
        for(QueryResult.Series series1 : series) {
            res.put("columns", series1.getColumns());
            res.put("values", series1.getValues());
        }
        return res;

    }

}
