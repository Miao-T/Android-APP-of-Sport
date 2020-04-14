package com.example.login_register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

import com.example.login_register.CloudSQL.DBConnection;

import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.Utils.ActivityCollector;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.ReadData;

import java.util.EnumMap;

public class AliSqlActivity extends BaseActivity{
    private EditText mEtId;
    private EditText mEtName;
    private Button mBtnSave,mBtnDelete,mBtnUpdate,mBtnRead,mBtnCreateTable,mBtnDropTable;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

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

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                @Override
                    public void run() {
                    DBConnection.InsertData();
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
                        String Name = mEtName.getText().toString();
                        DBConnection.Delete(Name);
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
                        DBConnection.DriverConnection();
                        DBConnection.UpdateData();
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
//                        DBConnection.DriverConnection();
//                        String Name = mEtName.getText().toString();
//                        ReadData readData = new DBConnection();
//                        EnumMap<ReadData.UserInfoData,Object> userInfo = readData.ReadCloudData(Name);
//                        userInfo.entrySet().iterator();
//                        String id = String.valueOf(userInfo.get(ReadData.UserInfoData.id));
//                        String registerTime = String.valueOf(userInfo.get(ReadData.UserInfoData.registerTime));
//                        Log.d("DB_tag",id + "  " + registerTime);

//                        DBConnection.DriverConnection();
//                        DBConnection.CheckNameRegistered(Name);
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
                        DBConnection.DriverConnection();
                        String Name = mEtName.getText().toString();
                        DBConnection.CreateTable(Name);
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
                        String Name = mEtName.getText().toString();
                        DBConnection.DriverConnection();
                        DBConnection.Drop(Name);
                    }
                }).start();
            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String Name = mEtName.getText().toString();
//                int Id = Integer.parseInt(mEtId.getText().toString());
//                DBConnection.link(Id,Name);
//            }
//        }).start();
    }
}
