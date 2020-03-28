package com.example.login_register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.HidePsdUtil;
import com.example.login_register.Utils.ToastUtil;
import com.google.i18n.phonenumbers.*;

import org.litepal.LitePal;

import java.util.List;

public class RegisterActivity extends BaseActivity {

    private EditText mUsername, mCountry, mPhoneNumber;
    private EditText mPassword1, mPassword2;
    private TextView mUsernameText, mPhoneText, mPsdText, mPsdMatchText;
    private Button mConfirm, mBack;
    private CheckBox mPsdHide;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Boolean mUsernameResult, mPhoneResult, mPsdResult, mPsdMatchResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        LitePal.initialize(this);
        UsernameTextChangeListener();
        PhoneNumTextChangeListener();
        Psd1TextChangeListener();
        Psd2TextChangeListener();
        HidePsdUtil.ShowOrHide(mPsdHide,mPassword1);

//        mConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String username = mUsername.getText().toString();
//                String phoneNumber = mPhoneNumber.getText().toString();
//                String psd1 = mPassword1.getText().toString();
//                String psd2 = mPassword2.getText().toString();
//                // mPsdMatchResult = psdMatch(psd1,psd2);
//
//                if (mUsernameResult && mPhoneResult && mPsdResult && mPsdMatchResult) {
//                    mEditor.putString("RegisterAccount", username);
//                    mEditor.putString("RegisterTelephone", phoneNumber);
//                    mEditor.putString("RegisterPsd", psd1);
//                    mEditor.apply();
//                    ToastUtil.showMsg(RegisterActivity.this, "账户注册成功");
//                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                    startActivity(intent);
//                } else {
//                    mPassword1.setText("");
//                    mPassword2.setText("");
//                    mEditor.clear();
//                    ToastUtil.showMsg(RegisterActivity.this, "账户注册有误");
//                }
//            }
//        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String phoneNumber = mPhoneNumber.getText().toString();
                String phoneCountry = mCountry.getText().toString();
                String psd1 = mPassword1.getText().toString();
                String psd2 = mPassword2.getText().toString();

                if (mUsernameResult && mPhoneResult && mPsdResult && mPsdMatchResult) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUsername(username);
                    userInfo.setPassword(psd1);
                    userInfo.setPhoneNumber(phoneCountry + phoneNumber);
                    userInfo.save();
                    ToastUtil.showMsg(RegisterActivity.this, "账户注册成功");
                    Intent intent = new Intent(RegisterActivity.this, OptionPickerActivity.class);
                    startActivity(intent);
                } else {
                    mPassword1.setText("");
                    mPassword2.setText("");
                    ToastUtil.showMsg(RegisterActivity.this, "账户注册有误");
                }
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
            }
        });
    }

    public boolean nameUsed(String name) {
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
            return false;
            //已注册
        }
    }
    public boolean nameCheck(String name, TextView nameText) {
        if (name.length() != 0) {
            String test = "^[a-zA-Z][a-zA-Z0-9_]{5,10}$";
            boolean isUsed = nameUsed(name);
            if (name.matches(test) && isUsed){
                nameText.setText(R.string.username_ok);
                nameText.setTextColor(getResources().getColor(R.color.colorBlack));
                //用户名有效
                return true;
            } else if(!isUsed){
                nameText.setText(R.string.username_has_been_registered);
                nameText.setTextColor(getResources().getColor(R.color.colorAccent));
                return false;
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


}
