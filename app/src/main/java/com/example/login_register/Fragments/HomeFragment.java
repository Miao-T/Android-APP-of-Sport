package com.example.login_register.Fragments;

import android.content.Intent;
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
import com.example.login_register.R;
import com.example.login_register.Service.FloatWindowService;
import com.example.login_register.Utils.ActivityCollector;

public class HomeFragment extends Fragment {

    private TextView mTvUpdateStep;
    private Button mBtnFloatBle;

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
        mBtnFloatBle = view.findViewById(R.id.btn_floatBle);

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
