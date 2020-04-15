package com.example.login_register.Broadcast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.example.login_register.R;
import com.example.login_register.Service.TimerService;
import com.example.login_register.Utils.ToastUtil;

public class BroadcastNetworkActivity extends AppCompatActivity {
    private IntentFilter intentFilter;
    private TimeChangeReceiver timeChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_network);
//        Intent intent = new Intent(this, TimerService.class);
//        startService(intent);
        Log.d("BLEtimer","start");
        intentFilter = new IntentFilter();
        timeChangeReceiver = new TimeChangeReceiver();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeChangeReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeChangeReceiver);
    }

    class TimeChangeReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Intent.ACTION_TIME_TICK)){
                Log.d("BLEtimer","time change ok");
                ToastUtil.showMsg(BroadcastNetworkActivity.this,"time change");
            }
        }
    }
//    class NetWorkChangeReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//            if(networkInfo != null && networkInfo.isAvailable()){
//                ToastUtil.showMsg(BroadcastNetworkActivity.this,"network is available");
//            }else {
//                ToastUtil.showMsg(BroadcastNetworkActivity.this,"network is unavailable");
//            }
//            Log.d("breoadcast","network change");
//        }
//    }
}
