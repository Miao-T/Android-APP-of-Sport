package com.example.login_register.LitePalDatabase;

import org.litepal.annotation.Encrypt;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class UserInfo extends LitePalSupport {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String Username;
    private String PhoneNumber;
    private String MailBox;
    @Encrypt(algorithm = AES)
    private String Password;
    private double Height;
    private double Weight;
    private boolean Sex;
    private int Age;
    private String Birthday;
    private String Location;
    private List<DailyRecord> DailyRecords = new ArrayList<DailyRecord>();



    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getMailBox() {
        return MailBox;
    }

    public void setMailBox(String mailBox) {
        MailBox = mailBox;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public double getHeight() {
        return Height;
    }

    public void setHeight(double height) {
        Height = height;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public List<DailyRecord> getDailyRecords() {
        return DailyRecords;
    }

    public void setDailyRecords(List<DailyRecord> dailyRecords) {
        DailyRecords = dailyRecords;
    }

    public boolean isSex() {
        return Sex;
    }

    public void setSex(boolean sex) {
        Sex = sex;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }
}
