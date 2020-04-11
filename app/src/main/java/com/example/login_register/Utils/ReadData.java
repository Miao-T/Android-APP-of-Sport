package com.example.login_register.Utils;

import java.util.EnumMap;

public interface ReadData {
    enum UserInfoData{
        id,name,password,registerTime,phoneNumber,email,
        sex,height,weight,birthday,age,lcoation,targetStep
    }

    public EnumMap<UserInfoData,Object> ReadCloudData(String name);
}
