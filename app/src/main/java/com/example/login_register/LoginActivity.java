package com.example.login_register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.login_register.BLE.ScanBLEActivity;
import com.example.login_register.CloudSQL.DBConnection;
import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.MD5Util;
import com.example.login_register.Utils.NetworkListener;
import com.example.login_register.Utils.ReadData;
import com.example.login_register.Utils.ToastUtil;

import org.litepal.LitePal;
import org.litepal.annotation.Encrypt;

import java.util.EnumMap;
import java.util.List;

import static java.security.AccessController.getContext;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtName,mEtPassword;
    private Button mBtnLogin,mBtnRegister;
    private CheckBox mCbRememberPsd;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private TextView mTvMessage;
    private String psdCloud,psdInput,psdMD5Input;
    private NetworkListener networkListener;
    private boolean mNetworkResult;
    private String userId,userName,registerData,phoneNumber,sex,height,weight,birthday,age,location,targetStep;
    private Handler handler;
    private static final int MessageText = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedPreferences();
        auto_login();
        setContentView(R.layout.activity_login);
        LitePal.initialize(this);
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.DriverConnection();
            }
        }).start();
        rememberPsd();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MessageText:
                        String nameInput = mEtName.getText().toString().trim();
                        Log.d("DB_tag", psdCloud);
                        if (psdCloud.equals("null")) {
                            ToastUtil.showMsg(LoginActivity.this, "用户名未注册，请先注册");
                            mCbRememberPsd.setChecked(false);
                            mEtPassword.setText("");
                        } else {
                            if (psdMD5Input.equals(psdCloud)) {
                                if (mCbRememberPsd.isChecked()) {
                                    mEditor.putBoolean("is_remember", true);
                                    mEditor.putString("RememberPsd", psdMD5Input);
                                } else {
                                    mEditor.remove("RememberPsd");
                                    mEditor.putBoolean("is_remember", false);
                                }
                                mEditor.putString("RememberName", nameInput);
                                mEditor.putBoolean("has_login", true);
                                Log.d("ali",psdCloud+userId+registerData+phoneNumber+sex+height+weight+birthday+age+location+targetStep);
                                mEditor.putString("userId",userId);
                                mEditor.putString("registerData",registerData);
                                mEditor.putString("phoneNumber",phoneNumber);
                                mEditor.putString("sex",sex);
                                mEditor.putString("height",height);
                                mEditor.putString("weight",weight);
                                mEditor.putString("birthday",birthday);
                                mEditor.putString("age",age);
                                mEditor.putString("location",location);
                                mEditor.putString("targetStep",targetStep);
                                mEditor.apply();
                                Intent intent = new Intent(LoginActivity.this, ScanBLEActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                ToastUtil.showMsg(LoginActivity.this, "密码不正确");
                                mCbRememberPsd.setChecked(false);
                                mEtPassword.setText("");
                            }
                        }
                        break;
                }
            }
        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                mNetworkResult = networkListener.NetWorkState(LoginActivity.this);
                if(mNetworkResult){
                    login();
                }else{
                    ToastUtil.showMsg(LoginActivity.this,"请先打开移动数据，不然无法登录");
                }
                break;
            case R.id.btn_register:
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_message:
                Intent intent2 = new Intent(LoginActivity.this,LoginMessageActivity.class);
                startActivity(intent2);
                break;
        }
    }

    private void initSharedPreferences(){
        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    private void initView(){
        mEtName = findViewById(R.id.et_account);
        mEtPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register);
        mCbRememberPsd = findViewById(R.id.cb_reme);
        mTvMessage = findViewById(R.id.tv_message);
        mTvMessage.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
//        mEditor = mSharedPreferences.edit();

        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mTvMessage.setOnClickListener(this);

        networkListener = new NetworkListener();
    }


    private void rememberPsd(){
        boolean is_remember = mSharedPreferences.getBoolean("is_remember",false);
        if(is_remember){
            String username = mSharedPreferences.getString("RememberName","");
            String password = mSharedPreferences.getString("RememberPsd",null);
            mEtName.setText(username);
            mEtPassword.setText(password);
            mCbRememberPsd.setChecked(true);
        }
    }

    private void auto_login(){
        boolean has_login = mSharedPreferences.getBoolean("has_login",false);
        String RememberDevice = mSharedPreferences.getString("DeviceMac","");
        String FlagDevice = "flag" + RememberDevice;
        Log.d("DB_tag","flag"+ RememberDevice);
        if(has_login){
            if(FlagDevice.equals("flag")){
                Log.d("DB_tag",RememberDevice);
                Intent intent = new Intent(LoginActivity.this, ScanBLEActivity.class);
                startActivity(intent);
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        LoginActivity.this.finish();
                    }
                };
                handler.postDelayed(runnable,2000);
            }else{
                Log.d("DB_tag",RememberDevice);
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        LoginActivity.this.finish();
                    }
                };
                handler.postDelayed(runnable,2000);
            }

//            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//            startActivity(intent);
//            Handler handler = new Handler();
//            Runnable runnable = new Runnable() {
//                @Override
//                public void run() {
//                    LoginActivity.this.finish();
//                }
//            };
//            handler.postDelayed(runnable,2000);
        }
    }

    private void login(){
        psdInput = mEtPassword.getText().toString().trim();
        if(psdInput.length() < 32){
            psdMD5Input = MD5Util.encrypt(psdInput);
        }else{
            psdMD5Input = psdInput;
        }
//        boolean flag = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                String nameInput = mEtName.getText().toString().trim();

                ReadData readData = new DBConnection();
                EnumMap<ReadData.UserInfoData,Object> userInfo = readData.ReadUserInfo(nameInput,"");
                userInfo.entrySet().iterator();
                psdCloud = String.valueOf(userInfo.get(ReadData.UserInfoData.password));
                userId = String.valueOf(userInfo.get(ReadData.UserInfoData.userId));
                registerData = String.valueOf(userInfo.get(ReadData.UserInfoData.registerDate));
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
//                Log.d("DB_tag",psdCloud);
//                if(psdCloud.equals("null")){
//                    ToastUtil.showMsg(LoginActivity.this,"用户名未注册，请先注册");
//                    mCbRememberPsd.setChecked(false);
//                    mEtPassword.setText("");
//                }else{
//                    if(psdMD5Input.equals(psdCloud)){
//                        if(mCbRememberPsd.isChecked()){
//                            mEditor.putBoolean("is_remember",true);
//                            mEditor.putString("RememberPsd",psdMD5Input);
//                        }else{
//                            mEditor.remove("RememberPsd");
//                            mEditor.putBoolean("is_remember",false);
//                        }
//                        mEditor.putString("RememberName",nameInput);
////                        mEditor.putString("userId",userId);
////                        mEditor.putString("registerTime",registerTime);
////                        mEditor.putString("phoneNumber",phoneNumber);
////                        mEditor.putString("sex",sex);
////                        mEditor.putString("height",height);
////                        mEditor.putString("weight",weight);
////                        mEditor.putString("birthday",birthday);
////                        mEditor.putString("age",age);
////                        mEditor.putString("location",location);
////                        mEditor.putString("targetStep",targetStep);
//                        mEditor.putBoolean("has_login",true);
//                        mEditor.apply();
//                        Intent intent = new Intent(LoginActivity.this,ScanBLEActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }else{
//                        ToastUtil.showMsg(LoginActivity.this,"密码不正确");
//                        mCbRememberPsd.setChecked(false);
//                        mEtPassword.setText("");
//                    }
//                }
            }
        }).start();


//        List<UserInfo> userInfos = LitePal.findAll(UserInfo.class);
//        for (UserInfo userInfo : userInfos) {
//            if (name.equals(userInfo.getUsername()) && psdMD5.equals(userInfo.getPassword())) {
//                flag = true;
//                break;
//            }else{
//                flag = false;
//            }
//        }


//        if(flag){
//            if(mCbRememberPsd.isChecked()){
//                mEditor.putBoolean("is_remember",true);
//                mEditor.putString("RememberName",nameInput);
//                mEditor.putString("RememberPsd",psdMD5Input);
//            }else{
//                mEditor.remove("RememberName");
//                mEditor.remove("RememberPsd");
//                mEditor.putBoolean("is_remember",false);
//            }
//            mEditor.putBoolean("has_login",true);
//            mEditor.apply();
//            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//            startActivity(intent);
//            finish();
//        }else{
//            ToastUtil.showMsg(LoginActivity.this,"Account or password is invalid");
//            mCbRememberPsd.setChecked(false);
//            mEtPassword.setText("");
//        }
    }
}
