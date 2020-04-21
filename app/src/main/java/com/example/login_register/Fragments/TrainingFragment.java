package com.example.login_register.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login_register.Calorie.CountCalorie;
import com.example.login_register.MainActivity;
import com.example.login_register.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class TrainingFragment extends Fragment {
    private TextView mTvStart;
    private TextView mTvTime;
    private TextView mTvStep;
    private TextView mTvCalories;
    private String startTime,endTime;
    private long expendTime;
    private String startStep,endStep;
    private int expendStep;
    private double expendCalories;
    private int sex,height;
    private double weight;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvStart = view.findViewById(R.id.tv_start_training);
        mTvTime = view.findViewById(R.id.tv_time_training);
        mTvStep = view.findViewById(R.id.tv_step_training);
        mTvCalories = view.findViewById(R.id.tv_calorie_training);

        mSharedPreferences = getActivity().getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        sex = Integer.parseInt(mSharedPreferences.getString("sex",null));
        height = Integer.parseInt(mSharedPreferences.getString("height",null));
        weight = Double.parseDouble(mSharedPreferences.getString("weight",null));

        mTvStart.setText("Start");

        mTvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTvStart.getText().toString().equals("Start")){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    startTime = simpleDateFormat.format(date);
                    mTvStart.setText("Finish");
                    mTvTime.setText("开始时间： " + startTime);
                    startStep = MainActivity.dataBle;
                }else if(mTvStart.getText().toString().equals("Finish")){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(System.currentTimeMillis());
                    endTime = simpleDateFormat.format(date);
                    mTvStart.setText("Start");
                    endStep = MainActivity.dataBle;
                    expendTime = CountCalorie.getTimeExpend(startTime,endTime);
                    Log.d("training",startTime + " " + endTime);
                    long longHours = expendTime/(60 * 60 * 1000);
                    long longMinutes = (expendTime - longHours * (60 * 60 * 1000)) / (60 * 1000);
                    long longSeconds = (expendTime - longHours * (60 * 60 * 1000) - longMinutes * (60 * 1000)) / 1000;
                    mTvTime.setText("您训练了" + String.valueOf(longHours) + "H" + String.valueOf(longMinutes) + "M" + String.valueOf(longSeconds) + "S");
                    expendStep = Integer.parseInt(endStep) - Integer.parseInt(startStep);
                    mTvStep.setText("共走了" + expendStep + "步");
                    expendCalories = CountCalorie.calories(sex,height,weight,expendStep,expendTime / 1000);
                    mTvCalories.setText("消耗了" + String.format("%.2f",expendCalories) + "kcal");
                    Log.d("training" ,String.valueOf(expendTime) + " " + String.valueOf(expendStep) + " " +String.valueOf(expendCalories));
                }
            }
        });
    }
}
