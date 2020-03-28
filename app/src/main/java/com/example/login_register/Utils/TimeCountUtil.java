package com.example.login_register.Utils;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.Button;

public class TimeCountUtil extends CountDownTimer {
    private Button mButton;

    public TimeCountUtil(Button button,long millisInFuture,long countDownInterval){
        super(millisInFuture,countDownInterval);
        this.mButton = button;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mButton.setEnabled(false);
        mButton.setBackgroundColor(Color.parseColor("#dddddd"));
        String showText = millisUntilFinished / 1000 + "s后可重新发送";
        mButton.setText(showText);
    }

    @Override
    public void onFinish() {
        mButton.setEnabled(true);
        mButton.setText("重新获取验证码");
    }
}
