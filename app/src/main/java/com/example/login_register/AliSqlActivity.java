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
import com.example.login_register.Utils.ToastUtil;
import com.example.login_register.Utils.WeekUtil;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

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
                    DBConnection.AddRequestList("lindidi","qinhui");
                    //DBConnection.AddRequestList("miaobeibei","lindidi");
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
                        String userA = DBConnection.ReadRequestList0("lindidi");
                        Log.d("FriendList",userA);
                        ToastUtil.showMsg(AliSqlActivity.this,userA);
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
                        DBConnection.UpdateRequestState("1","miaobeibei","lindidi");
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
                        String name2 = DBConnection.ReadRequestListNo("miaowa");
                        //Log.d("FriendList","Yes"+name1);
                        Log.d("FriendList","No"+name2);
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
                        DBConnection.DeleteStep();
                    }
                }).start();
            }
        });

    }
}
