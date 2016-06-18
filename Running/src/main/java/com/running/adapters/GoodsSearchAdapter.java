package com.running.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.running.android_main.R;
import com.running.beans.GoodsData;

import java.util.List;

/**
 * Created by C5-0 on 2016/6/18.
 */
public class GoodsSearchAdapter extends BaseAdapter{


    Context mContext;
    List<GoodsData> goodsDataList;
    LayoutInflater layoutInflater;

    public GoodsSearchAdapter(Context mContext, List<GoodsData> goodsDataList) {
        this.mContext = mContext;
        this.goodsDataList = goodsDataList;
        layoutInflater=LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return goodsDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return goodsDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder{
        TextView goodsTextView;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view==null){
            viewHolder=new ViewHolder();
            view=layoutInflater.inflate(android.R.layout.simple_list_item_1,null);
            viewHolder.goodsTextView= (TextView) view.findViewById(android.R.id.text1);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.goodsTextView.setText(goodsDataList.get(i).getName());
        return view;
    }
}
