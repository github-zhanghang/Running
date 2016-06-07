package com.running.android_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.running.adapters.RankActivityAdapter;
import com.running.beans.RankItemInfo;
import com.running.beans.RankList;
import com.running.myviews.CircleImageView;
import com.running.myviews.TopBar;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {
    public int START = 0;
    private MyApplication mApplication;
    private TopBar mTopBar;
    private CircleImageView mImageView;
    private TextView mDistanceTextView, mRankTextView;

    private RadioGroup mRadioGroup;
    private PullToRefreshListView mListView;
    private RankActivityAdapter mAdapter;
    private List<RankItemInfo> mList = new ArrayList<>();
    private static final String mPath = "http://192.168.191.1:8080/Running/rankServlet";
    private static final int WHAT_DAY = 1;
    private static final int WHAT_TOTAL = 2;
    private RequestQueue mRequestQueue = NoHttp.newRequestQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        mApplication = (MyApplication) getApplication();
        mApplication.addActivity(this);
        initViews();
        initMyData();
        //默认加载日排行榜
        loadDayData();
        initListeners();
    }

    private void initViews() {
        mTopBar = (TopBar) findViewById(R.id.rank_topbar);
        mImageView = (CircleImageView) findViewById(R.id.rank_my_img);
        mDistanceTextView = (TextView) findViewById(R.id.rank_my_distance);
        mRankTextView = (TextView) findViewById(R.id.rank_my_rank);
        mRadioGroup = (RadioGroup) findViewById(R.id.rank_group);
        mListView = (PullToRefreshListView) findViewById(R.id.rank_listview);
        //设置pull-to-refresh模式为Mode.Both
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
    }

    private void initMyData() {
        Glide.with(RankActivity.this)
                .load(mApplication.getUserInfo().getImageUrl())
                .fitCenter()
                .error(R.mipmap.ic_launcher)
                .into(mImageView);
        mDistanceTextView.setText("距离:" + mApplication.getDistance() + "km");
    }

    private void getMyRank(String type) {

    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            Log.e("my", "result=" + result);
            if (result.equals("[]") || result == null) {
                Toast.makeText(RankActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                return;
            }
            if (what == WHAT_DAY) {
                List<RankList> list = new Gson().fromJson(result, new TypeToken<ArrayList<RankList>>() {
                }.getType());
                for (int i = START; i < list.size(); i++) {
                    RankList rankList = list.get(i);
                    mList.add(new RankItemInfo("" + (i + 1), rankList.getmNickName(),
                            rankList.getmImageUrl(), rankList.getmDistance()));
                    mAdapter = new RankActivityAdapter(RankActivity.this, mList, mListView);
                    mListView.setAdapter(mAdapter);
                    START += 10;
                }
            } else if (what == WHAT_TOTAL) {
                List<RankList> list = new Gson().fromJson(result, new TypeToken<ArrayList<RankList>>() {
                }.getType());
                for (int i = START; i < list.size(); i++) {
                    RankList rankList = list.get(i);
                    mList.add(new RankItemInfo("" + (i + 1), rankList.getmNickName(),
                            rankList.getmImageUrl(), rankList.getmDistance()));
                    mAdapter = new RankActivityAdapter(RankActivity.this, mList, mListView);
                    mListView.setAdapter(mAdapter);
                    START += 10;
                }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            if (what == WHAT_DAY || what == WHAT_TOTAL) {
                Toast.makeText(RankActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish(int what) {
        }
    };

    private void loadDayData() {
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "day");
        request.add("index", START);
        mRequestQueue.add(WHAT_DAY, request, onResponseListener);
        mRequestQueue.start();
    }

    private void loadTotalData() {
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "total");
        request.add("index", START);
        mRequestQueue.add(WHAT_TOTAL, request, onResponseListener);
        mRequestQueue.start();
    }

    private void initListeners() {
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                RankActivity.this.finish();
                mApplication.removeActivity(RankActivity.this);
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                START = 0;
                mList = null;
                mList = new ArrayList<RankItemInfo>();
                if (checkedId == R.id.rank_day) {
                    loadDayData();
                } else {
                    loadTotalData();
                }
                mAdapter = new RankActivityAdapter(RankActivity.this, mList, mListView);
                mListView.setAdapter(mAdapter);
            }
        });

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                START = 0;
                mList = null;
                mList = new ArrayList<RankItemInfo>();
                if (mRadioGroup.getCheckedRadioButtonId() == R.id.rank_day) {
                    loadDayData();
                } else {
                    loadTotalData();
                }
                mAdapter = new RankActivityAdapter(RankActivity.this, mList, mListView);
                mListView.setAdapter(mAdapter);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mRadioGroup.getCheckedRadioButtonId() == R.id.rank_day) {
                    loadDayData();
                } else {
                    loadTotalData();
                }
                mAdapter = new RankActivityAdapter(RankActivity.this, mList, mListView);
                mListView.setAdapter(mAdapter);
            }
        });
    }
}
