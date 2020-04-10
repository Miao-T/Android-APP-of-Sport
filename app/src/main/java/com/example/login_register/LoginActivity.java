package com.example.login_register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.UserManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.MD5Util;
import com.example.login_register.Utils.ToastUtil;

import org.litepal.LitePal;
import org.litepal.annotation.Encrypt;

import java.util.List;

import static java.security.AccessController.getContext;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtName,mEtPassword;
    private Button mBtnLogin,mBtnRegister;
    private CheckBox mCbRememberPsd;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private TextView mTvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LitePal.initialize(this);
        initView();
        rememberPsd();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                login();
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


    public void initView(){
        mEtName = findViewById(R.id.et_account);
        mEtPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register);
        mCbRememberPsd = findViewById(R.id.cb_reme);
        mTvMessage = findViewById(R.id.tv_message);
        //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mTvMessage.setOnClickListener(this);
    }


    public void rememberPsd(){
        boolean is_remember = mSharedPreferences.getBoolean("is_remember",false);
        boolean has_login = mSharedPreferences.getBoolean("has_login",false);
        if(is_remember){
            String username = mSharedPreferences.getString("RememberName","");
            String password = mSharedPreferences.getString("RememberPsd","");
            mEtName.setText(username);
            mEtPassword.setText(password);
            mCbRememberPsd.setChecked(true);
            if(has_login){
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                //finish();
            }
        }
    }


    public void login(){
        String name = mEtName.getText().toString().trim();
        String psd = mEtPassword.getText().toString().trim();
        String psdMD5;
        if(psd.length() < 32){
            psdMD5 = MD5Util.encrypt(psd);
        }else{
            psdMD5 = psd;
        }
        boolean flag = false;
        List<UserInfo> userInfos = LitePal.findAll(UserInfo.class);
        for (UserInfo userInfo : userInfos) {
            if (name.equals(userInfo.getUsername()) && psdMD5.equals(userInfo.getPassword())) {
                flag = true;
                break;
            }else{
                flag = false;
            }
        }
        if(flag){
            if(mCbRememberPsd.isChecked()){
                mEditor.putBoolean("is_remember",true);
                mEditor.putString("RememberName",name);
                mEditor.putString("RememberPsd",psdMD5);
            }else{
                mEditor.remove("RememberName");
                mEditor.remove("RememberPsd");
                mEditor.putBoolean("is_remember",false);
            }
            mEditor.putBoolean("has_login",true);
            mEditor.apply();
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            ToastUtil.showMsg(LoginActivity.this,"Account or password is invalid");
            mCbRememberPsd.setChecked(false);
            mEtPassword.setText("");
        }
    }


}
