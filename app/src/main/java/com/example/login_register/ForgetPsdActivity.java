package com.example.login_register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.login_register.Utils.BaseActivity;

public class ForgetPsdActivity extends BaseActivity {
    private EditText mEtAscii;
    private TextView mTvNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psd);

        mEtAscii = findViewById(R.id.et_ascii);
        mTvNum = findViewById(R.id.tv_number);
    }


}
