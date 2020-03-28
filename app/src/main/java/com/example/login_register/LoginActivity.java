package com.example.login_register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.login_register.Utils.ToastUtil;

import org.litepal.LitePal;
import org.litepal.annotation.Encrypt;

import java.util.List;

public class LoginActivity extends BaseActivity {

    private EditText mEtName,mEtPassword;
    private Button mBtnLogin,mBtnRegister;
    private CheckBox mCbPsd;
//    private SharedPreferences mSharedPreferences;
//    private SharedPreferences.Editor mEditor;
    private Boolean RememberPsd;
    private TextView mTvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.initialize(this);
//        if(mSharedPreferences.getBoolean("LoginBefore",true)){
//            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//            startActivity(intent);
//            LoginActivity.this.finish();
//        }
        setContentView(R.layout.activity_login);
        initView();
//        mCbPsd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.d("CheckBox","点击状态为："+isChecked);
//                if(isChecked){
//                    String account = mSharedPreferences.getString("InputAccount","");
//                    String password = mSharedPreferences.getString("InputPsd","");
//                    //Boolean remember = mSharedPreferences.getBoolean("is_remember",false);
//                    mEtAccount.setText(account);
//                    mEtPassword.setText(password);
//                    //mCbPsd.setChecked(remember);
//                }
//            }
//        });

        mCbPsd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    RememberPsd = true;
                }else{
                    RememberPsd = false;
                }
            }
        });
//
//        boolean is_remember =mSharedPreferences.getBoolean("is_remember",false);
//        if(is_remember){
//            String account = mSharedPreferences.getString("InputAccount","");
//            String password = mSharedPreferences.getString("InputPsd","");
//            //Boolean remember = mSharedPreferences.getBoolean("is_remember",false);
//            mEtName.setText(account);
//            mEtPassword.setText(password);
//            mCbPsd.setChecked(true);
//        }

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEtName.getText().toString();
                String psd = mEtPassword.getText().toString();
                boolean flag = false;
                List<UserInfo> userInfos = LitePal.findAll(UserInfo.class);
                for (UserInfo userInfo : userInfos) {
                    if (name.equals(userInfo.getUsername()) && psd.equals(userInfo.getPassword())) {
                        flag = true;
                        break;
                    }else{
                        flag = false;
                    }
                }
                if(flag){
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                    mEditor.putBoolean("LoginBefore",true);
//                    mEditor.apply();
                    startActivity(intent);
                    finish();
                }else{
                    ToastUtil.showMsg(LoginActivity.this,"Account or password is invalid");
                }
            }
        });

//        mBtnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String inputAccount = mEtAccount.getText().toString();
//                String inputPassword = mEtPassword.getText().toString();
//                String registerAccount = mSharedPreferences.getString("RegisterAccount","");
//                String registerPsd = mSharedPreferences.getString("RegisterPsd","");
//
//                if(inputAccount.equals(registerAccount) && inputPassword.equals(registerPsd)){
//                    if(RememberPsd){
//                        mEditor.putBoolean("is_remember",true);
//                        mEditor.putString("InputAccount",inputAccount);
//                        mEditor.putString("InputPsd",inputPassword);
//                    }else{
//                        mEditor.putString("InputAccount","");
//                        mEditor.putString("InputPsd","");
//                        mEditor.putBoolean("is_remember",false);
//                    }
//                    mEditor.putBoolean("is_remember",mCbPsd.isChecked());
//                    mEditor.apply();
//                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }else{
//                    ToastUtil.showMsg(LoginActivity.this,"Account or password is invalid");
//                }
//            }
//        });


        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        mTvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,LoginMessageActivity.class);
                startActivity(intent);
            }
        });
    }

    public void initView(){
        mEtName = findViewById(R.id.et_account);
        mEtPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register);
        mCbPsd = findViewById(R.id.cb_reme);
        mTvMessage = findViewById(R.id.tv_message);
        //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
//        mEditor = mSharedPreferences.edit();
    }
}
