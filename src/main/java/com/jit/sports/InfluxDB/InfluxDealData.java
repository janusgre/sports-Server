package com.jit.sports.InfluxDB;
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
                                            double elevation, double speed, double direction_x,
                                            double direction_y, double direction_z,
                                            double accelerated_x, double accelerated_y,
                                            double accelerated_z, int steps) {
        Map<String, String> tags = new HashMap<String, String>();
        Map<String, Object> fields = new HashMap<String, Object>();
        tags.put("tag", tag);
        fields.put("longitude", longitude);
        fields.put("latitude", latitude);
        fields.put("elevation", elevation);
        fields.put("speed", speed);
        fields.put("direction_x", direction_x);
        fields.put("direction_y", direction_y);
        fields.put("direction_z", direction_z);
        fields.put("accelerated_x", accelerated_x);
        fields.put("accelerated_y", accelerated_y);
        fields.put("accelerated_z", accelerated_z);
        fields.put("steps", steps);

        influxDBConnection.insert("sportDetail", tags, fields, System.currentTimeMillis(),
                TimeUnit.MILLISECONDS);

        System.out.println(tag + "插入一条记录。");
    }

    public static ArrayList<SportDetailInfo> getSportDetailByTag(String tag) {
        ArrayList<SportDetailInfo> res = new ArrayList<>();
        QueryResult results = influxDBConnection
                .query("SELECT time,longitude,latitude,elevation,speed,direction_x,direction_y," +
                        "direction_z,accelerated_x,accelerated_y,accelerated_z,steps FROM sportDetail " +
                        "where tag = '"+ tag +"'  order by time asc");
        QueryResult.Result oneResult = results.getResults().get(0);
        if (oneResult.getSeries() != null) {
            List<List<Object>> valueList = oneResult.getSeries().stream().map(QueryResult.Series::getValues)
                    .collect(Collectors.toList()).get(0);
            if (valueList != null && valueList.size() > 0) {
                for (List<Object> value : valueList) {
                    SportDetailInfo sportDetailInfo = new SportDetailInfo();

                    sportDetailInfo.setTime(value.get(0).toString());
                    sportDetailInfo.setLongitude(Double.parseDouble(value.get(1).toString()));
                    sportDetailInfo.setLatitude(Double.parseDouble(value.get(2).toString()));
                    sportDetailInfo.setElevation(Double.parseDouble(value.get(3).toString()));
                    sportDetailInfo.setSpeed(Double.parseDouble(value.get(4).toString()));
                    sportDetailInfo.setDirection_x(Double.parseDouble(value.get(5).toString()));
                    sportDetailInfo.setDirection_y(Double.parseDouble(value.get(6).toString()));
                    sportDetailInfo.setDirection_z(Double.parseDouble(value.get(7).toString()));
                    sportDetailInfo.setAccelerated_x(Double.parseDouble(value.get(8).toString()));
                    sportDetailInfo.setAccelerated_y(Double.parseDouble(value.get(9).toString()));
                    sportDetailInfo.setAccelerated_z(Double.parseDouble(value.get(10).toString()));
                    sportDetailInfo.setSteps(Integer.parseInt(value.get(11).toString()));

                    res.add(sportDetailInfo);
                }
            }
        }
        return res;
    }

}
