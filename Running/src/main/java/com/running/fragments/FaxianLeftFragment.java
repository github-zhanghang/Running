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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.running.adapters.RaceAdapter;
import com.running.adapters.RaceBannerAdapter;
import com.running.android_main.MainActivity;
import com.running.android_main.R;
import com.running.android_main.RaceActivity;
import com.running.beans.RaceBannerData;
import com.running.beans.RaceData;
import com.running.myviews.NoScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangHang on 2016/5/5.
 */
public class FaxianLeftFragment extends Fragment {
    private MainActivity mMainActivity;
    private View mLeftView;

    //轮播图
    List<ImageView> mImageViews;
    List<View> dotsViews;
    TextView bannderTextView;
    List<RaceBannerData> mBannerDatas=new ArrayList<>();
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
            currentItem=(currentItem+1)%mImageViews.size();
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
    boolean isloading=false;
    boolean isRefreshing=false;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Handler recyclerhandler =new Handler();
    int lastVisibleItemPosition;

    private NoScrollView mNoScrollView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLeftView = inflater.inflate(R.layout.faxian_left, null);
        mMainActivity= (MainActivity) getActivity();

        //轮播
        initBannerData();
        initBannerView();
        addBannerListener();

        initRecyclerViewData();
        initRecyclerViews();

        //刷新加载
        initSwiprRefresh();
        //addListener();

        addClickListener();
        initNoScollView();

        return mLeftView;
    }

    private void addClickListener() {
        //添加点击事件
        mRaceAdapter.setOnItemClickListener(new RaceAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Toast.makeText(mMainActivity,"赛事:"+(position+1),Toast.LENGTH_SHORT).show();
                Bundle bundle=new Bundle();
                bundle.putString("weburl",mRaceDataList.get(position).getWebUrl());

                Intent intent=new Intent(getActivity(), RaceActivity.class);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });
    }
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

                            if(!isloading) {
                                isloading = true;
                                loadData();
                                mRaceBannerAdapter.notifyDataSetChanged();
                                mRaceAdapter.notifyItemRemoved(mRaceAdapter.getItemCount());
                                isloading = false;
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
    private void initBannerData() {
        RaceBannerData b1=new RaceBannerData(R.drawable.b1,"我是萌萌哒的标题1","http://www.athletics.org.cn/competition/jsgc/2016-03-28/494904.html");
        RaceBannerData b2=new RaceBannerData(R.drawable.b2,"我是萌萌哒的标题2","http://www.athletics.org.cn/competition/jsgc/2016-03-28/494904.html");
        RaceBannerData b3=new RaceBannerData(R.drawable.b3,"我是萌萌哒的标题3","http://www.athletics.org.cn/competition/jsgc/2016-03-28/494904.html");
        RaceBannerData b4=new RaceBannerData(R.drawable.b4,"我是萌萌哒的标题4","http://www.athletics.org.cn/competition/jsgc/2016-03-28/494904.html");
        RaceBannerData b5=new RaceBannerData(R.drawable.b5,"我是萌萌哒的标题5","http://www.athletics.org.cn/competition/jsgc/2016-03-28/494904.html");
        mBannerDatas.add(b1);
        mBannerDatas.add(b2);
        mBannerDatas.add(b3);
        mBannerDatas.add(b4);
        mBannerDatas.add(b5);
    }
    private void initBannerView() {
        //标题
        bannderTextView= (TextView) mLeftView.findViewById(R.id.title_banner);
        //图片
        mImageViews =new ArrayList<>();
        for (int i = 0; i <mBannerDatas.size() ; i++) {
            ImageView imageView=new ImageView(mMainActivity);
            //imageView.setImageResource(mBannerDatas.get(i).getPic());
            Glide.with(mMainActivity)
                    .load(mBannerDatas.get(i).getPic())
                    .error(R.drawable.fail)
                    .centerCrop()
                    .into(imageView);
            mImageViews.add(imageView);
        }
        //点
        dotsViews=new ArrayList<>();
        dotsViews.add(mLeftView.findViewById(R.id.v_dot0));
        dotsViews.add(mLeftView.findViewById(R.id.v_dot1));
        dotsViews.add(mLeftView.findViewById(R.id.v_dot2));
        dotsViews.add(mLeftView.findViewById(R.id.v_dot3));
        dotsViews.add(mLeftView.findViewById(R.id.v_dot4));
        //viewpager
        mBannerViewPager= (ViewPager) mLeftView.findViewById(R.id.vp_banner_race);
        mRaceBannerAdapter=new RaceBannerAdapter(mBannerDatas,mImageViews,mMainActivity);
        mBannerViewPager.setAdapter(mRaceBannerAdapter);
    }

    private void addBannerListener() {
        mBannerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bannderTextView.setText(mBannerDatas.get(position).getTitle());
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
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(),3,3, TimeUnit.SECONDS);
    }

    private void initRecyclerViewData() {
        /* RaceData r1=new RaceData(R.drawable.race1,"利马索尔马拉松1","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-04-05/495506.html");
        RaceData r2=new RaceData(R.drawable.race2,"利马索尔马拉松2","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-04-01/495174.html");
        RaceData r3=new RaceData(R.drawable.race3,"利马索尔马拉松3","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-03-28/494904.html");
        RaceData r4=new RaceData(R.drawable.race4,"利马索尔马拉松4","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-02-21/492617.html");
        RaceData r5=new RaceData(R.drawable.race5,"利马索尔马拉松5","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-02-19/492602.html");
        RaceData r6=new RaceData(R.drawable.race6,"利马索尔马拉松6","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");*/
        RaceData r1=new RaceData("http://imgcache.gtimg.cn/vipstyle/game/act/130924_mls/img/pic5.jpg","利马索尔马拉松1","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-04-05/495506.html");
        RaceData r2=new RaceData("http://img.sdchina.com/news/20140120/c01_388a5632-e6bb-4bf3-a03e-70d125d13a72_0.JPG","利马索尔马拉松2","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-04-01/495174.html");
        RaceData r3=new RaceData("http://pic.58pic.com/58pic/14/76/98/82b58PICwz7_1024.jpg","利马索尔马拉松3","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-03-28/494904.html");
        RaceData r4=new RaceData("http://image.tianjimedia.com/uploadImages/2013/329/E278VCB5R5BC_1000x500.jpg","利马索尔马拉松4","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-02-21/492617.html");
        RaceData r5=new RaceData("http://static.qjwb.com.cn/Mon_1411/235_258940_670150b237a58a1.jpg","利马索尔马拉松5","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-02-19/492602.html");
        RaceData r6=new RaceData("http://www.wj.gov.cn/images/web2010/xwzx/ztbd/2014/wjxthbcmls/xwdt/2014/9/29/29c451b2-791e-49ad-921c-d5d129679969.jpg","利马索尔马拉松6","2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");
        mRaceDataList.add(r1);
        mRaceDataList.add(r2);
        mRaceDataList.add(r3);
        mRaceDataList.add(r4);
        mRaceDataList.add(r5);
        mRaceDataList.add(r6);
    }

    //加载数据
    private void loadData() {
        int count= mRaceDataList.size();
        /* RaceData r1=new RaceData(R.drawable.race1,"利马索尔马拉松"+(count+1),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");
        RaceData r2=new RaceData(R.drawable.race2,"利马索尔马拉松"+(count+2),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");
        RaceData r3=new RaceData(R.drawable.race3,"利马索尔马拉松"+(count+3),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");*/
        RaceData r1=new RaceData("http://img.700bike.com.cn/upload/2014/07/201407181204533874.jpg","利马索尔马拉松"+(count+1),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");
        RaceData r2=new RaceData("http://gb.cri.cn/mmsource/images/2014/04/14/a554db12b2584cbdb555125be0251497.jpg","利马索尔马拉松"+(count+2),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");
        RaceData r3=new RaceData("http://imgpic.gmw.cn/dt/2013-10/20/20131020105713_2853.jpg","利马索尔马拉松"+(count+3),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");
        mRaceDataList.add(r1);
        mRaceDataList.add(r2);
        mRaceDataList.add(r3);
    }
    //加载数据
    private void refreshData() {
        int count= mRaceDataList.size();
      /*RaceData r1=new RaceData(R.drawable.race4,"新增马拉松"+(count+1),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");
        RaceData r2=new RaceData(R.drawable.race2,"新增马拉松"+(count+2),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");
        RaceData r3=new RaceData(R.drawable.race6,"新增马拉松"+(count+3),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");*/
        RaceData r1=new RaceData("http://att2.citysbs.com/taizhou/sns01/forum/2011/01/03-00/20110103_fe439bbabbd96378d29ad3pmfyx3DYtO.jpg","新增马拉松"+(count+1),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");
        RaceData r2=new RaceData("http://img.zjolcdn.com/pic/0/05/85/93/5859379_678575.jpg","新增马拉松"+(count+2),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");
        RaceData r3=new RaceData("http://www.lanzhou.cn/pic/0/10/47/75/10477576_997148.jpg","新增马拉松"+(count+3),"2016/09/10","76890","http://www.athletics.org.cn/competition/jsgc/2016-01-27/491827.html");
        mRaceDataList.add(0,r3);
        mRaceDataList.add(1,r2);
        mRaceDataList.add(2,r1);
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

    private void initSwiprRefresh() {
        mSwipeRefreshLayout= (SwipeRefreshLayout)mLeftView.findViewById(R.id.swiperefreshlayout);
        //设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        mSwipeRefreshLayout.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED, Color.YELLOW);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // mRaceDataList.clear();
                        refreshData();
                        mRaceAdapter.notifyDataSetChanged();
                        mRaceAdapter.notifyItemRemoved(mRaceAdapter.getItemCount());
                        mSwipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                },1000);
            }
        });
    }
    private void addListener() {
        //设置滑动监听事件
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("test", "StateChanged = " + newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("test", "onScrolled");
                lastVisibleItemPosition=mLinearLayoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition+1== mRaceAdapter.getItemCount()){
                    Log.d("test", "loading executed");
                    isRefreshing=mSwipeRefreshLayout.isRefreshing();
                    if (isRefreshing){
                        mRaceAdapter.notifyItemRemoved(mRaceAdapter.getItemCount());
                        return;
                    }
                    if (!isRefreshing)
                    {
                        isloading=true;
                        recyclerhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadData();
                                mSwipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                    }
                                });
                                mRaceAdapter.notifyDataSetChanged();
                                mRaceAdapter.notifyItemRemoved(mRaceAdapter.getItemCount());
                                Log.d("test", "load more completed");
                                isloading=false;
                            }
                        },500);
                    }
                }

            }
        });
    }
}
