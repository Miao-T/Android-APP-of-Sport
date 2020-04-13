package com.example.login_register.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class MonitorText implements TextWatcher {
    private Button button;
    private EditText[] editTexts;

    public void SetMonitorText(final Button button,final EditText... editTexts){
        this.button = button;
        this.editTexts = editTexts;

        for(int i = 0; i < editTexts.length; i++){
            if(editTexts[i]!=null){
                editTexts[i].addTextChangedListener(MonitorText.this);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        for(int i = 0; i<editTexts.length; i++){
            if(editTexts[i].length() == 0){
                button.setEnabled(false);
                button.setClickable(false);
                return;
            }else{
                button.setEnabled(true);
                button.setClickable(true);
            }
        }
    }
}
