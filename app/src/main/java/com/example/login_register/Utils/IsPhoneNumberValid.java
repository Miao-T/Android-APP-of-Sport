package com.example.login_register.Utils;
import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.i18n.phonenumbers.*;

public class IsPhoneNumberValid {
    public static boolean libphonenumber(String phoneNumber,String countryCode){
        System.out.println("isPhoneNumberValid: " + phoneNumber + "/" + countryCode);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try{
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phoneNumber,countryCode);
            return phoneUtil.isValidNumber(numberProto);
        }catch (NumberParseException e){
            System.err.println("isPhoneNumberValid NumberParseException was thrown: " + e.toString());
            return false;
        }
    }
}

//    public static boolean PhoneNumberValid(Context context, EditText number){
//        String PhoneNum = number.getText().toString().trim();
//        if(TextUtils.isEmpty(PhoneNum)){
//            ToastUtil.showMsg(context,"手机号码不能为空");
//            number.requestFocus();
//            return false;
//        }else if(PhoneNum.length() != 11){
//            ToastUtil.showMsg(context,"手机号码位数错误");
//            number.requestFocus();
//            return false;
//        }else{
//            //String num = "[1][358]\\d{9}";
//            String num = "^((13[0-9])|(14[4-9])|(15[^4])|(16[6-7])|(17[^9])|(18[0-9])|(19[1|8|9]))\\d{8}$";
//            if(PhoneNum.matches(num)){
//                return true;
//            }else{
//                ToastUtil.showMsg(context,"手机号码不合法");
//                number.requestFocus();
//                return false;
//            }
//        }
//    }