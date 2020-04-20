package com.example.login_register.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login_register.CloudSQL.DBConnection;
import com.example.login_register.R;
import com.example.login_register.Utils.DateUtil;
import com.example.login_register.Utils.ToastUtil;
import com.example.login_register.Utils.WeekUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.smssdk.gui.IdentifyNumPage;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.STORAGE_SERVICE;

public class ChartFragment extends Fragment{
    private final static String TAG = "chart";
    private RadioGroup mRgDate;
    private BarChart barChart;
    private TextView mTvDate;
    private Button mBtnLeft;
    private Button mBtnRight;
    private TextView mTvDataShowTotal;
    private TextView mTvDataShowAverage;
    private List list;
    private String loginName;
    private int year,month,day,week;
    private String dateToday,weekToday,weekCheck;
    private int flag_date = 1;
    //private int totalNum = 0, averageNum = 0;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRgDate = view.findViewById(R.id.rg_choice);
        barChart = view.findViewById(R.id.bar_chart);
        mTvDate = view.findViewById(R.id.tv_date);
        mBtnLeft = view.findViewById(R.id.btn_choiceLeft);
        mBtnRight = view.findViewById(R.id.btn_choiceRight);
        mTvDataShowTotal = view.findViewById(R.id.tv_data_show1);
        mTvDataShowAverage = view.findViewById(R.id.tv_data_show2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.DriverConnection();
            }
        }).start();
        mSharedPreferences = getActivity().getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        loginName = mSharedPreferences.getString("RememberName",null);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = new Date(System.currentTimeMillis());
        dateToday = simpleDateFormat.format(date);
        mTvDate.setText(dateToday);
        weekToday = WeekUtil.getWeek(dateToday);
        Log.d(TAG,dateToday + weekToday);

        ChoiceDate();
        ReadFromDB1();
    }

    private void ChoiceDate(){
        mRgDate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if(radioButton.getText().equals("每日")){
                    flag_date = 1;
                    //每日
                    mTvDate.setTextSize(30);
                    mTvDate.setText(dateToday);
                    ReadFromDB1();
                }else if(radioButton.getText().equals("每周")){
                    flag_date = 2;
                    //每周
                    mTvDate.setText(dateToday);
                    int i = Integer.parseInt(weekToday.substring(4,5));
                    if(i == 7){
                        mTvDate.setTextSize(15);
                        weekCheck = dateToday;
                        mTvDate.setText(weekCheck + " ~ " + dateToday);
                    }else{
                        for(int j = 0; j < i; j++){
                            String dateShow = mTvDate.getText().toString();
                            year = Integer.parseInt(dateShow.substring(0,4));
                            month = Integer.parseInt(dateShow.substring(5,7));
                            day = Integer.parseInt(dateShow.substring(8,10));
                            weekCheck = DateUtil.reduceDay(year,month,day);
                            Log.d(TAG,weekCheck);
                            mTvDate.setTextSize(15);
                            mTvDate.setText(weekCheck + " ~ " +dateToday);
                        }
                    }
                    ReadFromDB2();
                }else{
                    flag_date = 3;
                    //每月
                    mTvDate.setTextSize(30);
                    mTvDate.setText(dateToday.substring(0,7));
                    ReadFromDB3();
                }
            }
        });

        mBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag_date == 1){
                    String dateShow = mTvDate.getText().toString();
                    year = Integer.parseInt(dateShow.substring(0,4));
                    month = Integer.parseInt(dateShow.substring(5,7));
                    day = Integer.parseInt(dateShow.substring(8,10));
                    String dateAfter = DateUtil.reduceDay(year,month,day);
                    mTvDate.setText(dateAfter);
                    Log.d(TAG,dateAfter);
                    ReadFromDB1();
                }else if(flag_date == 2){
                    String dateShow = mTvDate.getText().toString();
                    year = Integer.parseInt(dateShow.substring(0,4));
                    month = Integer.parseInt(dateShow.substring(5,7));
                    day = Integer.parseInt(dateShow.substring(8,10));
                    String dateAfterEnd = DateUtil.reduceDay(year,month,day);
                    for(int i = 0; i < 7; i++){
                        String dateShow2 = mTvDate.getText().toString();
                        year = Integer.parseInt(dateShow2.substring(0,4));
                        month = Integer.parseInt(dateShow2.substring(5,7));
                        day = Integer.parseInt(dateShow2.substring(8,10));
                        String dateAfterStart = DateUtil.reduceDay(year,month,day);
                        Log.d(TAG,i +" " + dateAfterStart);
                        String dateAfter = dateAfterStart + " ~ " + dateAfterEnd;
                        mTvDate.setText(dateAfter);
                    }
                    ReadFromDB2();
                }else if(flag_date == 3){
                    String dateShow = mTvDate.getText().toString();
                    Log.d(TAG,dateShow);
                    year = Integer.parseInt(dateShow.substring(0,4));
                    month = Integer.parseInt(dateShow.substring(5,7));
                    String dateAfter = DateUtil.reduceMonth(year,month);
                    mTvDate.setText(dateAfter);
                    Log.d(TAG,dateAfter);
                    ReadFromDB3();
                }
            }
        });

        mBtnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag_date == 1){
                    String dateShow = mTvDate.getText().toString();
                    year = Integer.parseInt(dateShow.substring(0,4));
                    month = Integer.parseInt(dateShow.substring(5,7));
                    day = Integer.parseInt(dateShow.substring(8,10));
                    if(dateShow.equals(dateToday)){
                        ToastUtil.showMsg(getActivity(),"没有更多啦");
                    }else{
                        mBtnRight.setVisibility(View.VISIBLE);
                        String dateAfter = DateUtil.addDay(year,month,day);
                        mTvDate.setText(dateAfter);
                        Log.d(TAG,dateAfter);
                    }
                    ReadFromDB1();
                }else if(flag_date == 2){
                    String dateShow = mTvDate.getText().toString();
                    int k = Integer.parseInt(weekCheck.substring(8,10)) - 1;
                    String strK = String.valueOf(k);
                    if(strK.length() == 1){
                        strK = "0" + strK;
                    }
                    if(dateShow.substring(0,10).equals(weekCheck)){
                        ToastUtil.showMsg(getActivity(),"没有更多啦");
                    }else if(dateShow.substring(13,23).equals(weekCheck.substring(0,8) + strK)){
                        mTvDate.setText(weekCheck + " ~ " + dateToday);
                    }else{
                        year = Integer.parseInt(dateShow.substring(13,17));
                        month = Integer.parseInt(dateShow.substring(18,20));
                        day = Integer.parseInt(dateShow.substring(21,23));
                        String dateAfterStart = DateUtil.addDay(year,month,day);
                        for(int i = 0; i < 7; i++){
                            String dateShow2 = mTvDate.getText().toString();
                            year = Integer.parseInt(dateShow2.substring(13,17));
                            month = Integer.parseInt(dateShow2.substring(18,20));
                            day = Integer.parseInt(dateShow2.substring(21,23));
                            String dateAfterEnd = DateUtil.addDay(year,month,day);
                            Log.d(TAG,i +" " + dateAfterEnd);
                            String dateAfter = dateAfterStart + " ~ " + dateAfterEnd;
                            mTvDate.setText(dateAfter);
                        }
                    }
                    ReadFromDB2();
                }else if(flag_date == 3){
                    String dateShow = mTvDate.getText().toString();
                    Log.d(TAG,dateShow);
                    year = Integer.parseInt(dateShow.substring(0,4));
                    month = Integer.parseInt(dateShow.substring(5,7));
                    if(dateShow.equals(dateToday.substring(0,7))){
                        ToastUtil.showMsg(getActivity(),"没有更多啦");
                    }else{
                        mBtnRight.setVisibility(View.VISIBLE);
                        String dateAfter = DateUtil.addMonth(year,month);
                        mTvDate.setText(dateAfter);
                        Log.d(TAG,dateAfter);
                    }
                    ReadFromDB3();
                }
            }
        });
    }


    private void ReadFromDB1(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dateShow = mTvDate.getText().toString().substring(0,10);
                list = DBConnection.ReadStepHour(dateShow,loginName);
                DrawChart1();
            }
        }).start();
    }

    private void DrawChart1() {
        barChart.invalidate();
        barChart.setDrawBorders(true);
        //barChart.setDrawGridBackground(false);
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            Log.d("drawChart", String.valueOf(i));
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
        String name = "当日每小时步数";
        BarDataSet barDataSet = new BarDataSet(barEntries, name);
        //initBarDataSet(barDataSet,getResources().getColor(R.color.colorBlack));
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        //averageNum = totalNum / list.size();
        //mTvDataShowTotal.setText("今日总步数 " + String.valueOf(totalNum));
        //barChart.animateXY(3000,3000);
    }

        private void ReadFromDB2(){
        list.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int week = Integer.parseInt(weekToday.substring(4,5));
                String dayEnd = mTvDate.getText().toString().substring(13,23);
                int weekEnd = Integer.parseInt(WeekUtil.getWeek(dayEnd).substring(4,5));
                String dateShow = mTvDate.getText().toString().substring(0,10);
                String startDay = mTvDate.getText().toString().substring(0,10);
                String sql = "";
                if(weekEnd == 6){
                    for(int i = 0; i < 7; i++){
                        year = Integer.parseInt(dateShow.substring(0,4));
                        month = Integer.parseInt(dateShow.substring(5,7));
                        day = Integer.parseInt(dateShow.substring(8,10));
                        Log.d("drawChart", "6 " + dateShow);
                        sql = sql + "time LIKE '" + dateShow + "%' OR ";
                        Log.d("drawChart", "6 " + sql);
                        dateShow = DateUtil.addDay(year,month,day);
                    }
                }else{
                    for(int i = 0; i < week; i++){
                        year = Integer.parseInt(dateShow.substring(0,4));
                        month = Integer.parseInt(dateShow.substring(5,7));
                        day = Integer.parseInt(dateShow.substring(8,10));
                        Log.d("drawChart", "7 "+ dateShow);
                        sql = sql + "time LIKE '" + dateShow + "%' OR ";
                        Log.d("drawChart", "6 " + sql);
                        dateShow = DateUtil.addDay(year,month,day);
                    }
                }
                sql = "(" + sql.substring(0,sql.length()-3) + ")";
                list = DBConnection.ReadStepWeekly(startDay,sql,loginName);
                DrawChart2();
            }
        }).start();
    }

    private void DrawChart2() {
        barChart.invalidate();
        //barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(true);
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Log.d("drawChart", String.valueOf(i));
            Log.d("drawChart", "drawchart2"+String.valueOf(list.size()));
            if(i >= list.size()){
                Log.d("drawChart", "drawchart2 0");
                barEntries.add(new BarEntry(i,Float.parseFloat("0")));
            }else{
                Log.d("drawChart", String.valueOf(list.get(i)));
                String step = String.valueOf(list.get(i));
                Log.d("DB_tag","list  " + step);
                barEntries.add(new BarEntry(i,Float.parseFloat(step)));
            }
        }
        String name = "当周每日步数";
        BarDataSet barDataSet = new BarDataSet(barEntries, name);
        //initBarDataSet(barDataSet,getResources().getColor(R.color.colorBlack));
        BarData data = new BarData(barDataSet);
        barChart.setData(data);

        //barChart.animateXY(3000,3000);
    }

    private void ReadFromDB3(){
        list.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dateShow = mTvDate.getText().toString().substring(0,7);
                list = DBConnection.ReadStepDaily(dateShow,loginName);
                DrawChart3();
            }
        }).start();
    }

    private void DrawChart3() {
        barChart.invalidate();
        ///barChart.setDrawGridBackground(false);
        int j= 0;
        barChart.setDrawBorders(true);
        List<BarEntry> barEntries = new ArrayList<>();
        int year = Integer.parseInt(mTvDate.getText().toString().substring(0,4));
        int month = Integer.parseInt(mTvDate.getText().toString().substring(5,7));
        if((year%4 == 0 && year%100 != 0 || year%400 == 0) && month == 2){
            j = 29;
        }else{
            switch (month){
                case 2:
                    j  = 28;
                    break;
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    j = 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                        j= 30;
                        break;
            }
        }

        for (int i = 0; i < j; i++) {
            Log.d("drawChart", String.valueOf(i));
            if(i >= list.size()){
                barEntries.add(new BarEntry(i,Float.parseFloat("0")));
            }else{
                Log.d("drawChart", String.valueOf(list.get(i)));
                String step = String.valueOf(list.get(i));
                Log.d("DB_tag","list  " + step);
                barEntries.add(new BarEntry(i,Float.parseFloat(step)));
            }
        }
        String name = "当月每日步数";
        BarDataSet barDataSet = new BarDataSet(barEntries, name);
        //initBarDataSet(barDataSet,getResources().getColor(R.color.colorBlack));
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        //barChart.animateXY(3000,3000);
    }

    private void initBarDataSet(BarDataSet barDataSet,int color){
        barDataSet.setColor(color);
        barDataSet.setFormLineWidth(1f);
        barDataSet.setFormSize(15.f);
        barDataSet.setDrawValues(false);
    }
}

