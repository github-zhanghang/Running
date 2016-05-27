package com.running.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.running.android_main.R;
import com.running.android_main.RaceActivity;
import com.running.beans.GoodsBannerData;

import java.util.List;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class GoodsBannerAdapter extends PagerAdapter {
    List<GoodsBannerData> mGoodsBannerDatas;
    List<ImageView> mImageViews;
    Context mContext;
    LayoutInflater mInflater;

    public GoodsBannerAdapter(Context context, List<GoodsBannerData> goodsBannerDatas, List<ImageView> imageViews) {
        mContext = context;
        mGoodsBannerDatas = goodsBannerDatas;
        mImageViews = imageViews;
        mInflater.from(mContext).inflate(R.layout.banner_goods,null);
    }

    @Override
    public int getCount() {
        return mImageViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mImageViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        ImageView imageView=mImageViews.get(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"图片:"+position,Toast.LENGTH_SHORT).show();

                Bundle bundle=new Bundle();
                bundle.putString("weburl",mGoodsBannerDatas.get(position).getWeburl());

                Intent intent=new Intent(mContext, RaceActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        container.addView(mImageViews.get(position));
        return mImageViews.get(position);
    }
}
