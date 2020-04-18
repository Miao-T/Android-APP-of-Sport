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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.login_register.CloudSQL.DBConnection;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.HidePsdUtil;
import com.example.login_register.Utils.MD5Util;
import com.example.login_register.Utils.NetworkListener;
import com.example.login_register.Utils.ReadData;
import com.example.login_register.Utils.TimeCountUtil;
import com.example.login_register.Utils.ToastUtil;

import org.litepal.LitePal;

import java.util.EnumMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ForgetPsdActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "message_tag" ;
    private EditText mEtAccount;
    private EditText mEtMessageMod;
    private Button mBtnGetMessage;
    private Button mBtnConfirm;

    private RelativeLayout mRl1,mRl2;
    private TextView mTvShowUsername;
    private EditText mEtPsd1Forget,mEtPsd2Forget;
    private CheckBox mCbHide1,mCbHide2;
    private TextView mTvPsdForget1,mTvPsdForget2;
    private Button mBtnChangePsd;

    EventHandler eventHandler;
    private TimeCountUtil mTimeCountUtil;
    private Boolean flag = true;
    private Boolean flagNumber = false;
    private String phoneNumber,messageCode,wholeNumber;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private NetworkListener networkListener;
    private boolean mNetworkResult;
    private String userName;
    private boolean mPsdResult = false, mPsdMatchResult = false;
    private String psdCloud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psd);

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

        Psd1TextChangeListener();
        Psd2TextChangeListener();
    }

    public void initView(){
        mEtAccount = findViewById(R.id.et_accountForget);
        mEtMessageMod = findViewById(R.id.et_CodeForget);
        mBtnGetMessage = findViewById(R.id.btn_get_message_Forget);
        mBtnConfirm = findViewById(R.id.btn_confirm_Forget);

        mRl1 = findViewById(R.id.relativeLayout_1);
        mRl2 = findViewById(R.id.relativeLayout_2);
        mTvShowUsername = findViewById(R.id.tv_text2);

        mTvPsdForget1 = findViewById(R.id.psd_Text_forget);
        mTvPsdForget2 = findViewById(R.id.psd_match_Text_forget);
        mEtPsd1Forget = findViewById(R.id.et_psd1_forget);
        mEtPsd2Forget = findViewById(R.id.et_psd2_forget);
        mCbHide1 = findViewById(R.id.cb_hide_forget);
        mCbHide2 = findViewById(R.id.cb_hide2_forget);
        mBtnChangePsd = findViewById(R.id.btn_changePsd);

        mBtnGetMessage.setOnClickListener(this);
        mBtnConfirm.setOnClickListener(this);
        mBtnChangePsd.setOnClickListener(this);
        networkListener = new NetworkListener();

        HidePsdUtil.ShowOrHide(mCbHide1,mEtPsd1Forget);
        HidePsdUtil.ShowOrHide(mCbHide2,mEtPsd2Forget);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_message_Forget:
                phoneNumber = mEtAccount.getText().toString().trim();
                CheckPhoneNumber();
                if(flagNumber){
                    mTimeCountUtil.start();
                    SMSSDK.getVerificationCode("86",phoneNumber);
                    mEtMessageMod.requestFocus();
                }else{
                    ToastUtil.showMsg(ForgetPsdActivity.this,"该手机号未注册，请先注册");
                    mEtAccount.setText("");
                }

//                Handler handler = new Handler();
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                };
//                handler.postDelayed(runnable,2000);
                break;

            case R.id.btn_confirm_Forget:
                mNetworkResult = networkListener.NetWorkState(ForgetPsdActivity.this);
                if(mNetworkResult){
                    messageCode = mEtMessageMod.getText().toString().trim();
                    SMSSDK.submitVerificationCode("86",phoneNumber,messageCode);
                    flag = false;
                }else{
                    ToastUtil.showMsg(ForgetPsdActivity.this,"请先打开移动数据，不然无法登录");
                }
                break;

            case R.id.btn_changePsd:
                mNetworkResult = networkListener.NetWorkState(ForgetPsdActivity.this);
                if(mNetworkResult){
                    ChangePsd();
                }else{
                    ToastUtil.showMsg(ForgetPsdActivity.this,"请先打开移动数据，不然无法注册");
                }
        }
    }

    private void ChangePsd(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String psd1 = mEtPsd1Forget.getText().toString().trim();
                String psd2 = mEtPsd2Forget.getText().toString().trim();
                String psdMD5 = MD5Util.encrypt(psd1);
                DBConnection.UpdateData(psdMD5,wholeNumber);
            }
        }).start();
        ToastUtil.showMsg(ForgetPsdActivity.this, "用户密码修改成功");
        Intent intent = new Intent(ForgetPsdActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void Psd1TextChangeListener(){
        mEtPsd1Forget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPsdResult = psdCheck(s.toString(), mTvPsdForget1);
                buttonEnable();
            }
        });
    }

    public boolean psdCheck(String psd, TextView psdText) {
        if (psd.length() != 0) {
            String test = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$";
            if (psd.matches(test)) {
                psdText.setText(R.string.psd_ok);
                psdText.setTextColor(getResources().getColor(R.color.colorBlack));
                //密码有效
                return true;
            } else {
                psdText.setText(R.string.psd_invalid_warning);
                psdText.setTextColor(getResources().getColor(R.color.colorAccent));
                //密码必须同时包含数字和字母，并且在6-10位之间
                return false;
            }
        } else {
            psdText.setText(R.string.psd_empty_warning);
            psdText.setTextColor(getResources().getColor(R.color.colorAccent));
            //密码不能为空
            return false;
        }
    }

    public void Psd2TextChangeListener(){
        mEtPsd2Forget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String psd1Match = mEtPsd1Forget.getText().toString();
                mPsdMatchResult = psdMatch(psd1Match, s.toString(), mTvPsdForget2);
                buttonEnable();
            }
        });
    }

    public boolean psdMatch(String psd1, String psd2, TextView matchText) {
        if (psd1.equals(psd2)) {
            matchText.setText(R.string.psd_match_ok);
            matchText.setTextColor(getResources().getColor(R.color.colorBlack));
            return true;
        } else {
            matchText.setText(R.string.psd_match_invalid_warning);
            matchText.setTextColor(getResources().getColor(R.color.colorAccent));
            return false;
        }
    }

    private void buttonEnable(){
        if (mPsdResult && mPsdMatchResult){
            mBtnChangePsd.setEnabled(true);
            mBtnChangePsd.setClickable(true);
        }else{
            mBtnChangePsd.setEnabled(false);
            mBtnChangePsd.setClickable(false);
        }
    }







    public void CheckPhoneNumber(){
        wholeNumber = "86" + phoneNumber;
        Log.d("Psd",wholeNumber);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReadData readData = new DBConnection();
                EnumMap<ReadData.UserInfoData,Object> userInfo = readData.ReadCloudData("",wholeNumber);
                userInfo.entrySet().iterator();
                psdCloud = String.valueOf(userInfo.get(ReadData.UserInfoData.password));
                userName = String.valueOf(userInfo.get(ReadData.UserInfoData.userName));
                Log.d("Psd",psdCloud);
                if(psdCloud.equals("null")){
//                    ToastUtil.showMsg(ForgetPsdActivity.this,"该手机号未注册，请先注册");
//                    mEtAccount.setText("");
//                    Log.d("Psd","false");
                    flagNumber = false;
                }else{
//                    mTimeCountUtil.start();
//                    SMSSDK.getVerificationCode("86",phoneNumber);
//                    mEtMessageMod.requestFocus();
//                    Log.d("Psd","true");
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
                        ToastUtil.showMsg(ForgetPsdActivity.this,"该手机号已经注册过，请重新输入");
                        mEtAccount.requestFocus();
                        return;
                    }
                }
            }
            if(result == SMSSDK.RESULT_COMPLETE){
                if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ReadData readData = new DBConnection();
//                            EnumMap<ReadData.UserInfoData,Object> userInfo = readData.ReadCloudData("",wholeNumber);
//                            userInfo.entrySet().iterator();
//                            userName = String.valueOf(userInfo.get(ReadData.UserInfoData.userName));
//                            mTvShowUsername.setText("用户名：" + userName);
//                        }
//                    }).start();
                    ToastUtil.showMsg(ForgetPsdActivity.this,"验证码输入正确");
                    mTvShowUsername.setText("用户名：" + userName);
                    mRl1.setVisibility(View.GONE);
                    mRl2.setVisibility(View.VISIBLE);
                    mBtnChangePsd.setEnabled(false);
                    mBtnChangePsd.setClickable(false);
                }else if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    Log.d("SMSSDK","succeed");
                }
            }else{
                if(flag){
                    mBtnGetMessage.setVisibility(View.VISIBLE);
                    ToastUtil.showMsg(ForgetPsdActivity.this,"验证码获取失败请重新获取");
                    mEtAccount.requestFocus();
                    Log.d("SMSSDK",String.valueOf(data));
                }else{
                    ToastUtil.showMsg(ForgetPsdActivity.this,"验证码输入错误");
                }
            }
        }

    };



}
