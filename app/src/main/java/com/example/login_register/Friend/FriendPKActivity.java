package com.example.login_register.Friend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.login_register.CloudSQL.DBConnection;
import com.example.login_register.R;
import com.example.login_register.Utils.DateUtil;
import com.example.login_register.Utils.ToastUtil;
import com.example.login_register.Utils.WeekUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private List list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_pk);

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
                String dateShow = mTvDate.getText().toString();
                year = Integer.parseInt(dateShow.substring(0,4));
                month = Integer.parseInt(dateShow.substring(5,7));
                day = Integer.parseInt(dateShow.substring(8,10));
                weekCheck = DateUtil.reduceDay(year,month,day);
                Log.d(TAG,weekCheck);
                mTvDate.setText(weekCheck + " ~ " +dateToday);
            }
        }
        //ReadFromDB2();
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
                //ReadFromDB2();
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
                //ReadFromDB2();
                break;
        }
    }
//    private void ReadFromDB2(){
//        list.clear();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int week = Integer.parseInt(weekToday.substring(4,5));
//                String dayEnd = mTvDate.getText().toString().substring(13,23);
//                int weekEnd = Integer.parseInt(WeekUtil.getWeek(dayEnd).substring(4,5));
//                String dateShow = mTvDate.getText().toString().substring(0,10);
//                String startDay = mTvDate.getText().toString().substring(0,10);
//                String sql = "";
//                if(weekEnd == 6){
//                    for(int i = 0; i < 7; i++){
//                        year = Integer.parseInt(dateShow.substring(0,4));
//                        month = Integer.parseInt(dateShow.substring(5,7));
//                        day = Integer.parseInt(dateShow.substring(8,10));
//                        Log.d("drawChart", "6 " + dateShow);
//                        sql = sql + "time LIKE '" + dateShow + "%' OR ";
//                        Log.d("drawChart", "6 " + sql);
//                        dateShow = DateUtil.addDay(year,month,day);
//                    }
//                }else{
//                    for(int i = 0; i < week; i++){
//                        year = Integer.parseInt(dateShow.substring(0,4));
//                        month = Integer.parseInt(dateShow.substring(5,7));
//                        day = Integer.parseInt(dateShow.substring(8,10));
//                        Log.d("drawChart", "7 "+ dateShow);
//                        sql = sql + "time LIKE '" + dateShow + "%' OR ";
//                        Log.d("drawChart", "6 " + sql);
//                        dateShow = DateUtil.addDay(year,month,day);
//                    }
//                }
//
//                sql = "(" + sql.substring(0,sql.length()-3) + ")";
//                list = DBConnection.ReadStepWeekly(startDay,sql,userA);
//                DrawChart2();
//            }
//        }).start();
//    }
//
//    private void DrawChart2() {
//        barChart.setDrawBorders(true);
//        List<BarEntry> barEntries = new ArrayList<>();
//        for (int i = 0; i < 7; i++) {
//            Log.d("drawChart", String.valueOf(i));
//            Log.d("drawChart", "drawchart2"+String.valueOf(list.size()));
//            if(i >= list.size()){
//                Log.d("drawChart", "drawchart2 0");
//                barEntries.add(new BarEntry(i,Float.parseFloat("0")));
//            }else{
//                Log.d("drawChart", String.valueOf(list.get(i)));
//                String step = String.valueOf(list.get(i));
//                Log.d("DB_tag","list  " + step);
//                barEntries.add(new BarEntry(i,Float.parseFloat(step)));
//            }
//        }
//        String name = "步数";
//        BarDataSet barDataSet = new BarDataSet(barEntries, name);
//        BarData data = new BarData(barDataSet);
//        barChart.setData(data);
//        barChart.animateXY(3000,3000);
//    }

}
