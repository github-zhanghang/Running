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
import com.running.beans.Friend;
import com.running.beans.NearUserInfo;

import java.util.List;

import io.rong.imkit.widget.AsyncImageView;

/**
 * Created by Ezio on 2016/6/4.
 */
public class NearByAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<NearUserInfo> mResults;

    public NearByAdapter(Context context, List<NearUserInfo> results) {
        mContext = context;
        mResults = results;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public int getCount() {
        return mResults.size();
    }

    @Override
    public Object getItem(int position) {
        return mResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_nearby,null);
            viewHolder.mImg = (ImageView) convertView.findViewById(R.id.item_nearby_portrait);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.item_nearby_username);
            viewHolder.mDistance = (TextView) convertView.findViewById(R.id.item_nearby_distance);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Glide.with(mContext)
                .load(mResults.get(position).getImageUrl())
                .into(viewHolder.mImg);
        viewHolder.mName.setText(mResults.get(position).getNickName());
        viewHolder.mDistance.setText(mResults.get(position).getDistance()+"米");
        return convertView;
    }
    class ViewHolder{
        TextView mName;
        TextView mDistance;
        ImageView mImg;
    }
}
