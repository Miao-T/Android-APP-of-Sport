package com.example.login_register.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login_register.Broadcast.BroadcastNetworkActivity;
import com.example.login_register.LoginActivity;
import com.example.login_register.MainActivity;

public class BaseActivity extends AppCompatActivity {
    private ForceOfflineReceiver forceOfflineReceiver;
    //private NetWorkChangeReceiver netWorkChangeReceiver;
    //public static boolean networkOk;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter1 = new IntentFilter();
        //IntentFilter intentFilter2 = new IntentFilter();
        intentFilter1.addAction("com.example.login.FORCE_OFFLINE");
        //intentFilter2.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        forceOfflineReceiver = new ForceOfflineReceiver();
        //netWorkChangeReceiver = new NetWorkChangeReceiver();
        registerReceiver(forceOfflineReceiver,intentFilter1);
        //registerReceiver(netWorkChangeReceiver,intentFilter2);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        unregisterReceiver(forceOfflineReceiver);
        //unregisterReceiver(netWorkChangeReceiver);
    }

    class ForceOfflineReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(final Context context, Intent intent) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Warning");
            builder.setMessage("You are forced to be offline. Please try to login again");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCollector.finishAll();
                    Intent intent = new Intent(context,LoginActivity.class);
                    context.startActivity(intent);
                }
            });
            builder.show();
        }
    }
}
