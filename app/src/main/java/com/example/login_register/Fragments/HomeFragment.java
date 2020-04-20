package com.example.login_register.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login_register.BLEActivity;
import com.example.login_register.MainActivity;
import com.example.login_register.R;
import com.example.login_register.Service.FloatWindowService;
import com.example.login_register.Utils.ActivityCollector;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private TextView mTvUpdateStep;
    private TextView mTvError;
    private Button mBtnFloatBle;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTvUpdateStep = view.findViewById(R.id.tv_updateStep);
        mTvError = view.findViewById(R.id.tv_error);
        mBtnFloatBle = view.findViewById(R.id.btn_floatBle);
        mTvError.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mSharedPreferences = getActivity().getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mTvUpdateStep.setText("- -");
        mTvError.setVisibility(View.INVISIBLE);
        mBtnFloatBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FloatWindowService.class);
                getActivity().startService(intent);
                getActivity().moveTaskToBack(true);
                //ActivityCollector.finishAll();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
