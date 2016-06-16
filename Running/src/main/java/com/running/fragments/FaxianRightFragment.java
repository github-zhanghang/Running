package com.running.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.running.adapters.GoodsAdapter;
import com.running.adapters.GoodsBannerAdapter;
import com.running.android_main.GoodsActivity;
import com.running.android_main.MainActivity;
import com.running.android_main.MyApplication;
import com.running.android_main.R;
import com.running.beans.GoodsData;
import com.running.myviews.NoScrollView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;

/**
 * Created by ZhangHang on 2016/5/5.
 */
public class FaxianRightFragment extends Fragment {
    MainActivity mMainActivity;
    private View mRightView;

    //轮播图
    List<View> dotsViews;
    List<GoodsData> mGoodsBannerDatas=new ArrayList<>();
    ViewPager mBannerViewPager;
    GoodsBannerAdapter mGoodsBannerAdapter;
    private int oldPosition = 0;//记录上一次点的位置
    private int currentItem; //当前页面
    private ScheduledExecutorService scheduledExecutorService;
    private Handler bannerHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //设置当前页面
            mBannerViewPager.setCurrentItem(currentItem,false);
        }
    };
    private class ViewPagerTask implements Runnable{

        @Override
        public void run() {
            currentItem=(currentItem+1)%mGoodsBannerDatas.size();
            //更新页面
            bannerHandler.obtainMessage().sendToTarget();
        }
    }

    //装备信息
    private RecyclerView goodRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private List<GoodsData> mGoodsDataList=new ArrayList<>();
    private GoodsAdapter mGoodsAdapter;

    //上拉加载，下拉刷新
    boolean isloading=false;
    boolean isRefreshing=false;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Handler recyclerhandler =new Handler();
    int page=1;

    private NoScrollView mNoScrollView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainActivity= (MainActivity) getActivity();
        mRightView = inflater.inflate(R.layout.faxian_right, null);

        initBannerView();
        initBannerData();

        addBannerListener();

        initGoodView();
        initGoodData();

        initSwiprRefresh();
        initNoScollView();

        addClickListener();
        return mRightView;
    }

    private void initBannerData() {
        OkHttpUtils.get()
                .url(MyApplication.HOST+"goodsBannerServlet")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String name=jsonObject.getString("name");
                                double price=jsonObject.getDouble("price");
                                String img=jsonObject.getString("img");
                                String  html=jsonObject.getString("html");
                                GoodsData goodsBannerData=new GoodsData(name,price,img,html);
                               mGoodsBannerDatas.add( goodsBannerData);
                            }
                            //Log.e("taozier", "initGoodsData:"+mGoodsBannerDatas.size()+"");
                            mGoodsBannerAdapter=new GoodsBannerAdapter(mMainActivity,mGoodsBannerDatas);
                            mBannerViewPager.setAdapter(mGoodsBannerAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initBannerView() {
        //点
        dotsViews=new ArrayList<>();
        dotsViews.add(mRightView.findViewById(R.id.g_dot0));
        dotsViews.add(mRightView.findViewById(R.id.g_dot1));
        dotsViews.add(mRightView.findViewById(R.id.g_dot2));
        dotsViews.add(mRightView.findViewById(R.id.g_dot3));
        dotsViews.add(mRightView.findViewById(R.id.g_dot4));
        //viewpager
        mBannerViewPager= (ViewPager) mRightView.findViewById(R.id.vp_banner_goods);

    }

    private void addBannerListener() {
        mBannerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                dotsViews.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                dotsViews.get(position).setBackgroundResource(R.drawable.dot_focused);
                oldPosition = position;
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        scheduledExecutorService= Executors.newSingleThreadScheduledExecutor();
        //每隔三秒切换一张图片
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(),3,5, TimeUnit.SECONDS);
    }


    private void initGoodData() {
        recyclerhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1500);
    }

    private void initGoodView() {
        goodRecyclerView= (RecyclerView) mRightView.findViewById(R.id.rcy_goods);
        mStaggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        goodRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        goodRecyclerView.setHasFixedSize(true);
        mGoodsAdapter=new GoodsAdapter(mMainActivity, mGoodsDataList);
        goodRecyclerView.setAdapter(mGoodsAdapter);
    }

    private void initSwiprRefresh() {
        mSwipeRefreshLayout= (SwipeRefreshLayout) mRightView.findViewById(R.id.swiperefresh_goods);
        //设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#eb4f38"));

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page=1;
                        mGoodsDataList.clear();
                        refreshData();
                    }
                }, 1000);
            }
        });
    }


    private void initNoScollView() {

        mNoScrollView= (NoScrollView) mRightView.findViewById(R.id.goods_noscroll);
        mNoScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //在这里进行监听，滑动布局滑到了底部，然后又进行加载数据
                        int scrollY = v.getScrollY();
                        int height = v.getHeight();
                        int scrollViewMeasureHeight = mNoScrollView.getChildAt(0).getMeasuredHeight();

                        if (scrollY==0){
//                          Log.e(TAG,"--------滑到了顶端 scrollY" + scrollY );
                        }
                        if((scrollY+height) + 30 >=scrollViewMeasureHeight){

                            if (isRefreshing){
                                mGoodsAdapter.notifyItemRemoved(mGoodsAdapter.getItemCount());
                            }
                            if(!isloading) {
                                isloading = true;
                                recyclerhandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshData();
                                        isloading=false;
                                    }
                                }, 1000);
                                page++;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return false;
            }
        });
    }
    private void getData() {
        refreshData();
    }
    private void refreshData() {
        OkHttpUtils.get()
                .url(MyApplication.HOST+"goodsServlet")
                .addParams("page",page+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String name=jsonObject.getString("name");
                                double price=jsonObject.getDouble("price");
                                String img=jsonObject.getString("img");
                                String  html=jsonObject.getString("html");
                                GoodsData goodsData=new GoodsData(name,price,img,html);
                                mGoodsDataList.add(goodsData);
                            }
                            mGoodsAdapter.notifyDataSetChanged();
                            mGoodsAdapter.notifyItemRemoved(mGoodsAdapter.getItemCount());
                            mSwipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void addClickListener() {
        mGoodsAdapter.setOnItemClickListener(new GoodsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                //Toast.makeText(mMainActivity,"装备:"+(position+1),Toast.LENGTH_SHORT).show();
                Bundle bundle=new Bundle();
                bundle.putString("weburl",mGoodsDataList.get(position).getHtml());

                Intent intent=new Intent(mMainActivity, GoodsActivity.class);
                intent.putExtras(bundle);
                mMainActivity.startActivity(intent);
            }
        });
    }
}
