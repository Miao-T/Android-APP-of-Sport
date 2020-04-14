package com.example.login_register.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BLEStateBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
    }
}
