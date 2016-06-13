package com.running.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.running.android_main.R;
import com.running.beans.UserInfo;

import java.util.List;

import static com.running.android_main.R.id.item_conFriend_address;
import static com.running.android_main.R.id.item_conFriend_age;
import static com.running.android_main.R.id.item_conFriend_sex;
import static com.running.android_main.R.id.item_conFriend_username;

/**
 * Created by Ezio on 2016/6/6.
 */
public class ConFriendAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<UserInfo> mUserInfoList;

    public ConFriendAdapter(Context context, List<UserInfo> userInfoList) {
        mContext = context;
        mUserInfoList = userInfoList;
        mLayoutInflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {
        return mUserInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new MyViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_confriend,null);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.item_conFriend_portrait);
            viewHolder.nameTextView = (TextView) convertView.findViewById(item_conFriend_username);
            viewHolder.sexTextView = (TextView) convertView.findViewById(item_conFriend_sex);
            viewHolder.ageTextView = (TextView) convertView.findViewById(item_conFriend_age);
            viewHolder.addressTextView = (TextView) convertView.findViewById(item_conFriend_address);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        Glide.with(mContext)
                .load(mUserInfoList.get(position).getImageUrl())
                .into(viewHolder.mImageView);
        viewHolder.nameTextView.setText(mUserInfoList.get(position).getNickName());
        viewHolder.sexTextView.setText(mUserInfoList.get(position).getSex());
        Log.e("Ezio123", "getView: "+mUserInfoList.get(position).getAge());
        viewHolder.ageTextView.setText(mUserInfoList.get(position).getAge()+"岁");
        viewHolder.addressTextView.setText(mUserInfoList.get(position).getAddress());
        return convertView;
    }
    class MyViewHolder {
        ImageView mImageView;
        TextView nameTextView,ageTextView,sexTextView,addressTextView;

    }
}
