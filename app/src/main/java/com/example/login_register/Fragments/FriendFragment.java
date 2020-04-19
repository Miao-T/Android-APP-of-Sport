package com.example.login_register.Fragments;

import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.login_register.BLEActivity;
import com.example.login_register.CloudSQL.DBConnection;
import com.example.login_register.Friend.FriendAdapter;
import com.example.login_register.Friend.FriendPKActivity;
import com.example.login_register.MainActivity;
import com.example.login_register.R;
import com.example.login_register.Utils.ReadData;
import com.example.login_register.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FriendFragment extends Fragment {

    private EditText mEtInputFriendPhone;
    private Button mBtnSearch;
    private RadioGroup mRgChoiceList;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String loginName;
    private String numInput;
    private String userB;
    private String friendNameFromList;
    private int flag = 1;

    private ListView mLvFriendList;
    private List<String> friendList;
    private FriendAdapter friendAdapter;
    private Handler handler;
    private static final int MessageText = 1;
    private static final int MessageText2 = 2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEtInputFriendPhone = view.findViewById(R.id.et_searchFriend);
        mBtnSearch = view.findViewById(R.id.btn_search);
        mLvFriendList = view.findViewById(R.id.friend_list_view);
        mRgChoiceList = view.findViewById(R.id.rg_friend);
        friendList = new ArrayList<>();

        mSharedPreferences = getActivity().getSharedPreferences("User",MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        loginName = mSharedPreferences.getString("RememberName",null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DBConnection.DriverConnection();
            }
        }).start();

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MessageText:
                        friendAdapter = new FriendAdapter(getActivity());
                        mLvFriendList.setAdapter(friendAdapter);
                        friendAdapter.setFriendList(friendList);
                        friendAdapter.notifyDataSetChanged();
                        break;
                    case MessageText2:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("添加好友").setMessage(userB + '\n' + numInput)
                            .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DBConnection.AddRequestList(loginName,userB);
                                            ToastUtil.showMsg(getActivity(),"添加好友请求已发送");
                                        }
                                    }).start();
                                }
                            }).setNeutralButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                        break;
                }
            }
        };

        searchFriendList();

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFriendName();
            }
        });

        mRgChoiceList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if(radioButton.getText().toString().equals("好友列表")){
                    flag = 1;
                    searchFriendList();
                }else if(radioButton.getText().toString().equals("消息列表")){
                    flag = 2;
                    searchFriendList();
                }else{
                    //被拒
                    flag = 3;
                    searchFriendList();
                }
            }
        });

        mLvFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                friendNameFromList = friendList.get(position);
                //friendAdapter.selectedItemPosition(position);
                if(flag == 1){
                    ToastUtil.showMsg(getActivity(),friendNameFromList);
                    Log.d("friendList",friendNameFromList);
                    Intent intent = new Intent(getActivity(), FriendPKActivity.class);
                    intent.putExtra("friendName",friendNameFromList);
                    intent.putExtra("loginName",loginName);
                    startActivity(intent);
                }else if(flag == 2){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("消息").setMessage(friendNameFromList + '\n' + "请求添加你为好友")
                            .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DBConnection.UpdateRequestState(friendNameFromList,loginName,"1");
                                        }
                                    }).start();
                                    ToastUtil.showMsg(getActivity(),"同意啦");
                                }
                            })
                            .setNeutralButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    DBConnection.UpdateRequestState(friendNameFromList,loginName,"2");
                                }
                            }).start();
                            ToastUtil.showMsg(getActivity(),"拒绝啦");
                        }
                    }).show();
                }
            }
        });
    }

    private void searchFriendList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(flag == 1){
                    //好友列表
                    friendList = DBConnection.ReadRequestListYes(loginName);
                }else if(flag == 2){
                    //消息列表
                    friendList = DBConnection.ReadRequestList0(loginName);
                }else{
                    //被拒
                    friendList = DBConnection.ReadRequestListNo(loginName);
                }
                for(int i = 0; i < friendList.size(); i++){
                    Log.d("friendList",friendList.get(i));
                }
                Message message = new Message();
                message.what = MessageText;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void checkFriendName(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                numInput = mEtInputFriendPhone.getText().toString().trim();
                ReadData readData = new DBConnection();
                EnumMap<ReadData.UserInfoData,Object> userInfo = readData.ReadCloudData("",numInput);
                userInfo.entrySet().iterator();
                userB = String.valueOf(userInfo.get(ReadData.UserInfoData.userName));
                Log.d("friendList",userB);
                if(userB.equals("null")){
                    ToastUtil.showMsg(getActivity(),"未找到该用户");
                }else{
                    Message message = new Message();
                    message.what = MessageText2;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
}
