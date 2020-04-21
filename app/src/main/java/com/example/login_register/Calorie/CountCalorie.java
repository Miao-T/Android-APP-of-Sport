package com.example.login_register.Calorie;




import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CountCalorie {

    public static double calories(int sex,int height,double weight,int step,long time){
        //double stepSize = (height - 155.911) / 0.262;
        double stepSize = height * 0.45 * 0.01;
        double result;
        if(sex == 1){
            //female
            result = (0.662 * 2 * weight * stepSize * stepSize * step * step * step / (time * time * time)) + 0.042;
        }else{
            //male
            result = (0.517 * 2 * weight * stepSize * stepSize * step * step * step / (time * time * time)) + 0.058;
        }
        return result;
    }

    public static long getTimeMillis(String strTime){
        long returnMillis = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try{
            d = simpleDateFormat.parse(strTime);
            returnMillis = d.getTime();
        }catch (ParseException e){
            e.printStackTrace();
        }
        return returnMillis;
    }

    public static long getTimeExpend(String startTime,String endTime){
        long longStart = getTimeMillis((startTime));
        long longEnd = getTimeMillis(endTime);
        long longExpend = longEnd - longStart;
        return longExpend;
    }
}
