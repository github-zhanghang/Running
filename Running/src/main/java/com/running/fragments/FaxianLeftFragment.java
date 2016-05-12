package com.running.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.running.adapters.FaxianLeftListViewAdapter;
import com.running.android_main.R;
import com.running.beans.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangHang on 2016/5/5.
 */
public class FaxianLeftFragment extends Fragment {
    private View mLeftView;
    private PullToRefreshListView mListView;
    List<Music> mList = new ArrayList<>();
    private FaxianLeftListViewAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLeftView = inflater.inflate(R.layout.faxian_left, null);
        initViews();
        loadData();
        setListeners();
        mAdapter = new FaxianLeftListViewAdapter(getContext(), mList);
        mListView.setAdapter(mAdapter);
        //3.设置上拉加载下拉刷新组件和事件监听
        //设置刷新模式为BOTH才可以上拉和下拉都能起作用,必须写在前面
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        //设置刷新时头部的状态
        initRefreshListView();
        return mLeftView;
    }

    private void loadData() {
        //模拟数据
        for (int i = 0; i < 10; i++) {
            mList.add(new Music("30天2小时" + (i + 1) + "分", "" + (i + 100)));
        }
    }

    public void initRefreshListView() {
        ILoadingLayout startLabels = mListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");
        startLabels.setRefreshingLabel("正在拉");
        startLabels.setReleaseLabel("放开刷新");
        ILoadingLayout endLabels = mListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");
        endLabels.setRefreshingLabel("正在载入...");
        endLabels.setReleaseLabel("放开加载...");
    }

    private void setListeners() {
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新
                new LoadDataAsyncTask(FaxianLeftFragment.this).execute();//执行下载数据
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载
                new LoadDataAsyncTask(FaxianLeftFragment.this).execute();//执行下载数据
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "第" + position + "项", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        mListView = (PullToRefreshListView) mLeftView.findViewById(R.id.faxian_left_listview);
    }

    static class LoadDataAsyncTask extends AsyncTask<Void, Void, String> {//定义返回值的类型
        //后台处理
        private FaxianLeftFragment fragment;

        public LoadDataAsyncTask(FaxianLeftFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected String doInBackground(Void... params) {
            //用一个线程来模拟刷新
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //加载数据
            fragment.loadData();
            return "success";
        }

        //  onPostExecute（）是对返回的值进行操作
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if ("success".equals(s)) {
                fragment.mAdapter.notifyDataSetChanged();//通知数据集改变,界面刷新
                fragment.mListView.onRefreshComplete();//表示刷新完成
            }
        }
    }
}
