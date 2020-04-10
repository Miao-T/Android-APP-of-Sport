package com.example.login_register.FloatView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.login_register.BLEActivity;
import com.example.login_register.MainActivity;
import com.example.login_register.R;
import com.example.login_register.Service.FloatWindowService;
import com.example.login_register.Utils.ActivityCollector;

import static android.content.Context.MODE_PRIVATE;

public class FloatWindowBigView extends LinearLayout {
    /**
     * 记录大悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录大悬浮窗的高度
     */
    public static int viewHeight;

    public FloatWindowBigView(final Context context){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.float_window_big,this);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        Button close = findViewById(R.id.btn_close);
        final Button back = findViewById(R.id.btn_back);

        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.removeSmallWindow(context);
                Intent intent = new Intent(getContext(), FloatWindowService.class);
                context.stopService(intent);
//                BLEActivity.mBluetoothGatt.disconnect();
//                BLEActivity.mBluetoothGatt.close();
//                ActivityCollector.finishAll();
//                Bundle bundle = new Bundle();
//                bundle.putString("data","back");
                BLEActivity.data = 1;
                Intent intent2 = new Intent(getContext(), BLEActivity.class);
                context.startActivity(intent2);

            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                MyWindowManager.removeBigWindow(context);
                MyWindowManager.createSmallWindow(context);
            }
        });
    }
}
