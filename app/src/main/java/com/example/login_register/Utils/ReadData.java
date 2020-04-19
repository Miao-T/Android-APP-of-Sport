package com.example.login_register.Utils;

import java.util.EnumMap;

public interface ReadData {
    enum UserInfoData{
        userId,userName,password,registerDate,phoneNumber,
        sex,height,weight,birthday,age,location,targetStep
    }

    public EnumMap<UserInfoData,Object> ReadCloudData(String name,String phoneNum);
    public EnumMap<UserInfoData,Object> ReadUserInfo(String name,String phoneNum);

}
