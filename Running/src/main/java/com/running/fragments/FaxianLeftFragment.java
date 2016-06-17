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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.running.adapters.RaceAdapter;
import com.running.adapters.RaceBannerAdapter;
import com.running.android_main.MainActivity;
import com.running.android_main.MyApplication;
import com.running.android_main.R;
import com.running.android_main.RaceActivity;
import com.running.android_main.RaceForecastActivity;
import com.running.beans.RaceData;
import com.running.myviews.NoScrollView;
import com.running.myviews.ShineTextView;
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
public class FaxianLeftFragment extends Fragment {
    private MainActivity mMainActivity;
    private View mLeftView;

    //轮播图
    List<View> dotsViews;
    List<RaceData> mBannerDatas=new ArrayList<>();
    TextView bannerTextView;
    ViewPager mBannerViewPager;
    RaceBannerAdapter mRaceBannerAdapter;
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
            currentItem=(currentItem+1)%mBannerDatas.size();
            //更新页面
            bannerHandler.obtainMessage().sendToTarget();
        }
    }

    //赛事信息
    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    RaceAdapter mRaceAdapter;
    List<RaceData> mRaceDataList =new ArrayList<>();

    //上拉加载，下拉刷新
    boolean isloading;
    boolean isRefreshing;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Handler recyclerhandler =new Handler();
    int page=1;
    private NoScrollView mNoScrollView;

    //赛事预告
    TextView forecastTextView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLeftView = inflater.inflate(R.layout.faxian_left, null);
        mMainActivity= (MainActivity) getActivity();

        //轮播
        initBannerView();
        initBannerData();
        addBannerListener();

        initRecyclerViews();
        initRecyclerViewData();

        //下拉刷新
        initSwiprRefresh();
        //上拉加载
        initNoScollView();

        //点击跳转页面
        addClickListener();

        //赛事预告
        initForecast();


        return mLeftView;
    }


    private void initBannerData() {

        OkHttpUtils.get()
                .url(MyApplication.HOST+"raceBannerServlet")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("taozier ",e.getMessage() );
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name=jsonObject.getString("name");
                                String time=jsonObject.getString("time");
                                String img=jsonObject.getString("img");
                                String  html=jsonObject.getString("html");
                                String location=jsonObject.getString("location");
                                RaceData bannerRaceData=new RaceData(name,time,img,html,location);
                                mBannerDatas.add(bannerRaceData);
                            }
                            mRaceBannerAdapter=new RaceBannerAdapter(mBannerDatas,mMainActivity);
                            mBannerViewPager.setAdapter(mRaceBannerAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }
    private void initBannerView() {
        //标题
        bannerTextView = (TextView) mLeftView.findViewById(R.id.title_banner);

        //点
        dotsViews=new ArrayList<>();
        dotsViews.add(mLeftView.findViewById(R.id.v_dot0));
        dotsViews.add(mLeftView.findViewById(R.id.v_dot1));
        dotsViews.add(mLeftView.findViewById(R.id.v_dot2));
        dotsViews.add(mLeftView.findViewById(R.id.v_dot3));
        dotsViews.add(mLeftView.findViewById(R.id.v_dot4));
        //viewpager
        mBannerViewPager= (ViewPager) mLeftView.findViewById(R.id.vp_banner_race);

    }

    private void addBannerListener() {
        mBannerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bannerTextView.setText(mBannerDatas.get(position).getName());
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

    //初始化赛事数据
    private void initRecyclerViewData() {
     recyclerhandler.postDelayed(new Runnable() {
         @Override
         public void run() {
             getData();
         }
     }, 1500);

    }
    //得到数据
    private void getData() {
        refreshData();
    }
    //刷新数据
    private void refreshData() {
        OkHttpUtils.get()
                .url(MyApplication.HOST+"raceServlet")
                .addParams("page",page+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name=jsonObject.getString("name");
                                String time=jsonObject.getString("time");
                                String img=jsonObject.getString("img");
                                String  html=jsonObject.getString("html");
                                String location=jsonObject.getString("location");
                                RaceData raceData=new RaceData(name,time,img,html,location);
                                mRaceDataList.add(raceData);
                               // Log.e( "taozi1234: ",mRaceDataList.size()+" " );
                            }
                            mRaceAdapter.notifyDataSetChanged();
                            mRaceAdapter.notifyItemRemoved(mRaceAdapter.getItemCount());

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

    private void initRecyclerViews() {
        mRecyclerView= (RecyclerView) mLeftView.findViewById(R.id.recyclerview);

        //创建默认的线性LayoutManager
        mLinearLayoutManager=new LinearLayoutManager(mMainActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        mRaceAdapter =new RaceAdapter(mMainActivity, mRaceDataList);
        mRecyclerView.setAdapter(mRaceAdapter);
    }

    //下拉刷新
    private void initSwiprRefresh() {
        mSwipeRefreshLayout= (SwipeRefreshLayout)mLeftView.findViewById(R.id.swiperefreshlayout);
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
                        mRaceDataList.clear();
                        refreshData();
                    }
                },1000);
            }
        });
    }

    //上拉加载
    private void initNoScollView() {
        mNoScrollView= (NoScrollView) mLeftView.findViewById(R.id.noScollView);
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

                            isRefreshing=mSwipeRefreshLayout.isRefreshing();
                            if (isRefreshing) {
                                mRaceAdapter.notifyItemRemoved(mRaceAdapter.getItemCount());

                                break;
                            }
                            if(!isloading) {
                                isloading = true;
                                recyclerhandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshData();
                                        isloading = false;
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
    private void addClickListener() {
        //点击跳转页面
        mRaceAdapter.setOnItemClickListener(new RaceAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(mMainActivity,"赛事:"+(position+1),Toast.LENGTH_SHORT).show();
                Bundle bundle=new Bundle();
                bundle.putString("weburl",mRaceDataList.get(position).getHtml());

                Intent intent=new Intent(getActivity(), RaceActivity.class);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });
    }

    private void initForecast() {
        forecastTextView= (TextView) mLeftView.findViewById(R.id.forecast_race);
        forecastTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), RaceForecastActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }
}
