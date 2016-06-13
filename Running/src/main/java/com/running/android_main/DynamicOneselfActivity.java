package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.running.adapters.DynamicOneselfAdapter;
import com.running.beans.DynamicOneselfBean;
import com.running.myviews.SuperSwipeRefreshLayout;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

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
    SuperSwipeRefreshLayout mOneselfSwipe;

    private DynamicOneselfCallBack mOneselfCallBack;
    String url = MyApplication.HOST + "dynamicOperateServlet";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    for (int i = 0; i < mOneselfCallBack.mOneselfBeen.size(); i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        if (i==0) {
                            map.put("type", IMG);
                            map.put("DynamicOneselfBean", mOneselfCallBack.mOneselfBeen.get(i));
                            mList.add(map);
                        }
                        map.put("type", IMG);
                        map.put("DynamicOneselfBean", mOneselfCallBack.mOneselfBeen.get(i));
                        mList.add(map);
                    }
                    mAdapter.notifyDataSetChanged();
                    mOneselfSwipe.post(new Runnable() {
                        @Override
                        public void run() {
                            mOneselfSwipe.setRefreshing(false);
                        }
                    });

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_oneself);
        ButterKnife.bind(this);

        //设置刷新颜色
//        mOneselfSwipe.setColorSchemeResources(
//                android.R.color.holo_blue_bright,
//                android.R.color.holo_blue_dark
//        );
        mOneselfSwipe.setTargetScrollWithLayout(false);
        mOneselfSwipe.post(new Runnable() {
            @Override
            public void run() {
                mOneselfSwipe.setRefreshing(true);
            }
        });

        initData();

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
                DynamicOneselfActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });

        mOneselfSwipe.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOneselfSwipe.setRefreshing(false);
                    }
                }, 2000);
            }

            @Override
            public void onPullDistance(int distance) {

            }

            @Override
            public void onPullEnable(boolean enable) {

            }
        });

        mOneselfSwipe.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {


            @Override
            public void onLoadMore() {
               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       mOneselfSwipe.setRefreshing(false);
                   }
               }, 2000);
            }

            @Override
            public void onPushDistance(int distance) {

            }

            @Override
            public void onPushEnable(boolean enable) {

            }
        });

        //RecyclerView下拉刷新
        /*mOneselfSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOneselfSwipe.setRefreshing(false);
                    }
                }, 2000);
            }
        });*/

        //RecyclerView上拉加载
        /*mOneselfRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                onLoading = false;
                            }
                        }, 2000);
                    }
                }
            }
        });*/
    }

    private void initData() {
        mList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        List<Object> objectList = new ArrayList<>();
        Intent intent = getIntent();
        int dId = intent.getIntExtra("dId", -1);
        if (dId == -1) {
        } else {
            OkHttpUtils.post()
                    .url(url)
                    .addParams("appRequest", "DynamicOneself")
                    .addParams("dId", String.valueOf(dId))
                    .build()
                    .execute(mOneselfCallBack = new DynamicOneselfCallBack());

        }
    }

    public class DynamicOneselfCallBack extends StringCallback {
        List<DynamicOneselfBean> mOneselfBeen = new ArrayList<>();

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            mOneselfBeen = gson.fromJson(response, new TypeToken<List<DynamicOneselfBean>>() {
            }.getType());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                }
            }).start();
        }
    }
}
