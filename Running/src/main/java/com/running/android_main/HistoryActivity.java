package com.running.android_main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.running.adapters.HistoryAdapter;
import com.running.beans.History;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List<History> mHistoryList=new ArrayList<>();
    private HistoryAdapter mHistoryAdapter;

    //上拉加载，下拉刷新
    private boolean isloading=false;
    private boolean isRefreshing=false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Handler recyclerhandler =new Handler();
    private int lastVisibleItemPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initView();
        initData();

        mLinearLayoutManager=new LinearLayoutManager(HistoryActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mHistoryAdapter=new HistoryAdapter(mHistoryList, HistoryActivity.this);
        mRecyclerView.setAdapter(mHistoryAdapter);

        //设置点击事件
        mHistoryAdapter.setOnItemClickListener(new HistoryAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(HistoryActivity.this,HistoryDetailActivity.class);
                startActivity(intent);
            }
        });

        initSwipe();
        addLoad();
    }

    private void initSwipe() {
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
                        mHistoryAdapter.notifyDataSetChanged();
                        mHistoryAdapter.notifyItemRemoved(mHistoryAdapter.getItemCount());
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

    private void addLoad() {
        //设置滑动监听事件
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastVisibleItemPosition=mLinearLayoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition+1== mHistoryAdapter.getItemCount()){
                    isRefreshing=mSwipeRefreshLayout.isRefreshing();
                    if (isRefreshing){
                        mHistoryAdapter.notifyItemRemoved(mHistoryAdapter.getItemCount());
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
                                mHistoryAdapter.notifyDataSetChanged();
                                mHistoryAdapter.notifyItemRemoved(mHistoryAdapter.getItemCount());
                                Log.d("test", "load more completed");
                                isloading=false;
                            }
                        },500);
                    }
                }

            }
        });
    }

    private void loadData() {
        History h1=new History("2016/05/29","4.23","00:50:61");
        History h2=new History("2016/05/30","4.23","00:50:61");
        History h3=new History("2016/05/31","4.23","00:50:61");
        mHistoryList.add(h1);
        mHistoryList.add(h2);
        mHistoryList.add(h3);
    }

    private void initData() {
        History h1=new History("2016/05/20","4.23","00:50:61");
        History h2=new History("2016/05/21","4.23","00:50:61");
        History h3=new History("2016/05/22","4.23","00:50:61");
        History h4=new History("2016/05/23","4.23","00:50:61");
        History h5=new History("2016/05/24","4.23","00:50:61");
        History h6=new History("2016/05/25","4.23","00:50:61");
        mHistoryList.add(h1);
        mHistoryList.add(h2);
        mHistoryList.add(h3);
        mHistoryList.add(h4);
        mHistoryList.add(h5);
        mHistoryList.add(h6);
    }
    private void refreshData() {
        History h1=new History("2016/05/26","4.23","00:50:61");
        History h2=new History("2016/05/27","4.23","00:50:61");
        History h3=new History("2016/05/28","4.23","00:50:61");
        mHistoryList.add(0,h1);
        mHistoryList.add(1,h2);
        mHistoryList.add(2,h3);

    }

    private void initView() {
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerview_history);
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swiperefresh_history);
    }
}
