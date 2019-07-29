package com.jit.sports.extra;

import java.util.ArrayList;
import java.util.List;

//海拔点聚合
public class Elevation {

    public static List<Double> polymerization(List<Double> lastElevation, int frequency)
    {
        List<Double>newSpeed = new ArrayList<>();
        int size = lastElevation.size();
        int len =  size%frequency != 0 ? (size / frequency+1) : size/frequency;
        int j=0;
        System.out.println(len);
        for(int i = 0; i < len ; i++)
        {
            System.out.println(j+"   "+frequency);

            if(j+frequency<size)
                newSpeed.add(getAverage(lastElevation.subList(j,j+frequency)));
            else
                newSpeed.add(getAverage(lastElevation.subList(j,size)));
            j = j+frequency;

        }
        return newSpeed;
    }
    public static double  getAverage(List<Double> elevationNums)
    {
        System.out.println(elevationNums);
        double sum=0;
        for (int i = 0; i < elevationNums.size(); i++)
        {
            sum = sum +elevationNums.get(i);
        }
        return sum/elevationNums.size()*1.0;
    }
}
