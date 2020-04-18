package com.example.login_register.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login_register.CloudSQL.DBConnection;
import com.example.login_register.Friend.FriendAdapter;
import com.example.login_register.Friend.FriendPKActivity;
import com.example.login_register.MainActivity;
import com.example.login_register.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FriendFragment extends Fragment {

    private EditText mEtInputFriendName;
    private Button mBtnSearch;
    private ListView mLvFriendList;
    private List<String> friendList;
    private String loginName;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private FriendAdapter friendAdapter;
    private static final int MessageText = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEtInputFriendName = view.findViewById(R.id.et_searchFriend);
        mBtnSearch = view.findViewById(R.id.btn_search);
        mLvFriendList = view.findViewById(R.id.friend_list_view);
        friendList = new ArrayList<>();

        mSharedPreferences = getActivity().getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        loginName = mSharedPreferences.getString("RememberName",null);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == MessageText){
                    friendAdapter = new FriendAdapter(getActivity());
                    mLvFriendList.setAdapter(friendAdapter);
                    friendAdapter.setFriendList(friendList);
                    friendAdapter.notifyDataSetChanged();
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.DriverConnection();
                friendList = DBConnection.ReadRequestListYes(loginName);
                for(int i = 0;i<friendList.size();i++){
                    Log.d("friendList",friendList.get(i));
                }
                Message message = new Message();
                message.what = MessageText;
                handler.sendMessage(message);
            }
        }).start();

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mLvFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FriendPKActivity.class);
                startActivity(intent);
            }
        });
    }
}
