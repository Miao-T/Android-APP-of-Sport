package com.example.login_register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.login_register.CloudSQL.DBConnection;

import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.Utils.ActivityCollector;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.ReadData;
import com.example.login_register.Utils.ToastUtil;
import com.example.login_register.Utils.WeekUtil;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static com.example.login_register.CloudSQL.DBConnection.InsertStep;

public class AliSqlActivity extends BaseActivity{
    private EditText mEtId;
    private EditText mEtName;
    private Button mBtnSave,mBtnDelete,mBtnUpdate,mBtnRead,mBtnCreateTable,mBtnDropTable;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String userId,userName,psd,registerTime,phoneNumber,sex,height,weight,birthday,age,location,targetStep;
    private Handler handler;
    private static final int MessageText = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_sql);

        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        String a = mSharedPreferences.getString("DeviceMac",null);
        Log.d("ali",a);
        mEtId = findViewById(R.id.et_id_cloud);
        mEtName = findViewById(R.id.et_name_cloud);
        mBtnSave = findViewById(R.id.btn_save_cloud);
        mBtnUpdate = findViewById(R.id.btn_update_cloud);
        mBtnRead = findViewById(R.id.btn_read_cloud);
        mBtnDelete = findViewById(R.id.btn_delete_cloud);
        mBtnCreateTable = findViewById(R.id.btn_createTable_cloud);
        mBtnDropTable = findViewById(R.id.btn_dropTable_cloud);
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.DriverConnection();
            }
        }).start();

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d("ali_try",psd+userId+registerTime+phoneNumber+sex+height+weight+birthday+age+location+targetStep);
            }
        };

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                @Override
                    public void run() {
                    String date = mEtId.getText().toString();
                    InsertStep(date);
                }
            }).start();
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }).start();
            }
        });

        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }).start();
            }
        });


        mBtnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //String name1 = DBConnection.ReadRequestListYes("miaowa");
                    }
                }).start();
            }
        });

        mBtnCreateTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }).start();
            }
        });

        mBtnDropTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ReadData readData = new DBConnection();
                        EnumMap<ReadData.UserInfoData,Object> userInfo = readData.ReadUserInfo("lindidi","");
                        userInfo.entrySet().iterator();
                        psd = String.valueOf(userInfo.get(ReadData.UserInfoData.password));
                        userId = String.valueOf(userInfo.get(ReadData.UserInfoData.userId));
                        registerTime = String.valueOf(userInfo.get(ReadData.UserInfoData.registerDate));
                        phoneNumber = String.valueOf(userInfo.get(ReadData.UserInfoData.phoneNumber));
                        sex = String.valueOf(userInfo.get(ReadData.UserInfoData.sex));
                        height = String.valueOf(userInfo.get(ReadData.UserInfoData.height));
                        weight = String.valueOf(userInfo.get(ReadData.UserInfoData.weight));
                        birthday = String.valueOf(userInfo.get(ReadData.UserInfoData.birthday));
                        age = String.valueOf(userInfo.get(ReadData.UserInfoData.age));
                        location = String.valueOf(userInfo.get(ReadData.UserInfoData.location));
                        targetStep = String.valueOf(userInfo.get(ReadData.UserInfoData.targetStep));
                        Message message = new Message();
                        message.what = MessageText;
                        handler.sendMessage(message);
                        Log.d("ali",psd+userId+registerTime+phoneNumber+sex+height+weight+birthday+age+location+targetStep);
                    }
                }).start();
            }
        });

    }
}
