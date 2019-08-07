package com.jit.sports.extra;

import com.jit.sports.InfluxDB.InfluxDealData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpeedElevation {

    String time;
    double elevation;
    double speed;

    public SpeedElevation(String time, double elevation, double speed) {
        this.time = time;
        this.elevation = elevation;
        this.speed = speed;
    }
    public SpeedElevation()
    {

    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public static List<SpeedElevation> polymerization(List<SpeedElevation> last, int frequency)
    {
        List<SpeedElevation>newSpeed = new ArrayList<>();
        int size = last.size();
        int len =  size%frequency != 0 ? (size / frequency+1) : size/frequency;
        int j=0;
        System.out.println("len = "+len);
        for(int i = 0; i < len ; i++)
        {
           // System.out.println(j+"   "+frequency);

            if(j+frequency<size)
                newSpeed.add(getAverage(last.subList(j,j+frequency)));
            else
                newSpeed.add(getAverage(last.subList(j,size)));
            j = j+frequency;

        }
        return newSpeed;
    }
    public static SpeedElevation getAverage(List<SpeedElevation> allNums)
    {
        double speed=0;
        double elevation=0;
        for (int i = 0; i < allNums.size(); i++)
        {
            speed = speed + allNums.get(i).getSpeed();
            elevation = elevation + allNums.get(i).getElevation();
        }
        SpeedElevation res =new SpeedElevation(allNums.get(0).getTime(),
                Math.round(elevation/(allNums.size()*1.0)*100.0)/100.0,
                Math.round(speed/(allNums.size()*1.0)*100.0)/100.0);
        return res;
    }
    public static void insertInflxdb(List<SpeedElevation> res, String sportsTag)
    {
        for(int i=0;i<res.size();i++)
        {
            InfluxDealData.insertspeedAltitudeProcessedMsg(sportsTag,timeToLong(res.get(i).getTime())
            ,res.get(i).getSpeed()
            ,res.get(i).getElevation());
        }
    }
    public static Long timeToLong(String time)
    {

        time = time.replace("Z", " UTC");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        Date d = null;
        try {
            d = format1.parse(time);
        } catch (ParseException e) {
            long t=0;
            return t;
       }
        long t = d.getTime();
        return t;
    }
}
