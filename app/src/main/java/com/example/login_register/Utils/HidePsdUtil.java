package com.example.login_register.Utils;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class HidePsdUtil {
    public static void ShowOrHide(CheckBox checkBox, final EditText editText){
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }
}
