package com.example.login_register.Service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import com.example.login_register.Broadcast.TimerBroadcastReceiver;
import com.example.login_register.Fragments.HomeFragment;
import com.example.login_register.R;

public class TimerService extends Service {
    private IntentFilter intentFilter;
    private TimerBroadcastReceiver timerBroadcastReceiver;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BLEtimer","service创建");
        intentFilter = new IntentFilter();
        timerBroadcastReceiver = new TimerBroadcastReceiver();
        intentFilter.addAction("Intent.ACTION_TIME_TICK");
        registerReceiver(timerBroadcastReceiver,intentFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timerBroadcastReceiver);
    }
}
