package com.running.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.running.android_main.R;
import com.running.myviews.MyGridView;

import java.io.File;
import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by ldd on 2016/5/30.
 * 发布动态添加图片的 GridView 的适配器
 */
public class DynamicPublishGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Object> mList;
    private MyGridView mGridView;
    private LayoutInflater mInflater;
    private LinearLayout.LayoutParams mParams;

    public DynamicPublishGridViewAdapter(Context context, List<Object> list, MyGridView gridView) {
        mContext = context;
        mList = list;
        mGridView = gridView;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView mImageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dynamic_grid_view_img, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (ImageView) convertView.findViewById(
                    R.id.dynamic_grid_view_item_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Log.d("TAG",""+position+" "+mList.size());
        if (position == (mList.size() - 1)) {
            viewHolder.mImageView.setImageResource(R.drawable.addimg);
        } else {
            File file = new File(((PhotoInfo)mList.get(position)).getPhotoPath());
            Glide.with(mContext)
                    .load(file)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .into(viewHolder.mImageView);
        }
        int width = getWidth()/4;
        int height=width;
        mGridView.setColumnWidth(width);
        mParams=new LinearLayout.LayoutParams(width,height);
        viewHolder.mImageView.setLayoutParams(mParams);
        return convertView;
    }
    public int getWidth(){
        DisplayMetrics displayMetrics=new DisplayMetrics();
        displayMetrics=mContext.getResources().getDisplayMetrics();
        int screenWidth =displayMetrics.widthPixels;
        return screenWidth;
    }
}
