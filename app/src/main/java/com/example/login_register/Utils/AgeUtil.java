package com.example.login_register.Utils;

public class AgeUtil {
    public static int getAge(int yearNow,int yearBirth,int monthNow,int monthBirth,int dayNow,int dayBirth){
        int year = yearNow - yearBirth;
        int month = monthNow - monthBirth;
        int day = dayNow - dayBirth;
        int age = year + 1;
        if(month < 0){
            age--;
        }else if(month == 0){
            if(day < 0){
                age--;
            }
        }
        return age;
    }
}
