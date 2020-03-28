package com.example.login_register.Utils;


import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.login_register.MainActivity;

public class DoubleClickToExitUtil extends AppCompatActivity {
    private long mExitTime;
        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    //Object mHelperUtils;
                    ToastUtil.showMsg(this,"再按一次退出APP");
                    //System.currentTimeMillis()系统当前时间
                    mExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
}
