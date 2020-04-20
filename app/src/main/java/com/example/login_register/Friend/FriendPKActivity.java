package com.example.login_register.Friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.login_register.CloudSQL.DBConnection;
import com.example.login_register.MPAndroidChart.MyFormatter;
import com.example.login_register.R;
import com.example.login_register.Utils.DateUtil;
import com.example.login_register.Utils.ToastUtil;
import com.example.login_register.Utils.WeekUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FriendPKActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "chart";
    private String userA,userB;
    private TextView mTvName;
    private BarChart barChart;
    private TextView mTvDate;
    private Button mBtnLeft;
    private Button mBtnRight;
    private TextView mTvRate,mTvResult;
    private int year,month,day,week;
    private String dateToday,weekToday,weekCheck;
    private List listA,listB;
    private List<Integer> xAxisValue;
    private List<Float> yAxisValue1;
    private List<Float> yAxisValue2;
    private Handler handler;
    private static final int MessageText = 1;

    private int winDayA = 0;
    private int winDayB = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_pk);

        xAxisValue = new ArrayList<>();
        yAxisValue1 = new ArrayList<>();
        yAxisValue2 = new ArrayList<>();

        initView();
        Intent intent = getIntent();
        userA = intent.getStringExtra("loginName");
        userB = intent.getStringExtra("friendName");
        ToastUtil.showMsg(FriendPKActivity.this,userA + userB);
        mTvName.setText(userA + "  PK  " + userB);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = new Date(System.currentTimeMillis());
        dateToday = simpleDateFormat.format(date);
        weekToday = WeekUtil.getWeek(dateToday);
        Log.d(TAG,dateToday + weekToday);

        int i = Integer.parseInt(weekToday.substring(4,5));
        if(i == 7){
            mTvDate.setTextSize(15);
            weekCheck = dateToday;
            mTvDate.setText(weekCheck + " ~ " + dateToday);
        }else{
            for(int j = 0; j < i; j++){
                year = Integer.parseInt(dateToday.substring(0,4));
                month = Integer.parseInt(dateToday.substring(5,7));
                day = Integer.parseInt(dateToday.substring(8,10));
                weekCheck = DateUtil.reduceDay(year,month,day);
                Log.d(TAG,weekCheck);
                mTvDate.setText(weekCheck + " ~ " +dateToday);
            }
        }
        ReadFromDB2();

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MessageText:
                        DrawChart2();
                        chartDecoration();
                        break;
                }
            }
        };
    }

    private void initView(){
        mTvName = findViewById(R.id.tv_pk_name);
        barChart = findViewById(R.id.bar_chart_pk);
        mTvDate = findViewById(R.id.tv_date_pk);
        mBtnLeft = findViewById(R.id.btn_choiceLeft_pk);
        mBtnRight = findViewById(R.id.btn_choiceRight_pk);
        mTvRate = findViewById(R.id.tv_pk_rate);
        mTvResult = findViewById(R.id.tv_pk_result);

        mBtnLeft.setOnClickListener(this);
        mBtnRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_choiceLeft_pk:
                String dateShowLeft = mTvDate.getText().toString();
                year = Integer.parseInt(dateShowLeft.substring(0,4));
                month = Integer.parseInt(dateShowLeft.substring(5,7));
                day = Integer.parseInt(dateShowLeft.substring(8,10));
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
                break;
            case R.id.btn_choiceRight_pk:
                String dateShowRight = mTvDate.getText().toString();
                int k = Integer.parseInt(weekCheck.substring(8,10)) - 1;
                String strK = String.valueOf(k);
                if(strK.length() == 1){
                    strK = "0" + strK;
                }
                if(dateShowRight.substring(0,10).equals(weekCheck)){
                    ToastUtil.showMsg(FriendPKActivity.this,"没有更多啦");
                }else if(dateShowRight.substring(13,23).equals(weekCheck.substring(0,8) + strK)){
                    mTvDate.setText(weekCheck + " ~ " + dateToday);
                }else{
                    year = Integer.parseInt(dateShowRight.substring(13,17));
                    month = Integer.parseInt(dateShowRight.substring(18,20));
                    day = Integer.parseInt(dateShowRight.substring(21,23));
                    String dateAfterStart = DateUtil.addDay(year,month,day);
                    for(int i = 0; i < 7; i++){
                        String dateShow2 = mTvDate.getText().toString();
                        year = Integer.parseInt(dateShow2.substring(13,17));
                        month = Integer.parseInt(dateShow2.substring(18,20));
                        day = Integer.parseInt(dateShow2.substring(21,23));
                        String dateAfterEnd2 = DateUtil.addDay(year,month,day);
                        Log.d(TAG,i +" " + dateAfterEnd2);
                        String dateAfter = dateAfterStart + " ~ " + dateAfterEnd2;
                        mTvDate.setText(dateAfter);
                    }
                }
                ReadFromDB2();
                break;
        }
    }

    private void chartDecoration(){
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(true);
        barChart.setExtraBottomOffset(10);
        barChart.setExtraTopOffset(30);
        barChart.setFitBars(true);
        barChart.animateY(1500);

        XAxis xAxis = barChart.getXAxis();//获取x轴
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴标签显示位置
        xAxis.setDrawGridLines(false);//不绘制格网线
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelCount(xAxisValue.size());
//        IAxisValueFormatter xAxisValueFormatter = new MyFormatter();
//        xAxis.setValueFormatter(xAxisValueFormatter);



        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setDrawGridLines(true);

        Float yMin1 = Collections.min(yAxisValue1);
        Float yMin2 = Collections.min(yAxisValue2);
        Float yMax1 = Collections.max(yAxisValue1);
        Float yMax2 = Collections.max(yAxisValue2);
        Float yMin = Double.valueOf((yMin1 < yMin2 ? yMin1 : yMin2) * 0.1).floatValue();
        Float yMax = Double.valueOf((yMax1 > yMax2 ? yMax1 : yMax2) * 1.1).floatValue();
        leftAxis.setAxisMaximum(yMax);
        leftAxis.setAxisMinimum(yMin);
        rightAxis.setAxisMaximum(yMax);
        rightAxis.setAxisMinimum(yMin);
    }

    private void ReadFromDB2(){
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
                listA = DBConnection.ReadStepWeekly(startDay,sql,userA);
                listB = DBConnection.ReadStepWeekly(startDay,sql,userB);

                Message message = new Message();
                message.what = MessageText;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void DrawChart2() {
        float groupSpace = 0.04f;
        float barSpace = 0.03f;
        float barWidth = 0.45f;

        winDayA = 0;
        winDayB = 0;
        xAxisValue.clear();
        yAxisValue1.clear();
        yAxisValue2.clear();

        barChart.setDrawBorders(true);
        List<BarEntry> barEntriesA = new ArrayList<>();
        List<BarEntry> barEntriesB = new ArrayList<>();
        xAxisValue.clear();
        for (int i = 0; i < 7; i++) {
            Log.d("drawChart", String.valueOf(i));
            Log.d("drawChart", "drawchart2"+String.valueOf(listA.size()));
            xAxisValue.add(i);

            if(i >= listA.size()){
                Log.d("drawChart", "drawchart2 0");
                barEntriesA.add(new BarEntry(i,Float.parseFloat("0")));
                yAxisValue1.add(Float.parseFloat("0"));
            }else{
                Log.d("drawChart", String.valueOf(listA.get(i)));
                String stepA = String.valueOf(listA.get(i));
                Log.d("DB_tag","list  " + stepA);
                barEntriesA.add(new BarEntry(i,Float.parseFloat(stepA)));
                yAxisValue1.add(Float.parseFloat(stepA));
            }

            if(i >= listB.size()){
                Log.d("drawChart", "drawchart2 0");
                barEntriesB.add(new BarEntry(i,Float.parseFloat("0")));
                yAxisValue2.add(Float.parseFloat("0"));
            }else{
                Log.d("drawChart", String.valueOf(listB.get(i)));
                String stepB = String.valueOf(listB.get(i));
                Log.d("DB_tag","list  " + stepB);
                barEntriesB.add(new BarEntry(i,Float.parseFloat(stepB)));
                yAxisValue2.add(Float.parseFloat(stepB));
            }

            if(yAxisValue1.get(i) > yAxisValue2.get(i)){
                winDayA++;
            }else if(yAxisValue1.get(i) < yAxisValue2.get(i)){
                winDayB++;
            }
        }

        Log.d("TMD",winDayA + " " + winDayB);
        mTvRate.setText(userA + " : " + userB + "  =  " + winDayA + " : " + winDayB);
        if(winDayA > winDayB){
            mTvResult.setText(userA + "  PK胜利！");
        }else if(winDayA == winDayB){
            mTvResult.setText("平局！");
        }else{
            mTvResult.setText(userA + "  PK失败……");
        }



        String nameA = userA + "的步数";
        String nameB = userB + "的步数";
        BarDataSet barDataSetA;
        BarDataSet barDataSetB;

//        if(barChart.getData() != null && barChart.getData().getDataSetCount() > 0){
//            Log.d(TAG,"TMD" + "1");
//            barDataSetA = (BarDataSet)barChart.getData().getDataSetByIndex(0);
//            barDataSetB = (BarDataSet)barChart.getData().getDataSetByIndex(1);
//            barDataSetA.setValues(barEntriesA);
//            barDataSetB.setValues(barEntriesB);
//            barChart.getData().notifyDataChanged();
//            barChart.notifyDataSetChanged();
//        }else {
            Log.d(TAG,"TMD" + "2");
            barDataSetA = new BarDataSet(barEntriesA, nameA);
            barDataSetB = new BarDataSet(barEntriesB, nameB);
            barDataSetA.setColor(getResources().getColor(R.color.colorGrayDark));
            barDataSetB.setColor(getResources().getColor(R.color.colorAccent));

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(barDataSetA);
            dataSets.add(barDataSetB);
            BarData data = new BarData(dataSets);
            barChart.setData(data);
//        }

        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinimum(xAxisValue.get(0));
        barChart.getXAxis().setAxisMaximum(barChart.getBarData().getGroupWidth(groupSpace,barSpace) * xAxisValue.size() + xAxisValue.get(0));
        barChart.groupBars(xAxisValue.get(0),groupSpace,barSpace);
    }
}
