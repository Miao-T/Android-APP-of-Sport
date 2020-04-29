package com.example.login_register;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.login_register.CloudSQL.DBConnection;

import static com.example.login_register.Utils.ActivityCollector.finishAll;

public class SettingActivity extends AppCompatActivity {

    private TextView mBtnCancel;
    private static final String TAG = "DB_tag" ;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String LoginName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        LoginName = mSharedPreferences.getString("RememberName","");
        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.DriverConnection();
            }
        }).start();

        mBtnCancel = findViewById(R.id.btn_cancelAccount);
        mBtnCancel.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定要注销账号嘛！");
                builder.setCancelable(false);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                DBConnection.DropTable(LoginName);
//                                DBConnection.DeleteAccountData(LoginName);
//                                mEditor.clear();
//                                mEditor.apply();
//                            }
//                        }).start();
//                        finishAll();
//                        Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
//                        startActivity(intent);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                DBConnection.TrainingTable();
                            }
                        }).start();
                    }
                });
                builder.setNeutralButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
    }
}
