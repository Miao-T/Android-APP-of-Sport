package com.example.login_register;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.login_register.BLE.ScanBLEActivity;
import com.example.login_register.Broadcast.BroadcastNetworkActivity;
import com.example.login_register.Utils.ToastUtil;

public class DeviceActivity extends AppCompatActivity {

    private TextView mTvDeviceName,mTvDeviceMAC;
    private Button mBtnChangeDevice;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

//    private IntentFilter intentFilter;
//    private BLEConnectionReceiver bleConnectionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

//        intentFilter = new IntentFilter();
//        bleConnectionReceiver = new BLEConnectionReceiver();
//        intentFilter.addAction(Intent.ACTION_TIME_TICK);
//        registerReceiver(bleConnectionReceiver,intentFilter);

        mTvDeviceName = findViewById(R.id.tv_deviceName);
        mTvDeviceMAC = findViewById(R.id.tv_deviceMAC);
        mBtnChangeDevice = findViewById(R.id.btn_changeBleDevice);
//        mBtnChangeDevice.setEnabled(false);
//        mBtnChangeDevice.setClickable(false);

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
//                MainActivity.mBluetoothGatt.disconnect();
//                MainActivity.mBluetoothGatt.close();
                //MainActivity.instance.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
//        unregisterReceiver(bleConnectionReceiver);
        super.onDestroy();
    }

//    class BLEConnectionReceiver extends BroadcastReceiver {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
//                ToastUtil.showMsg(DeviceActivity.this,"请先断开手环和手机的蓝牙连接");
//                mBtnChangeDevice.setEnabled(false);
//                mBtnChangeDevice.setClickable(false);
//            }
//            if(action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
//                mBtnChangeDevice.setEnabled(true);
//                mBtnChangeDevice.setClickable(true);
//            }
//        }
//    }
}
