package com.running.android_main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.running.adapters.HistoryAdapter;
import com.running.beans.History;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class HistoryActivity extends AppCompatActivity {
    private TopBar mTopBar;
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

    //用户
    int uid;
    int page=1;

    private MyApplication myApplication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        myApplication = (MyApplication) getApplication();
        uid=myApplication.getUserInfo().getUid();

        initView();
        initData();

        mHistoryAdapter=new HistoryAdapter(mHistoryList, HistoryActivity.this);
        mRecyclerView.setAdapter(mHistoryAdapter);

        //下拉刷新
        initSwipe();
        //上拉加载
        addLoad();
        //设置点击事件
        addClick();
    }

    private void initView() {
        mTopBar = (TopBar) findViewById(R.id.history_topbar);
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerview_history);
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swiperefresh_history);
        mLinearLayoutManager=new LinearLayoutManager(HistoryActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    private void initData() {

        recyclerhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1500);
    }

    private void getData() {
        refreshData();
    }

    private void refreshData() {
        OkHttpUtils.get()
                .url(MyApplication.HOST+"recordServlet")
                .addParams("uid",uid+"")
                .addParams("page", page +"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("my", "his_response=" + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                History history=new Gson().fromJson(jsonObject.toString(),History.class);
                                mHistoryList.add(history);
                            }

                            mHistoryAdapter.notifyDataSetChanged();
                            mHistoryAdapter.notifyItemRemoved(mHistoryAdapter.getItemCount());
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

    private void initSwipe() {
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
                        page =1;
                        mHistoryList.clear();
                        refreshData();
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
                    if (!isloading)
                    {
                        isloading=true;
                        recyclerhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refreshData();
                                isloading=false;
                            }
                        },500);
                        page++;
                    }
                }

            }
        });
    }

    private void addClick() {
        mHistoryAdapter.setOnItemClickListener(new HistoryAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle=new Bundle();
                bundle.putSerializable("history",mHistoryList.get(position));
                Intent intent=new Intent(HistoryActivity.this,HistoryDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                HistoryActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
    }
}
