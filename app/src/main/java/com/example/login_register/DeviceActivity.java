package com.example.login_register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.login_register.BLE.ScanBLEActivity;

public class DeviceActivity extends AppCompatActivity {

    private TextView mTvDeviceName,mTvDeviceMAC;
    private Button mBtnChangeDevice;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        mTvDeviceName = findViewById(R.id.tv_deviceName);
        mTvDeviceMAC = findViewById(R.id.tv_deviceMAC);
        mBtnChangeDevice = findViewById(R.id.btn_changeBleDevice);

        mSharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        String deviceName = mSharedPreferences.getString("DeviceName","");
        String deviceMac = mSharedPreferences.getString("DeviceMac","");

        mTvDeviceName.setText("您的手环蓝牙名称：  " + deviceName);
        mTvDeviceMAC.setText("您的手环蓝牙地址：  " + deviceMac);
        mBtnChangeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceActivity.this, ScanBLEActivity.class);
                startActivity(intent);
            }
        });
    }
}
