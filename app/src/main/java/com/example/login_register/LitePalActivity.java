package com.example.login_register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.login_register.LitePalDatabase.DailyRecord;
import com.example.login_register.LitePalDatabase.UserInfo;
import com.example.login_register.Utils.BaseActivity;
import com.example.login_register.Utils.MD5Util;
import com.example.login_register.Utils.ToastUtil;
import com.mob.wrappers.UMSSDKWrapper;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LitePalActivity extends BaseActivity {
    private Button mCreate;
    private Button mAdd;
    private Button mBtnShow;
    private EditText mEtName,mEtPsd;
    private static final String TAG = "sql_tag" ;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lite_pal);
        LitePal.initialize(this);
        mCreate = findViewById(R.id.btn_create_database);
        mAdd = findViewById(R.id.btn_add);
        mBtnShow = findViewById(R.id.btn_show);
        mEtName = findViewById(R.id.et_name);
        mEtPsd = findViewById(R.id.et_psd);


        /**
         * 方法1
         **/
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LitePal.getDatabase();
//                LitePal.deleteAll(DailyRecord.class);
//                LitePal.deleteAll(UserInfo.class);
                mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
                mEditor = mSharedPreferences.edit();
            mEditor.remove("MACAddress");
            mEditor.apply();
            }
        });

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                handler.removeCallbacks(runnable);
//                String Psd = mEtPsd.getText().toString();
//                String Name = mEtName.getText().toString();
//                String UtilMD5 = MD5Util.encrypt(Psd);
//                Log.d(TAG,UtilMD5);
//                UserInfo userInfo = new UserInfo();
//                userInfo.setUsername(Name);
//                userInfo.setPassword(Psd);
//                userInfo.save();

                /**
                * 保存时间*/
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
//                Date date = new Date(System.currentTimeMillis());
//
//                UserInfo userInfo = LitePal.findLast(UserInfo.class);
//                DailyRecord dailyRecord = new DailyRecord();
//                dailyRecord.setDate(simpleDateFormat.format(date));
//                dailyRecord.setStep(2000);
//                dailyRecord.setUserInfo(userInfo);
//                dailyRecord.save();

                /**
                 * 检验是否重复*/
//                String name = mEtName.getText().toString().trim();
//                boolean flag = true;
//                List<UserInfo> userInfos = LitePal.findAll(UserInfo.class);
//                for (UserInfo userInfo : userInfos) {
//                    if (name.equals(userInfo.getUsername())) {
////                        ToastUtil.showMsg(LitePalActivity.this,"已注册");
////                        Log.d("Litepal","已注册");
//                        flag = false;
//                        break;
//                    } else {
////                        ToastUtil.showMsg(LitePalActivity.this,"oK");
////                        Log.d("Litepal","OK");
//                        flag = true;
//                    }
//                }
//                if(flag){
//                    ToastUtil.showMsg(LitePalActivity.this,"oK");
//                    Log.d("Litepal","OK");
//                }else{
//                    ToastUtil.showMsg(LitePalActivity.this,"已注册");
//                    Log.d("Litepal","已注册");
//                }
//            }
                /**
                * 保存数据*/
//                UserInfo userInfo = new UserInfo();
//                userInfo.setUsername(name);
//                userInfo.setPassword(psd);
//                userInfo.save();
                /**
                 * 校验密码
                 **/

//                String name = mEtName.getText().toString().trim();
//                String psd = mEtPsd.getText().toString();
//                boolean flag = false;
//                List<UserInfo> userInfos = LitePal.findAll(UserInfo.class);
//                for (UserInfo userInfo:userInfos) {
//                    if (name.equals(userInfo.getUsername()) && psd.equals(userInfo.getPassword())) {
//                            flag = true;
////                        ToastUtil.showMsg(LitePalActivity.this,"succeed");
//                        Log.d("Litepal","succeed" + userInfo.getUsername() + userInfo.getPassword());
//                    }
//
//                }
//                if(flag){
//                    ToastUtil.showMsg(LitePalActivity.this,"succeed");
//                    Log.d("Litepal","succeed");
//                }else{
//                    Log.d("Litepal","failed");
//                }
//            }
            }
        });

        mBtnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<UserInfo> userInfos = LitePal.findAll(UserInfo.class);
                for(UserInfo userInfo:userInfos){
                    Log.d(TAG,userInfo.getUsername());
                    Log.d(TAG,userInfo.getPassword());
                    Log.d(TAG,userInfo.getPhoneNumber());
                    Log.d(TAG,userInfo.getBirthday());
                    Log.d(TAG,String.valueOf(userInfo.getAge()));
                    Log.d(TAG,userInfo.getLocation());
                    Log.d(TAG,String.valueOf(userInfo.getHeight()));
                    Log.d(TAG,String.valueOf(userInfo.getWeight()));
                }
//                List<UserInfo> userInfos = LitePal.where("Username = ?","didiwa").find(UserInfo.class);
//                for(UserInfo userInfo:userInfos){
//                    Log.d(TAG,String.valueOf(userInfo.getId()));
//                    Log.d(TAG,userInfo.getUsername());
//                    Log.d(TAG,userInfo.getPassword());
//                    Log.d(TAG,userInfo.getPhoneNumber());
//                    Log.d(TAG,userInfo.getBirthday());
//                    Log.d(TAG,String.valueOf(userInfo.getAge()));
//                    Log.d(TAG,userInfo.getLocation());
//                }


//                UserInfo userInfo = LitePal.find(UserInfo.class,22);
//                if(userInfo == null){
//                    Log.d(TAG,"null");
//                }else{
//                    Log.d(TAG,String.valueOf(userInfo.getId()));
//                    Log.d(TAG,userInfo.getUsername());
//                    Log.d(TAG,userInfo.getPassword());
//                    Log.d(TAG,userInfo.getPhoneNumber());
//                    Log.d(TAG,userInfo.getBirthday());
//                    Log.d(TAG,String.valueOf(userInfo.getAge()));
//                    Log.d(TAG,userInfo.getLocation());
//                }


//                List<UserInfo> userInfos = LitePal.findAll(UserInfo.class);
//                for(UserInfo userInfo:userInfos){
//                    if(userInfo.getUsername().equals("didiwa")){
//                        Log.d(TAG,userInfo.getUsername());
//                        Log.d(TAG,userInfo.getPassword());
//                        Log.d(TAG,userInfo.getBirthday());
//                        Log.d(TAG,String.valueOf(userInfo.getAge()));
//                        Log.d(TAG,userInfo.getLocation());
//                        List<DailyRecord> dailyRecords = LitePal.findAll(DailyRecord.class);
//                        for(DailyRecord dailyRecord:dailyRecords){
//                            Log.d(TAG,dailyRecord.getDate());
//                            Log.d(TAG,String.valueOf(dailyRecord.getStep()));
//                        }
//                    }
//                }

//                List<DailyRecord> dailyRecords = LitePal.findAll(DailyRecord.class);
//                for(DailyRecord dailyRecord:dailyRecords){
////                    Log.d("Litepal",dailyRecord.getUserInfo().getUsername());
////                    Log.d("Litepal",dailyRecord.getUserInfo().getPassword());
//                    Log.d("Litepal",dailyRecord.getDate());
//                    Log.d("Litepal",String.valueOf(dailyRecord.getStep()));
//                }
            }
        });
    }
}
