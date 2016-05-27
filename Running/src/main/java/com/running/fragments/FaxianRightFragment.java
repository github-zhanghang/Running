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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.running.adapters.GoodsAdapter;
import com.running.adapters.GoodsBannerAdapter;
import com.running.android_main.MainActivity;
import com.running.android_main.R;
import com.running.android_main.RaceActivity;
import com.running.beans.GoodsBannerData;
import com.running.beans.GoodsData;
import com.running.myviews.NoScrollView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ZhangHang on 2016/5/5.
 */
public class FaxianRightFragment extends Fragment {
    MainActivity mMainActivity;
    private View mRightView;

    //轮播图
    List<ImageView> mImageViews;
    List<View> dotsViews;
    List<GoodsBannerData> mGoodsBannerDatas=new ArrayList<>();
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
            currentItem=(currentItem+1)%mImageViews.size();
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
    int[] lastVisibleItemPosition;

    private NoScrollView mNoScrollView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainActivity= (MainActivity) getActivity();
        mRightView = inflater.inflate(R.layout.faxian_right, null);

        initBannerData();
        initBannerView();
        addBannerListener();

        initGoodView();
        initGoodData();

        initSwiprRefresh();
        //addListener();

        addClickListener();
        initNoScollView();
        return mRightView;
    }

    private void addClickListener() {
        mGoodsAdapter.setOnItemClickListener(new GoodsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Toast.makeText(mMainActivity,"装备:"+(position+1),Toast.LENGTH_SHORT).show();
                Bundle bundle=new Bundle();
                bundle.putString("weburl",mGoodsDataList.get(position).getWeburl());

                Intent intent=new Intent(mMainActivity, RaceActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
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
                        if((scrollY+height) + 50 >=scrollViewMeasureHeight){

                            if(!isloading) {

                                isloading = true;
                                loadData();
                                mGoodsBannerAdapter.notifyDataSetChanged();
                                mGoodsAdapter.notifyItemRemoved(mGoodsAdapter.getItemCount());
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
        GoodsBannerData b1=new GoodsBannerData(R.drawable.gb1,"https://iem.taobao.com/item.htm?spm=a219r.lm944.14.71.Tl70OR&id=526213215934&ns=1&abbucket=19#detail");
        GoodsBannerData b2=new GoodsBannerData(R.drawable.gb2,"https://item.taobao.com/item.htm?spm=a219r.lm944.14.71.Tl70OR&id=526213215934&ns=1&abbucket=19#detail");
        GoodsBannerData b3=new GoodsBannerData(R.drawable.gb3,"https://item.taobao.com/item.htm?spm=a219r.lm944.14.71.Tl70OR&id=526213215934&ns=1&abbucket=19#detail");
        GoodsBannerData b4=new GoodsBannerData(R.drawable.gb4,"https://item.taobao.com/item.htm?spm=a219r.lm944.14.71.Tl70OR&id=526213215934&ns=1&abbucket=19#detail");
        GoodsBannerData b5=new GoodsBannerData(R.drawable.gb5,"https://item.taobao.com/item.htm?spm=a219r.lm944.14.71.Tl70OR&id=526213215934&ns=1&abbucket=19#detail");
        mGoodsBannerDatas.add(b1);
        mGoodsBannerDatas.add(b2);
        mGoodsBannerDatas.add(b3);
        mGoodsBannerDatas.add(b4);
        mGoodsBannerDatas.add(b5);
    }

    private void initBannerView() {
        //图片
        mImageViews =new ArrayList<>();
        for (int i = 0; i <mGoodsBannerDatas.size() ; i++) {
            ImageView imageView=new ImageView(mMainActivity);
            //imageView.setImageResource(mBannerDatas.get(i).getPic());
            Glide.with(mMainActivity)
                    .load(mGoodsBannerDatas.get(i).getPic())
                    .error(R.drawable.fail)
                    .centerCrop()
                    .into(imageView);
            mImageViews.add(imageView);
        }
        //点
        dotsViews=new ArrayList<>();
        dotsViews.add(mRightView.findViewById(R.id.g_dot0));
        dotsViews.add(mRightView.findViewById(R.id.g_dot1));
        dotsViews.add(mRightView.findViewById(R.id.g_dot2));
        dotsViews.add(mRightView.findViewById(R.id.g_dot3));
        dotsViews.add(mRightView.findViewById(R.id.g_dot4));
        //viewpager
        mBannerViewPager= (ViewPager) mRightView.findViewById(R.id.vp_banner_goods);
        mGoodsBannerAdapter=new GoodsBannerAdapter(mMainActivity,mGoodsBannerDatas,mImageViews);
        mBannerViewPager.setAdapter(mGoodsBannerAdapter);
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
        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(),3,3, TimeUnit.SECONDS);
    }

    private void initGoodData() {
        GoodsData g1=new GoodsData(R.drawable.p1,"我是推荐装备1","9999","https://item.taobao.com/item.htm?spm=a230r.1.14.23.KUPW1T&id=526925343827&ns=1&abbucket=19#detail");
        GoodsData g2=new GoodsData(R.drawable.p12,"我是推荐装备2","9999","https://item.taobao.com/item.htm?spm=a230r.1.14.45.KUPW1T&id=528481007031&ns=1&abbucket=19#detail");
        GoodsData g3=new GoodsData(R.drawable.p11,"我是推荐装备3","9999","https://item.taobao.com/item.htm?spm=a230r.1.14.16.KUPW1T&id=531563358148&ns=1&abbucket=19#detail");
        GoodsData g4=new GoodsData(R.drawable.p10,"我是推荐装备4","9999","https://item.taobao.com/item.htm?spm=a230r.1.14.65.KUPW1T&id=528503126638&ns=1&abbucket=19#detail");
        GoodsData g5=new GoodsData(R.drawable.p11,"我是推荐装备5","9999","https://item.taobao.com/item.htm?spm=a219r.lm944.14.1.MX7uR6&id=43268531240&ns=1&abbucket=19#detail");
        GoodsData g6=new GoodsData(R.drawable.p3,"我是推荐装备6","9999","https://item.taobao.com/item.htm?spm=a219r.lm944.14.48.MX7uR6&id=530800618134&ns=1&abbucket=19#detail");
        mGoodsDataList.add(g1);
        mGoodsDataList.add(g2);
        mGoodsDataList.add(g3);
        mGoodsDataList.add(g4);
        mGoodsDataList.add(g5);
        mGoodsDataList.add(g6);
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
        mSwipeRefreshLayout.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED, Color.YELLOW);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        mGoodsAdapter.notifyDataSetChanged();
                        mGoodsAdapter.notifyItemRemoved(mGoodsAdapter.getItemCount());
                        mSwipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                }, 1000);
            }
        });
    }
    private void addListener() {
        goodRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastVisibleItemPosition=mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);
                if (lastVisibleItemPosition[1]+1==mGoodsAdapter.getItemCount()||
                        (lastVisibleItemPosition[1]+2==mGoodsAdapter.getItemCount())) {
                    Log.d("test", "loading executed");
                    boolean isRefreshing = mSwipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        mGoodsAdapter.notifyItemRemoved(mGoodsAdapter.getItemCount());
                        return;
                    }
                    if (!isRefreshing) {
                        isloading = true;
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
                                mGoodsAdapter.notifyDataSetChanged();
                                mGoodsAdapter.notifyItemRemoved(mGoodsAdapter.getItemCount());
                                isloading = false;
                            }
                        }, 500);
                    }
                }
            }
        });
    }

    private void refreshData() {
        int count= mGoodsDataList.size();
        GoodsData g1=new GoodsData(R.drawable.p10,"新增装备"+(count+1),"9999","https://item.taobao.com/item.htm?spm=a219r.lm944.14.48.MX7uR6&id=530800618134&ns=1&abbucket=19#detail");
        GoodsData g2=new GoodsData(R.drawable.p11,"新增装备"+(count+2),"9999","https://item.taobao.com/item.htm?spm=a219r.lm944.14.48.MX7uR6&id=530800618134&ns=1&abbucket=19#detail");
        GoodsData g3=new GoodsData(R.drawable.p12,"新增装备"+(count+3),"9999","https://item.taobao.com/item.htm?spm=a219r.lm944.14.48.MX7uR6&id=530800618134&ns=1&abbucket=19#detail");
        mGoodsDataList.add(0,g3);
        mGoodsDataList.add(1,g2);
        mGoodsDataList.add(2,g1);
    }
    private void loadData() {
        int count= mGoodsDataList.size();
        GoodsData g1=new GoodsData(R.drawable.p3,"加载装备"+(count+1),"9999","https://item.taobao.com/item.htm?spm=a219r.lm944.14.48.MX7uR6&id=530800618134&ns=1&abbucket=19#detail");
        GoodsData g2=new GoodsData(R.drawable.p11,"加载装备"+(count+2),"9999","https://item.taobao.com/item.htm?spm=a219r.lm944.14.48.MX7uR6&id=530800618134&ns=1&abbucket=19#detail");
        GoodsData g3=new GoodsData(R.drawable.p12,"新增装备"+(count+3),"9999","https://item.taobao.com/item.htm?spm=a219r.lm944.14.48.MX7uR6&id=530800618134&ns=1&abbucket=19#detail");
        mGoodsDataList.add(g1);
        mGoodsDataList.add(g2);
        mGoodsDataList.add(g3);
    }
}
