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
            return "一";
        }
        if (value == 2) {
            return "二";
        }
        if (value == 3) {
            return "三";
        }
        if (value == 4) {
            return "四";
        }
        if (value == 5) {
            return "五";
        }
        if (value == 6) {
            return "六";
        }
        if (value == 0) {
            return "日";
        }
        return "";
    }

    public int getDecimalDigits(){
        return 0;
    }
}
