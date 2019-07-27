package com.jit.sports.entry;

import java.util.ArrayList;
import java.util.List;

//su度点聚合
public class Speed {


    public static List<Double>polymerization(List<Double> lastSpeed,int frequency)
    {
        List<Double>newSpeed = new ArrayList<>();
        int size = lastSpeed.size();
        int len =  size%frequency != 0 ? (size / frequency+1) : size/frequency;
        int j=0;
        System.out.println(len);
        for(int i = 0; i < len ; i++)
        {
            System.out.println(j+"   "+frequency);

            if(j+frequency<size)
                newSpeed.add(getAverage(lastSpeed.subList(j,j+frequency)));
            else
                newSpeed.add(getAverage(lastSpeed.subList(j,size)));
            j = j+frequency;

        }
        return newSpeed;
    }
    public static double  getAverage(List<Double> speedNums)
    {
        System.out.println(speedNums);
        double sum=0;
        for (int i = 0; i < speedNums.size(); i++)
        {
            sum = sum +speedNums.get(i);
        }
        return sum/speedNums.size()*1.0;
    }
    public static void main(String[] args) {

      double [] a = {1,2,3,4,5,6,7,8,9,0,1 };
      List<Double>c =new ArrayList<>();
      for(int i=0;i < a.length ;i++)
      {
          c.add(a[i]);
      }
      List <Double>d=polymerization(c,2);

        System.out.println(d);


    }
}
