package com.running.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.running.android_main.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ldd on 2016/5/29.
 */
public class DynamicImgGridViewAdapter extends BaseAdapter {
    Context mContext;
    List<Integer> mList;
    LayoutInflater mInflater;
    int size;
    LinearLayout.LayoutParams mParams;
    GridView mGridView;

    public DynamicImgGridViewAdapter(Context context, List<Integer> list, GridView gridView) {
        mContext = context;
        mList = list;
        mGridView = gridView;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        size = mList.size();
        return size;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dynamic_grid_view_img, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int img = mList.get(position);
        Glide.with(mContext).load(img).thumbnail(0.1f).into(viewHolder.mDynamicGridViewItemImg);
        //通过屏幕宽度及照片数量确定GridView显示几行几列
        if (size == 1) {
            mGridView.setNumColumns(1);
            mGridView.setColumnWidth(getWidth());
            int width = getWidth();
            int height = (int) (width * 0.85);
            mParams = new LinearLayout.LayoutParams(width, height);
            viewHolder.mDynamicGridViewItemImg.setLayoutParams(mParams);
        } else if (size == 2 || size == 4) {
            mGridView.setNumColumns(2);
            mGridView.setColumnWidth(getWidth() / 2);
            int width = getWidth() / 2;
            int height = (int) (width * 0.85);
            mParams = new LinearLayout.LayoutParams(width, height);
            viewHolder.mDynamicGridViewItemImg.setLayoutParams(mParams);
        } else {
            mGridView.setNumColumns(3);
            mGridView.setColumnWidth(getWidth() / 3);
            int width = getWidth() / 3;
            int height = (int) (width * 0.85);
            mParams = new LinearLayout.LayoutParams(width, height);
            viewHolder.mDynamicGridViewItemImg.setLayoutParams(mParams);
        }
        return convertView;
    }

    //得到屏幕的宽度
    public int getWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics = mContext.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels*5/6;
        //Log.d("width", String.valueOf(screenWidth));
        return screenWidth;
    }

    static class ViewHolder {
        @Bind(R.id.dynamic_grid_view_item_img)
        ImageView mDynamicGridViewItemImg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
