package com.running.android_main;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.running.adapters.DynamicOneselfAdapter;
import com.running.myviews.TopBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DynamicOneselfActivity extends AppCompatActivity {

    public static final String IMG = "img";
    private List<HashMap<String, Object>> mList;
    private RecyclerView.LayoutManager mManager;
    private DynamicOneselfAdapter mAdapter;
    private LinearLayout mLayout;
    private boolean onLoading = false;
    private int lastVisibleItem;


    @Bind(R.id.dynamic_oneself_topBar)
    TopBar mDynamicOneselfTopBar;
    @Bind(R.id.dynamic_oneself_recyclerView)
    RecyclerView mOneselfRecyclerView;
    @Bind(R.id.dynamic_oneself_swipe)
    SwipeRefreshLayout mOneselfSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_oneself);
        ButterKnife.bind(this);
        initData();

        //设置刷新颜色
        mOneselfSwipe.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_dark
        );

        //设置RecyclerView
        mManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mOneselfRecyclerView.setLayoutManager(mManager);
        mOneselfRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DynamicOneselfAdapter(this, mList);
        mOneselfRecyclerView.setAdapter(mAdapter);

        //RecyclerView的item监听事件
        mAdapter.setOnItemClickListener(new DynamicOneselfAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, HashMap<String, Object> map) {
                Toast.makeText(DynamicOneselfActivity.this, map.get("content").toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        addListener();
    }

    private void addListener() {
        //顶部TopBar监听事件
        mDynamicOneselfTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                Toast.makeText(DynamicOneselfActivity.this, "返回", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });

        //RecyclerView下拉刷新
        mOneselfSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mOneselfSwipe.setRefreshing(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<HashMap<String,Object>> list=new ArrayList<HashMap<String, Object>>();
                        HashMap<String,Object> map=new HashMap<String, Object>();
                        List<String> imgList = new ArrayList<>();
                        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP." +
                                "pVXXXXcAXXXXXXXXXXXX_!!2237636884.jpg");
                        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP." +
                                "pVXXXXcAXXXXXXXXXXXX_!!2237636884.jpg");
                        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP." +
                                "pVXXXXcAXXXXXXXXXXXX_!!2237636884.jpg");
                        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP." +
                                "pVXXXXcAXXXXXXXXXXXX_!!2237636884.jpg");

                        map.put("time", "2016-05-25 15:30");
                        map.put("content", "四月，你好");
                        map.put("imgList", imgList);
                        map.put("type", IMG);

                        list.add(map);
                        mList.addAll(1,list);
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        mAdapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });

        //RecyclerView上拉加载
        mOneselfRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) mManager;
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem == (mAdapter.getItemCount() - 1)) {
                    boolean isRefreshing = mOneselfSwipe.isRefreshing();
                    if (isRefreshing) {
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        return;
                    }
                    if (!onLoading) {
                        onLoading = true;
                        mOneselfRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                List<HashMap<String, Object>> list = new
                                        ArrayList<HashMap<String, Object>>();
                                HashMap<String,Object> map=new HashMap<String, Object>();
                                List<String> imgList = new ArrayList<>();
                                imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP." +
                                        "pVXXXXcAXXXXXXXXXXXX_!!2237636884.jpg");
                                imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP." +
                                        "pVXXXXcAXXXXXXXXXXXX_!!2237636884.jpg");
                                imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP." +
                                        "pVXXXXcAXXXXXXXXXXXX_!!2237636884.jpg");
                                imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP." +
                                        "pVXXXXcAXXXXXXXXXXXX_!!2237636884.jpg");

                                map.put("time", "2016-05-25 15:30");
                                map.put("content", "四月，你好");
                                map.put("imgList", imgList);
                                map.put("type", IMG);

                                list.add(map);
                                mList.addAll(list);
                                mAdapter.notifyDataSetChanged();
                                onLoading = false;
                            }
                        }, 2000);
                    }
                }
            }
        });
    }

    private void initData() {
        mList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("backImg",0);
        map.put("headImg",1);
        map.put("name","桃子");
        map.put("sex","女");
        mList.add(map);
        map=new HashMap<>();
        List<String> imgList = new ArrayList<>();
        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP.pVXXXXcAXXXXXXXXXXXX_" +
                "!!2237636884.jpg");
        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP.pVXXXXcAXXXXXXXXXXXX_" +
                "!!2237636884.jpg");
        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP.pVXXXXcAXXXXXXXXXXXX_" +
                "!!2237636884.jpg");
        imgList.add("https://img.alicdn.com/imgextra/i2/2237636884/TB2gaP.pVXXXXcAXXXXXXXXXXXX_" +
                "!!2237636884.jpg");

        map.put("time", "2016-05-25 15:30");
        map.put("content", "四月，你好");
        map.put("imgList", imgList);
        map.put("type", IMG);

        mList.add(map);
        mList.add(map);
    }
}
