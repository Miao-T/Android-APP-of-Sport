package com.example.login_register.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WeekUtil {
    public static String getWeek(String dateTime){
        String week = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try{
            calendar.setTime(simpleDateFormat.parse(dateTime));
        }catch (ParseException e){
            e.printStackTrace();
        }
        if(calendar.get(Calendar.DAY_OF_WEEK) == 1){
            week = "week7";//Sunday
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == 2){
            week = "week1";//Monday
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == 3){
            week = "week2";//Tuesday
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == 4){
            week = "week3";//Wednesday
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == 5){
            week = "week4";//Thursday
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == 6){
            week = "week5";//Friday
        }else if(calendar.get(Calendar.DAY_OF_WEEK) == 7){
            week = "week6";//Saturday
        }
        return week;
    }
}
