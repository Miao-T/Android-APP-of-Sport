package com.example.login_register.MPAndroidChart;

import com.github.mikephil.charting.components.AxisBase;

import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class MyFormatter implements IAxisValueFormatter{
    private DecimalFormat mFormat;
    String[] strings;

    public MyFormatter(){
        mFormat = new DecimalFormat("###,###,##0.0");
    }

    public MyFormatter(String[] strings){
        this.strings = strings;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (value == 1){
            return "Monday";
        }
        if (value == 2) {
            return "Tuesday";
        }
        if (value == 3) {
            return "Wednesday";
        }
        if (value == 4) {
            return "Thursday";
        }
        if (value == 5) {
            return "Friday";
        }
        if (value == 6) {
            return "Saturday";
        }
//        if (value == 7) {
//            return "Sunday";
//        }
        return "";
    }

    public int getDecimalDigits(){
        return 0;
    }
}
