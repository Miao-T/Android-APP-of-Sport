package com.example.login_register.Friend;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.login_register.MainActivity;
import com.example.login_register.R;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> friendList = new ArrayList<>();

    public FriendAdapter(Context context){
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    //重写
    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        public TextView mTvNum,mTvFriendName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.friend_list_item,null);
            holder = new ViewHolder();
            holder.mTvFriendName = convertView.findViewById(R.id.tv_friendName);
            holder.mTvNum = convertView.findViewById(R.id.tv_num);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mTvFriendName.setText(friendList.get(position));
        holder.mTvNum.setText(String.valueOf(position + 1));
        return convertView;
    }

    public void setFriendList(List<String> friendList) {
        this.friendList = friendList;
    }
}
