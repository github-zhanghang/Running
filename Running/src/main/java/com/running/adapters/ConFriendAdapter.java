package com.running.adapters;

import android.content.Context;
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
            viewHolder.mTextView = (TextView) convertView.findViewById(item_conFriend_username);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        Glide.with(mContext)
                .load(mUserInfoList.get(position).getImageUrl())
                .into(viewHolder.mImageView);
        viewHolder.mTextView.setText(mUserInfoList.get(position).getNickName());

        return convertView;
    }
    class MyViewHolder {
        ImageView mImageView;
        TextView mTextView;

    }
}
