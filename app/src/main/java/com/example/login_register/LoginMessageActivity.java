package com.example.login_register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.login_register.CloudSQL.DBConnection;
import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.ReadData;
import com.example.login_register.Utils.TimeCountUtil;
import com.example.login_register.Utils.ToastUtil;

import org.litepal.LitePal;

import java.sql.Time;
import java.util.EnumMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginMessageActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "message_tag" ;
    private EditText mEtAccount;
    private EditText mEtMessageMod;
    private Button mBtnGetMessage;
    private TextView mTvPsdLogin;
    private Button mBtnLogin;
    private Button mBtnRegister;
    EventHandler eventHandler;
    private TimeCountUtil mTimeCountUtil;
    private Boolean flag = true;
    private Boolean flagNumber = false;
    private String phoneNumber,messageCode,wholeNumber;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_message);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.DriverConnection();
            }
        }).start();
        LitePal.initialize(this);
        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
        initView();
        mTimeCountUtil = new TimeCountUtil(mBtnGetMessage, 60000, 1000);
        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    public void initView(){
        mEtAccount = findViewById(R.id.et_accountM);
        mEtMessageMod = findViewById(R.id.et_CodeM);
        mBtnGetMessage = findViewById(R.id.btn_get_message);
        mTvPsdLogin = findViewById(R.id.tv_passwordLogin);
        mBtnLogin = findViewById(R.id.btn_loginM);
        mBtnRegister = findViewById(R.id.btn_registerM);
        mBtnGetMessage.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mTvPsdLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_message:
                phoneNumber = mEtAccount.getText().toString().trim();
                CheckPhoneNumber();
                if(flagNumber){
                    mTimeCountUtil.start();
                    SMSSDK.getVerificationCode("86",phoneNumber);
                    mEtMessageMod.requestFocus();
                }else{
                    ToastUtil.showMsg(LoginMessageActivity.this,"该手机号未注册，请先注册");
                    mEtAccount.setText("  ");
                }
                break;
            case R.id.btn_loginM:
                messageCode = mEtMessageMod.getText().toString().trim();
                SMSSDK.submitVerificationCode("86",phoneNumber,messageCode);
                flag = false;
                break;
            case R.id.tv_passwordLogin:
                Intent intent = new Intent(LoginMessageActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_registerM:
                Intent intent2 = new Intent(LoginMessageActivity.this,RegisterActivity.class);
                startActivity(intent2);
                break;
        }
    }

    public void CheckPhoneNumber(){
        wholeNumber = "86" + phoneNumber;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReadData readData = new DBConnection();
                EnumMap<ReadData.UserInfoData,Object> userInfo = readData.ReadCloudData("",wholeNumber);
                userInfo.entrySet().iterator();
                String psdCloud = String.valueOf(userInfo.get(ReadData.UserInfoData.password));
                if(psdCloud.equals("null")){
                    flagNumber = false;
                }else{
                    flagNumber = true;
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

     Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data  = msg.obj;
            if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                if(result == SMSSDK.RESULT_COMPLETE){
                    boolean smart = (Boolean) data;
                    if(smart){
                        ToastUtil.showMsg(LoginMessageActivity.this,"该手机号已经注册过，请重新输入");
                        mEtAccount.requestFocus();
                        return;
                    }
                }
            }
            if(result == SMSSDK.RESULT_COMPLETE){
                if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ReadData readData = new DBConnection();
                            EnumMap<ReadData.UserInfoData,Object> userInfo = readData.ReadCloudData("",wholeNumber);
                            userInfo.entrySet().iterator();
                            String userName = String.valueOf(userInfo.get(ReadData.UserInfoData.userName));
                            mEditor.putString("RememberName",userName);
                            mEditor.putBoolean("has_login",true);
                            mEditor.apply();
                        }
                    }).start();
                    ToastUtil.showMsg(LoginMessageActivity.this,"验证码输入正确");
                    Intent intent = new Intent(LoginMessageActivity.this,MainActivity.class);
                    startActivity(intent);
                }else if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    Log.d("SMSSDK","succeed");
                }
            }else{
                if(flag){
                    mBtnGetMessage.setVisibility(View.VISIBLE);
                    ToastUtil.showMsg(LoginMessageActivity.this,"验证码获取失败请重新获取");
                    mEtAccount.requestFocus();
                    Log.d("SMSSDK",String.valueOf(data));
                }else{
                    ToastUtil.showMsg(LoginMessageActivity.this,"验证码输入错误");
                }
            }
        }

    };
}
