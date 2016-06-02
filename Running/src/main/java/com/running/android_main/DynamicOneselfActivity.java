package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.running.adapters.DynamicOneselfAdapter;
import com.running.myviews.TopBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DynamicOneselfActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener {

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
        mOneselfSwipe.setOnRefreshListener(this);
        mOneselfSwipe.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_blue_dark
        );
        mManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mOneselfRecyclerView.setLayoutManager(mManager);
        mOneselfRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DynamicOneselfAdapter(mList, this);
        addFootView(mOneselfRecyclerView);
        addHeadView(mOneselfRecyclerView);
        mOneselfRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new DynamicOneselfAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, HashMap<String, Object> map) {
                Toast.makeText(DynamicOneselfActivity.this, map.get("content").toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        addListener();
        //上拉加载有bug
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
                if (lastVisibleItem == (mManager.getItemCount() - 2)) {
                    boolean isRefreshing = mOneselfSwipe.isRefreshing();
                    if (isRefreshing) {
                        mLayout.setVisibility(View.GONE);
                        return;
                    }
                    if (!onLoading) {
                        mLayout.setVisibility(View.VISIBLE);
                        onLoading = true;
                        mOneselfRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mLayout.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();
                                onLoading = false;
                            }
                        }, 2000);
                    }
                }
            }
        });
    }

    private void addListener() {
        mDynamicOneselfTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                Toast.makeText(DynamicOneselfActivity.this, "返回", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
    }

    private void initData() {
        mList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.dynamic_test);
        list.add(R.drawable.dynamic_test);
        list.add(R.drawable.dynamic_test);
        list.add(R.drawable.dynamic_test);

        map.put("time", "2016-05-25 15:30");
        map.put("content", "四月，你好");
        map.put("imgList", list);
        map.put("type", IMG);

        mList.add(map);
        mList.add(map);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mOneselfSwipe.setRefreshing(false);

                mAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    private void addFootView(RecyclerView recyclerView) {
        View foot = LayoutInflater.from(this).inflate(R.layout.dynamic_load_foot,
                mOneselfRecyclerView, false);
        mLayout = (LinearLayout) foot.findViewById(R.id.dynamic_footer_LinearLayout);
        mLayout.setVisibility(View.GONE);
        mAdapter.setFootView(foot);
    }

    private void addHeadView(RecyclerView recyclerView) {
        View header = LayoutInflater.from(this).inflate(R.layout.dynamic_header, null);
        ImageView imageView = (ImageView) header.findViewById(R.id.dynamic_header_background);
        Glide.with(this).
                load(R.drawable.dynamic_test).
                thumbnail(0.1f).
                into(imageView);
        ImageView headImg = (ImageView) header.findViewById(R.id.dynamic_header_head_img);
        Glide.with(this)
                .load(R.drawable.head_photo)
                .thumbnail(0.1f)
                .into(headImg);
        headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DynamicOneselfActivity.this,
                        PersonInformationActivity.class));
            }
        });
        mAdapter.setHeadView(header);
    }
}
