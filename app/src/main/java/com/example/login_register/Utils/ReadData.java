package com.example.login_register.Utils;

import java.util.EnumMap;

public interface ReadData {
    enum UserInfoData{
        userId,userName,password,registerTime,phoneNumber,email,
        sex,height,weight,birthday,age,location,targetStep
    }

    public EnumMap<UserInfoData,Object> ReadCloudData(String name,String phoneNum);

}
