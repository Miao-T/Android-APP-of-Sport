package com.example.login_register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.login_register.Service.FloatWindowService;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.DoubleClickToExitUtil;
import com.example.login_register.Utils.ToastUtil;

public class MainActivity extends BaseActivity {

    private Button mBtnOffline;
    private Button mBtnBLE;
    private Button mBtnLitePal;
    private Button mBtnFloat;
    private Button mBtnBar;
    private Button mBtnPicker;
    private long mExitTime;
//    private SharedPreferences mSharedPreferences;
//    private SharedPreferences.Editor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnOffline = findViewById(R.id.btn_offline);
        mBtnBLE = findViewById(R.id.btn_BLE);
        mBtnLitePal = findViewById(R.id.btn_Database);
        mBtnFloat = findViewById(R.id.btn_Float);
        mBtnBar = findViewById(R.id.btn_Bar);
        mBtnPicker = findViewById(R.id.btn_Picker);
//        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
//        mEditor = mSharedPreferences.edit();

        mBtnOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.login.FORCE_OFFLINE");
//                mEditor.putBoolean("LoginBefore",false);
//                mEditor.apply();
                sendBroadcast(intent);
            }
        });

        mBtnBLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,BLEActivity.class);
                startActivity(intent);
            }
        });

        mBtnLitePal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LitePalActivity.class);
                startActivity(intent);
            }
        });

        mBtnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FloatWindowService.class);
                startService(intent);
                Log.d("Float","jump into float");
                finish();
            }
        });

        mBtnBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,BarActivity.class);
                startActivity(intent);
            }
        });

        mBtnPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterUserIfoActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //Object mHelperUtils;
                ToastUtil.showMsg(MainActivity.this,"再按一次退出APP");
                //System.currentTimeMillis()系统当前时间
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
