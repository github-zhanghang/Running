package com.running.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.running.android_main.R;
import com.running.android_main.RaceActivity;
import com.running.beans.GoodsData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class GoodsBannerAdapter extends PagerAdapter {
    List<GoodsData> mGoodsBannerDatas;
    List<ImageView> mImageViews;
    Context mContext;
    LayoutInflater mInflater;

    public GoodsBannerAdapter(Context context, List<GoodsData> goodsBannerDatas) {
        mContext = context;
        mGoodsBannerDatas = goodsBannerDatas;
        mInflater.from(mContext).inflate(R.layout.banner_goods,null);
    }

    @Override
    public int getCount() {
        return mGoodsBannerDatas.size();
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

        //图片
        mImageViews =new ArrayList<>();
        for (int i = 0; i <mGoodsBannerDatas.size() ; i++) {
            ImageView imageView=new ImageView(mContext);
            //imageView.setImageResource(mBannerDatas.get(i).getPic());
            Glide.with(mContext)
                    .load(mGoodsBannerDatas.get(i).getImg())
                    .error(R.drawable.fail)
                    .centerCrop()
                    /*.fitCenter()*/
                    .into(imageView);
            mImageViews.add(imageView);
        }
        ImageView imageView=mImageViews.get(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,"图片:"+position,Toast.LENGTH_SHORT).show();
                Bundle bundle=new Bundle();
                bundle.putString("weburl",mGoodsBannerDatas.get(position).getHtml());

                Intent intent=new Intent(mContext, RaceActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        container.addView(mImageViews.get(position));
        return mImageViews.get(position);
    }
}
