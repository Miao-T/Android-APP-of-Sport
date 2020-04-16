package com.example.login_register.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.login_register.Utils.ToastUtil;

public class TimerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_TIME_TICK)){
            Log.d("BLEtimer","time change ok");

        }
    }
}
