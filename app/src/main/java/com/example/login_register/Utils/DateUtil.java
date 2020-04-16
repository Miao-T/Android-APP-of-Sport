package com.example.login_register.Utils;

public class DateUtil {

    public static String addDay(int year,int month,int day){
        String date;
        if(day == 28){
            switch (month){
                case 2:
                    if(year%4 == 0 && year%100 != 0 || year%400 == 0){
                        day = day +1;
                    }else {
                        day = 1;
                        month = month +1;
                    }
                    break;
                default:
                    day = day +1;
                    break;
            }
        }else if(day == 29){
            switch (month){
                case 2:
                    day = 1;
                    month = month + 1;
                    break;
                default:
                       day = day + 1;
                       break;
            }
        }else if(day == 30){
            switch (month){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    day = day+1;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    day = 1;
                    month = month + 1;
            }
        }else if(day == 31){
            switch (month){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                    day = 1;
                    month = month + 1;
                    break;
                case 12:
                    day = 1;
                    month = 1;
                    year = year + 1;
            }
        }else {
            day = day +1;
        }
        if(String.valueOf(month).length() == 1){
            if(String.valueOf(day).length() == 1){
                date = String.valueOf(year) + "-0" + String.valueOf(month) + "-0" +String.valueOf(day);
            }else{
                date = String.valueOf(year) + "-0" + String.valueOf(month) + "-" +String.valueOf(day);
            }
        }else{
            if(String.valueOf(day).length() == 1){
                date = String.valueOf(year) + "-" + String.valueOf(month) + "-0" +String.valueOf(day);
            }else{
                date = String.valueOf(year) + "-" + String.valueOf(month) + "-" +String.valueOf(day);
            }
        }
        return date;
    }

    public static String reduceDay(int year,int month,int day){
        String date;
        if(day == 1){
            switch (month){
                case 1:
                    day = 31;
                    month = 12;
                    year = year  - 1;
                    break;
                case 2:
                case 4:
                case 6:
                case 8:
                case 9:
                case 11:
                    day = 31;
                    month = month - 1;
                    break;
                case 3:
                    if(year%4 == 0 && year%100 != 0 || year%400 == 0){
                        day = 29;
                        month = month - 1;
                    }else {
                        day = 28;
                        month = month - 1;
                    }
                    break;
                case 5:
                case 7:
                case 10:
                case 12:
                    day = 30;
                    month = month - 1;
                    break;
            }
        }else {
            day = day - 1;
        }
        if(String.valueOf(month).length() == 1){
            if(String.valueOf(day).length() == 1){
                date = String.valueOf(year) + "-0" + String.valueOf(month) + "-0" +String.valueOf(day);
            }else{
                date = String.valueOf(year) + "-0" + String.valueOf(month) + "-" +String.valueOf(day);
            }
        }else{
            if(String.valueOf(day).length() == 1){
                date = String.valueOf(year) + "-" + String.valueOf(month) + "-0" +String.valueOf(day);
            }else{
                date = String.valueOf(year) + "-" + String.valueOf(month) + "-" +String.valueOf(day);
            }
        }
        return date;
    }

    public static String reduceMonth(int year,int month){
        String date;
        if(month == 1){
            month = 12;
            year = year -1;
        }
        else {
            month = month - 1;
        }

        if(String.valueOf(month).length() == 1){
            date = String.valueOf(year) + "-0" + month;
        }else{
            date = String.valueOf(year) + "-" + month;
        }
        return date;
    }

    public static String addMonth(int year,int month){
        String date;
        if(month == 12){
            month = 1;
            year = year + 1;
        }else{
            month = month + 1;
        }

        if(String.valueOf(month).length() == 1){
            date = String.valueOf(year) + "-0" + month;
        }else{
            date = String.valueOf(year) + "-" + month;
        }
        return date;
    }
}
