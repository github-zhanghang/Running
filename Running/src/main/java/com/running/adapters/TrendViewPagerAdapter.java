package com.running.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.running.android_main.R;
import com.running.beans.TrendData;

import java.util.List;

/**
 * Created by C5-0 on 2016/5/28.
 */
public class TrendViewPagerAdapter extends PagerAdapter {
    List<TrendData> trendDatas;
    List<LinearLayout> linearLayouts;
    Context mContext;
    LayoutInflater mInflater;

    public TrendViewPagerAdapter(Context mContext, List<TrendData> trendDatas, List<LinearLayout> linearLayouts) {
        this.mContext = mContext;
        this.trendDatas = trendDatas;
        this.linearLayouts = linearLayouts;
        mInflater.from(mContext).inflate(R.layout.activity_trend,null);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LinearLayout linearLayout=linearLayouts.get(position);
        container.addView(linearLayout);
        return linearLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //销毁缓存
        container.removeView(linearLayouts.get(position));
    }

    @Override
    public int getCount() {
        return linearLayouts.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
