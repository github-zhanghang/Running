package com.running.android_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.running.adapters.RankActivityAdapter;
import com.running.beans.RankItemInfo;
import com.running.myviews.TopBar;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {
    public int START = 1;
    private MyApplication mApplication;
    private TopBar mTopBar;
    private RadioGroup mRadioGroup;
    private PullToRefreshListView mListView;
    private RankActivityAdapter mAdapter;
    private List<RankItemInfo> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        mApplication = (MyApplication) getApplication();
        mApplication.addActivity(this);
        initViews();
        //默认加载日排行榜
        loadDayData();
        mAdapter = new RankActivityAdapter(this, mList, mListView);
        mListView.setAdapter(mAdapter);
        initListeners();
    }

    private void initViews() {
        mTopBar = (TopBar) findViewById(R.id.rank_topbar);
        mRadioGroup = (RadioGroup) findViewById(R.id.rank_group);
        mListView = (PullToRefreshListView) findViewById(R.id.rank_listview);
        //设置pull-to-refresh模式为Mode.Both
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
    }

    private void loadDayData() {
        for (int i = START; i < START + 5; i++) {
            String postion = "" + i;
            String imgUrl = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=4046719105,2434242014&fm=58";
            String nickname = "日排行昵称" + i;
            String distance = 1000 - i + "km";
            mList.add(new RankItemInfo(postion, nickname, imgUrl, distance));
        }
        START += 5;
    }

    private void refreshDayData() {
        mList = new ArrayList<>();
        START = 1;
        for (int i = START; i < START + 5; i++) {
            String postion = "" + i;
            String imgUrl = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=4046719105,2434242014&fm=58";
            String nickname = "日排行昵称" + i;
            String distance = 1000 - i + "km";
            mList.add(new RankItemInfo(postion, nickname, imgUrl, distance));
        }
        START += 5;
    }

    private void loadTotalData() {
        for (int i = START; i < START + 5; i++) {
            String postion = "" + i;
            String imgUrl = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=4046719105,2434242014&fm=58";
            String nickname = "总排行昵称" + i;
            String distance = 1000 - i + "km";
            mList.add(new RankItemInfo(postion, nickname, imgUrl, distance));
        }
        START += 5;
    }

    private void refreshTotalData() {
        mList = new ArrayList<>();
        START = 1;
        for (int i = START; i < START + 5; i++) {
            String postion = "" + i;
            String imgUrl = "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=4046719105,2434242014&fm=58";
            String nickname = "总排行昵称" + i;
            String distance = 1000 - i + "km";
            mList.add(new RankItemInfo(postion, nickname, imgUrl, distance));
        }
        START += 5;
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
                START = 1;
                if (checkedId == R.id.rank_day) {
                    mList = new ArrayList<>();
                    loadDayData();
                } else {
                    mList = new ArrayList<>();
                    loadTotalData();
                }
                mAdapter = new RankActivityAdapter(RankActivity.this, mList, mListView);
                mListView.setAdapter(mAdapter);
            }
        });

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mRadioGroup.getCheckedRadioButtonId() == R.id.rank_day) {
                    refreshDayData();
                } else {
                    refreshTotalData();
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
