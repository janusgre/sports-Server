package com.jit.sports.entry;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Sparse {
    private ArrayList<MyLatLngPoint> latLngInit;
    private int pMax ;
    private int start;
    private int end;

    //计算2个经纬度之间的距离
    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;

    }

    public Sparse(ArrayList<MyLatLngPoint> latLngInit, int pMax) {
        if (latLngInit==null)
        {
            throw new IllegalArgumentException("传入的经纬度为空");
        }
        this.start=0;
        this.end = latLngInit.size()-1;
        this.latLngInit = latLngInit;
        this.pMax = pMax;
    }


    public double distToSegment(MyLatLngPoint start, MyLatLngPoint end, MyLatLngPoint center) {
        double a = Math.abs(getDistance(start.getLatitude(),end.getLatitude(),start.getLongitude()
        ,end.getLongitude()));
        double b = Math.abs(getDistance(start.getLatitude(),center.getLatitude(),start.getLongitude()
                ,center.getLongitude()));
        double c = Math.abs(getDistance(center.getLatitude(),end.getLatitude(),center.getLongitude()
                ,end.getLongitude()));

        double p = (a+b+c)/2.0;
        double s = Math.sqrt(Math.abs(p*(p-a)*(p-c)*(p-b)));
        double d = s*2.0/2;
        return d;
    }


    public ArrayList<MyLatLngPoint> compressLine(MyLatLngPoint[] originalLat, ArrayList<MyLatLngPoint> endLat, int start, int end, double pMax) {
        if (start < end)
        {
            double maxDist=0;
            int currebtIndex=0;
            for(int i = start + 1;i<end;i++)
            {
                double currentDist=distToSegment(originalLat[start],
                        originalLat[end],originalLat[i]);
                if(currentDist > maxDist)
                {
                    maxDist = currentDist;
                    currebtIndex = i;
                }
            }
            if (maxDist > pMax)
            {
                endLat.add(originalLat[(int) currebtIndex]);

                compressLine(originalLat,endLat,start,currebtIndex,pMax);

                compressLine(originalLat,endLat,currebtIndex,end,pMax);
            }
        }
        return endLat;
    }


    public ArrayList<MyLatLngPoint> compress()
    {
        int size = latLngInit.size()-1;
        ArrayList<MyLatLngPoint>later = new ArrayList<>();
        later.add(latLngInit.get(0));
        later.add(latLngInit.get(size));
        ArrayList<MyLatLngPoint> last = compressLine(latLngInit.toArray(new MyLatLngPoint[size])
        ,later,0,size-1,pMax);

        Collections.sort(last, new Comparator<MyLatLngPoint>() {
            @Override
            public int compare(MyLatLngPoint o1, MyLatLngPoint o2) {
                return o1.compareTo(o2);
            }
        });
        return last;
    }
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;

        return Math.round(s*100.0)/100.0;
    }
    public static double getDistance1(double lat1, double lng1, double lat2,
                                     double lng2) {

        double a =lat1-lat2;
        double b =lng1 -lng2;
        return Math.sqrt((a*a+b*b));
    }


}
