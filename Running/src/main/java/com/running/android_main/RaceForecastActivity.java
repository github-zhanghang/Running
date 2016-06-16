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
import android.widget.ImageView;

import com.google.gson.Gson;
import com.running.adapters.RaceAdapter;
import com.running.adapters.RaceForecastAdapter;
import com.running.beans.RaceData;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class RaceForecastActivity extends AppCompatActivity {

    private TopBar mTopBar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List<RaceData> raceDataList;
    private RaceForecastAdapter raceForecastAdapter;

    //上拉加载，下拉刷新
    private boolean isloading=false;
    private boolean isRefreshing=false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Handler recyclerhandler =new Handler();
    private int lastVisibleItemPosition;

    private int page=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_forecast);


        initView();
        initData();

        //下拉刷新
        addSwipe();
        //上拉加载
        addLoad();

        addBack();
        //点击跳转页面
        addClickListener();
    }

    private void addBack() {
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
              RaceForecastActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
    }

    private void initView() {
        mTopBar= (TopBar) findViewById(R.id.topbar_forecast);
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerview_forecast);
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swiperefresh_forecast);
        mLinearLayoutManager=new LinearLayoutManager(RaceForecastActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        raceDataList = new ArrayList<>();
        raceForecastAdapter=new RaceForecastAdapter(raceDataList,RaceForecastActivity.this);
        mRecyclerView.setAdapter(raceForecastAdapter);
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
                .url(MyApplication.HOST+"raceForecastServlet")
                .addParams("page",page+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("taoziforecast", "error=" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("taoziforecast", "his_response=" + response);
                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                RaceData raceData=new Gson().fromJson(jsonObject.toString(),RaceData.class);
                                raceDataList.add(raceData);
                            }
                            raceForecastAdapter.notifyDataSetChanged();
                            Log.e("taoziforecast", "his_response size=" + raceDataList.size());
                            raceForecastAdapter.notifyItemRemoved(raceForecastAdapter.getItemCount());
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


    private void addSwipe() {
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
                        raceDataList.clear();
                        refreshData();
                    }
                },1000);
            }
        });
    }

    private void addLoad() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                lastVisibleItemPosition=mLinearLayoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition+1== raceForecastAdapter.getItemCount()){
                    isRefreshing=mSwipeRefreshLayout.isRefreshing();
                    if (isRefreshing){
                        raceForecastAdapter.notifyItemRemoved(raceForecastAdapter.getItemCount());
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

    private void addClickListener() {
        //点击跳转页面
        raceForecastAdapter.setOnItemClickListener(new RaceForecastAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle=new Bundle();
                bundle.putString("weburl",raceDataList.get(position).getHtml());

                Intent intent=new Intent(RaceForecastActivity.this, RaceActivity.class);
                intent.putExtras(bundle);
               startActivity(intent);
            }
        });
    }
}
