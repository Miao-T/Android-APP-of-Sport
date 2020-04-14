package com.example.login_register.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.login_register.AliSqlActivity;
import com.example.login_register.BLEActivity;
import com.example.login_register.BarActivity;
import com.example.login_register.LitePalActivity;
import com.example.login_register.R;
import com.example.login_register.Service.FloatWindowService;
import com.example.login_register.RegisterUserInfoActivity;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment{
    private Button mBtnOffline;
    private Button mBtnBLE;
    private Button mBtnLitePal;
    private Button mBtnFloat;
    private Button mBtnBar;
    private Button mBtnPicker;
    private Button mBtnAli;
    private static final String TAG = "Fragment" ;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnOffline = view.findViewById(R.id.btn_offline);
        mBtnBLE = view.findViewById(R.id.btn_BLE);
        mBtnLitePal = view.findViewById(R.id.btn_Database);
        mBtnFloat = view.findViewById(R.id.btn_Float);
        mBtnBar = view.findViewById(R.id.btn_Bar);
        mBtnPicker = view.findViewById(R.id.btn_Picker);
        mBtnAli = view.findViewById(R.id.btn_ali);
        Log.d(TAG,"main onViewCreate");

        mBtnOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreferences = getActivity().getSharedPreferences("User",MODE_PRIVATE);
                mEditor = mSharedPreferences.edit();
                mEditor.remove("RememberName");
                mEditor.remove("has_login");
                mEditor.remove("DeviceMac");
                mEditor.remove("is_remember");
                mEditor.remove("RememberPsd");
                mEditor.apply();
                //sendBroadcast(intent);
            }
        });

        mBtnBLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BLEActivity.class);
                startActivity(intent);
            }
        });

        mBtnLitePal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LitePalActivity.class);
                startActivity(intent);
            }
        });

        mBtnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FloatWindowService.class);
                getActivity().startService(intent);
                Log.d("Float","jump into float");
                getActivity().finish();
            }
        });

        mBtnBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BarActivity.class);
                startActivity(intent);
            }
        });

        mBtnPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterUserInfoActivity.class);
                startActivity(intent);
            }
        });

        mBtnAli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AliSqlActivity.class);
                startActivity(intent);
            }
        });
    }
}
