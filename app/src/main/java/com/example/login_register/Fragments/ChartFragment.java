package com.example.login_register.Fragments;

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

public class ChartFragment extends Fragment{
    private final static String TAG = "chart";
    private RadioGroup mRgDate;
    private BarChart barChart;
    private TextView mTvDate;
    private Button mBtnLeft;
    private Button mBtnRight;
    private List list;
    private int year,month,day,week;
    private String dateToday,weekToday,weekCheck;
    private int flag_date = 1;

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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        dateToday = simpleDateFormat.format(date);
        mTvDate.setText(dateToday);
        weekToday = WeekUtil.getWeek(dateToday);
        Log.d(TAG,dateToday + weekToday);

        ChoiceDate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.DriverConnection();
                list = DBConnection.ReadStep("2020-04-16","t");
                for(int i = 0; i < list.size(); i++){
                    Log.d("DB_tag","list  " + String.valueOf(list.get(i)));
                }
                DrawChart();
            }
        }).start();

//        Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                Log.d("DB_tag","drawChart");
//                DrawChart();
//            }
//        };
//        handler.postDelayed(runnable,3000);

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
                }else if(radioButton.getText().equals("每周")){
                    flag_date = 2;
                    //每周
                    mTvDate.setText(dateToday);
                    int i = Integer.parseInt(weekToday.substring(4,5));
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
                }else{
                    flag_date = 3;
                    //每月
                    mTvDate.setTextSize(30);
                    mTvDate.setText(dateToday.substring(0,7));
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
                }else if(flag_date == 3){
                    String dateShow = mTvDate.getText().toString();
                    Log.d(TAG,dateShow);
                    year = Integer.parseInt(dateShow.substring(0,4));
                    month = Integer.parseInt(dateShow.substring(5,7));
                    String dateAfter = DateUtil.reduceMonth(year,month);
                    mTvDate.setText(dateAfter);
                    Log.d(TAG,dateAfter);
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
                }
            }
        });
    }

    private void DrawChart(){
        barChart.setDrawBorders(true);
        List<BarEntry> barEntries = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            Log.d("DB_tag",String.valueOf(i));
            String step = String.valueOf(list.get(i));
            Log.d("DB_tag","list  " + step);
            barEntries.add(new BarEntry(i,Float.parseFloat(step)));
        }
        String name = "步数";
        BarDataSet barDataSet = new BarDataSet(barEntries,name);
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
    }
}
