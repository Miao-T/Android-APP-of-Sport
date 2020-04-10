package com.example.login_register.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login_register.R;

public class MineFragment extends Fragment {
    private TextView mTvMine;
    private Button mBtnMine;
    private static final String TAG = "Fragment" ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTvMine = view.findViewById(R.id.tv_mine);
        mBtnMine = view.findViewById(R.id.btn_mine);
        Log.d(TAG,"mine onViewCreate");
        mBtnMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvMine.setText("change");
            }
        });
    }
}
