package com.example.login_register.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.login_register.Broadcast.BroadcastNetworkActivity;

public class NetworkListener {
    public boolean NetWorkState(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isAvailable()){
            Log.d("network","available");
            return true;
        }else {
            Log.d("network","unavailable");
            return false;
        }
    }
}
