package com.running.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.running.myviews.ImageTextView;

import java.util.List;

public class MedalAdapter extends BaseAdapter {
    private List<ImageTextView> mList;
    private ViewGroup.LayoutParams mLayoutParams;

    public MedalAdapter(List<ImageTextView> list) {
        mList = list;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mList.get(position);
        }
        return convertView;
    }
}