package com.example.login_register.LitePalDatabase;

import org.litepal.crud.LitePalSupport;

public class DailyRecord extends LitePalSupport {

    private int Step;
    private String Date;
    private String Time;
    private UserInfo userInfo;

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public int getStep() {
        return Step;
    }

    public void setStep(int step) {
        Step = step;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
