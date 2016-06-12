package com.running.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.achartengine.GraphicalView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by C5-0 on 2016/5/28.
 */
public class TrendViewPagerAdapter extends PagerAdapter {
    List<GraphicalView> mListGraphicalView = new ArrayList<>();
    Context mContext;
    LayoutInflater mInflater;

    public TrendViewPagerAdapter(Context mContext, List<GraphicalView> listGraphicalView) {
        this.mContext = mContext;
        this.mListGraphicalView = listGraphicalView;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mListGraphicalView.get(position));
        return mListGraphicalView.get(position);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //销毁缓存
        container.removeView(mListGraphicalView.get(position));
    }

    @Override
    public int getCount() {
        return mListGraphicalView.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
