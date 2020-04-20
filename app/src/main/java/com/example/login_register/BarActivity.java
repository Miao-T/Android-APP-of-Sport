package com.example.login_register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.example.login_register.CloudSQL.DBConnection;
import com.example.login_register.MPAndroidChart.MyFormatter;
import com.example.login_register.Utils.BaseActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import java.util.ArrayList;
import java.util.List;

public class BarActivity extends BaseActivity {
//    LineChart lineChart;

    private BarChart barChart;
    private List list;
    private Handler handler;
    private static final int MessageText = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);

        barChart = findViewById(R.id.bar_chart);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(true);
        //x坐标轴设置
        //IAxisValueFormatter xAxisFormatter = new StringAxisValueFormatter(xAxisValue);//设置自定义的x轴值格式化器
        XAxis xAxis = barChart.getXAxis();//获取x轴
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴标签显示位置
        xAxis.setDrawGridLines(false);//不绘制格网线
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        //xAxis.setValueFormatter(xAxisFormatter);
        //xAxis.setTextSize(xAxisTextSize);//设置标签字体大小
        //xAxis.setLabelCount(xAxisValue.size());//设置标签显示的个数

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);


        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setDrawGridLines(true);

        barChart.setFitBars(true);
        barChart.animateY(2000);

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MessageText:
                        DrawChart();
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.DriverConnection();
                list = DBConnection.ReadStepHour("2020-04-17","lindidi");

                Message message = new Message();
                message.what = MessageText;
                handler.sendMessage(message);
                //DrawChart();
                //list = DBConnection.ReadStep("2020-04-16","t");
//                for(int i = 0; i < list.size(); i++){
//                    Log.d("DB_tag","list  " + String.valueOf(list.get(i)));
//                }
            }
        }).start();

//        barChart.setDrawBorders(true);
//        //设置数据
//        List<BarEntry> entries = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            float a = (float) (Math.random()) * 80;
//            entries.add(new BarEntry(i, (float) (Math.random()) * 80));
//            Log.d("DB_tag",String.valueOf(a));
//        }
//        //一个LineDataSet就是一条线
//        String name = "步数";
//        BarDataSet barDataSet = new BarDataSet(entries,name);
//        BarData data = new BarData(barDataSet);
//        barChart.setData(data);
    }

    private void DrawChart(){
        barChart.setDrawBorders(true);
        List<BarEntry> barEntries = new ArrayList<>();
        for(int i = 0; i < 24; i++){
            if(i >= list.size()){
                barEntries.add(new BarEntry(i,Float.parseFloat("0")));
            }else{
                //totalNum = Integer.parseInt(String.valueOf(list.get(i))) + totalNum;
                Log.d("drawChart", String.valueOf(list.get(i)));
                String step = String.valueOf(list.get(i));
                Log.d("DB_tag","list  " + step);
                barEntries.add(new BarEntry(i,Float.parseFloat(step)));
            }
        }
        String name = "步数";
        BarDataSet barDataSet = new BarDataSet(barEntries,name);
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
    }

}
