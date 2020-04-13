package com.example.login_register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.login_register.CloudSQL.DBConnection;
import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.HidePsdUtil;
import com.example.login_register.Utils.MD5Util;
import com.example.login_register.Utils.ReadData;
import com.example.login_register.Utils.ToastUtil;
import com.google.i18n.phonenumbers.*;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;

public class RegisterActivity extends BaseActivity {

    private EditText mUsername, mCountry, mPhoneNumber,mEmailAddress;
    private EditText mPassword1, mPassword2;
    private TextView mUsernameText, mPhoneText, mEmailText, mPsdText, mPsdMatchText;
    private Button mConfirm, mBack;
    private CheckBox mPsdHide;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Boolean mUsernameResult = false, mRepeatedResult = false, mPhoneResult = false, mPsdResult = false, mPsdMatchResult = false;
    private String strName,registerDate;

    /******************
     检验邮箱有效性
     ***********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.DriverConnection();
            }
        }).start();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        registerDate = simpleDateFormat.format(date);

        initView();
        mConfirm.setEnabled(false);
        mConfirm.setClickable(false);
        LitePal.initialize(this);
        UsernameTextChangeListener();
        PhoneNumTextChangeListener();
        Psd1TextChangeListener();
        Psd2TextChangeListener();
        HidePsdUtil.ShowOrHide(mPsdHide,mPassword1);

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String username = mUsername.getText().toString().trim();
                            String phoneNumber = mPhoneNumber.getText().toString().trim();
                            String phoneCountry = mCountry.getText().toString().trim();
                            String phoneNum = phoneCountry + phoneNumber;

                            ReadData readData = new DBConnection();
                            EnumMap<ReadData.UserInfoData,Object> userInfo = readData.ReadCloudData(username,phoneNum);
                            userInfo.entrySet().iterator();
                            String psd = String.valueOf(userInfo.get(ReadData.UserInfoData.password));
                            Log.d("DB_tag",psd);

                            if(psd.equals("null")){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String username = mUsername.getText().toString().trim();
                                        String phoneNumber = mPhoneNumber.getText().toString().trim();
                                        String phoneCountry = mCountry.getText().toString().trim();
                                        String phoneNum = phoneCountry + phoneNumber;
                                        String emailAddress = mEmailAddress.getText().toString().trim();
                                        String psd1 = mPassword1.getText().toString().trim();
                                        String psd2 = mPassword2.getText().toString().trim();
                                        String psdMD5 = MD5Util.encrypt(psd1);
                                        DBConnection.RegisterAccount(username,phoneNum,emailAddress,psdMD5,registerDate);
                                        DBConnection.CreateTable(username);
                                        //恭喜您成为第N个用户
                                    }
                                }).start();
                                ToastUtil.showMsg(RegisterActivity.this, "账户注册成功");
                                Intent intent = new Intent(RegisterActivity.this, RegisterUserInfoActivity.class);
                                intent.putExtra("RegisterName",username);
                                startActivity(intent);
                            }else{
                                ToastUtil.showMsg(RegisterActivity.this, "用户名或手机号已被注册");
                            }

//                            UserInfo userInfo = new UserInfo();
//                            userInfo.setUsername(username);
//                            userInfo.setPassword(psdMD5);
//                            userInfo.setPhoneNumber(phoneCountry + phoneNumber);
//                            userInfo.save();
                        }
                    }).start();

            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        mUsername = findViewById(R.id.et_username);
        mCountry = findViewById(R.id.et_country);
        mPhoneNumber = findViewById(R.id.et_telephone);
        mEmailAddress = findViewById(R.id.et_email);
        mPassword1 = findViewById(R.id.et_psd1);
        mPassword2 = findViewById(R.id.et_psd2);
        mUsernameText = findViewById(R.id.name_Text);
        mPhoneText = findViewById(R.id.phone_Text);
        mPsdText = findViewById(R.id.psd_Text);
        mPsdMatchText = findViewById(R.id.psd_match_Text);
        mPsdHide = findViewById(R.id.cb_hide);
        mConfirm = findViewById(R.id.btn_confirm);
        mBack = findViewById(R.id.btn_back);

        // mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void UsernameTextChangeListener(){
        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mUsernameResult = nameCheck(s.toString(), mUsernameText);
                buttonEnable();
            }
        });
    }

    public boolean nameRegistered(String name) {
        List<UserInfo> userInfos = LitePal.findAll(UserInfo.class);
        boolean flag = true;

        for (UserInfo userInfo : userInfos) {
            if (name.equals(userInfo.getUsername())) {
                flag = false;
                break;
                //已注册
            } else {
                flag = true;
                //未注册
            }
        }

        if(flag){
            return true;
            //未注册
        }else{
            return true;
            //已注册
        }
    }

    public boolean nameCheck(String name, TextView nameText) {
        if (name.length() != 0) {
            String test = "^[a-zA-Z][a-zA-Z0-9_]{5,10}$";
            //boolean isUsed = nameRegistered(name);
            if (name.matches(test)){
                nameText.setText(R.string.username_ok);
                nameText.setTextColor(getResources().getColor(R.color.colorBlack));
                //用户名有效
                return true;
//            } else if(!mRepeatedResult){
//                nameText.setText(R.string.username_has_been_registered);
//                nameText.setTextColor(getResources().getColor(R.color.colorAccent));
//                return false;
            }else{
                nameText.setText(R.string.username_invalid_warning);
                nameText.setTextColor(getResources().getColor(R.color.colorAccent));
                return false;
            }
        } else {
            nameText.setText(R.string.username_empty_warning);
            nameText.setTextColor(getResources().getColor(R.color.colorAccent));
            return false;
        }
    }

    public void PhoneNumTextChangeListener(){
        mPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String countryCode = mCountry.getText().toString();
                String phoneNumCode = "+" + countryCode + s.toString();
                mPhoneResult = libphonenumber(phoneNumCode, countryCode);
                if (mPhoneResult) {
                    mPhoneText.setText(R.string.phone_ok);
                    mPhoneText.setTextColor(getResources().getColor(R.color.colorBlack));
                } else {
                    mPhoneText.setText(R.string.phone_invalid_warning);
                    mPhoneText.setTextColor(getResources().getColor(R.color.colorAccent));
                }
                buttonEnable();
            }
        });
    }

    public static boolean libphonenumber(String phoneNumber, String countryCode) {
        System.out.println("isPhoneNumberValid: " + phoneNumber + "/" + countryCode);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryCode);
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            System.err.println("isPhoneNumberValid NumberParseException was thrown: " + e.toString());
            return false;
        }
    }

    public void Psd1TextChangeListener(){
        mPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPsdResult = psdCheck(s.toString(), mPsdText);
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
        mPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String psd1Match = mPassword1.getText().toString();
                mPsdMatchResult = psdMatch(psd1Match, s.toString(), mPsdMatchText);
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
        if (mUsernameResult && mPhoneResult && mPsdResult && mPsdMatchResult){
            mConfirm.setEnabled(true);
            mConfirm.setClickable(true);
        }else{
            mConfirm.setEnabled(false);
            mConfirm.setClickable(false);
        }
    }
}
