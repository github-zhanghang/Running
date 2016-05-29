package com.running.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.running.adapters.DynamicAdapter;
import com.running.android_main.MainActivity;
import com.running.android_main.R;
import com.running.beans.DynamicImgBean;
import com.running.beans.DynamicLinkBean;
import com.running.myviews.TopBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by ldd on 2016/5/28.
 * 动态主界面
 */
public class DongtaiFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String IMG = "img";
    public static final String LINK = "link";
    private MainActivity mActivity;

    //动态内容界面
    private View mView;
    private TopBar mTopBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private List<HashMap<String, Object>> mList;
    private View mHeaderView, mFootView;
    private LinearLayout mLinearLayout;
    boolean IS_LOADING = false;
    private DynamicAdapter mDynamicAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dongtai, container, false);
        initViews();
        initData();
        setListeners();
        //下拉加载
        addLoadListener();
        return mView;
    }

    private void addLoadListener() {
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                boolean isRefreshing = mSwipeRefreshLayout.isRefreshing();
                if (isRefreshing) {
                    //mLinearLayout.setVisibility(View.GONE);
                    return;
                }
                if ((mListView.getLastVisiblePosition() == mListView.getCount() - 1) &&
                        !IS_LOADING) {
                    IS_LOADING = true;
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //加载相关操作

                            mLinearLayout.setVisibility(View.GONE);
                            mDynamicAdapter.notifyDataSetChanged();
                            IS_LOADING = false;
                        }
                    }, 2000);
                }
            }
        });
    }

    private void initViews() {
        mActivity = (MainActivity) getActivity();
        mTopBar = (TopBar) mView.findViewById(R.id.dongtai_topbar);

        //上拉刷新
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(
                R.id.dynamic_swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        //设置刷新动画的颜色
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_blue_dark);
        mListView = (ListView) mView.findViewById(R.id.dynamic_listView);
    }

    private void initData() {
        mList = new ArrayList<>();
        addImgDynamic();
        addLinkDynamic();
        addTestDynamic();

        //listView添加headView(添加headView和footView都要在添加适配器前进行)
        //HeaderView
        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_header,
                mListView, false);
        ImageView imageView = (ImageView) mHeaderView.findViewById(R.id.dynamic_header_background);
        Glide.with(this).
                load(R.drawable.dynamic_test).
                thumbnail(0.1f).
                into(imageView);
        mListView.addHeaderView(mHeaderView);

        //添加FootView
        mFootView = LayoutInflater.from(getActivity()).inflate(R.layout.dynamic_load_foot,
                mListView, false);
        mLinearLayout = (LinearLayout) mFootView.findViewById(R.id.dynamic_footer_LinearLayout);
        mLinearLayout.setVisibility(View.GONE);
        mListView.addFooterView(mFootView);

        mDynamicAdapter = new DynamicAdapter(getActivity(), mList);
        mListView.setAdapter(mDynamicAdapter);
    }

    //添加普通动态
    private void addImgDynamic() {
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.dynamic_test);
        list.add(R.drawable.dynamic_test);
        list.add(R.drawable.dynamic_test);
        list.add(R.drawable.dynamic_test);
        list.add(R.drawable.dynamic_test);
        list.add(R.drawable.dynamic_test);
        list.add(R.drawable.dynamic_test);
        DynamicImgBean dynamicImgBean = new DynamicImgBean(
                R.drawable.head_photo, "无名", "0415 07:15",
                "四月，你好",
                list, 28, 4,
                IMG);
        HashMap<String, Object> imgMap = new HashMap<>();
        imgMap.put("img", dynamicImgBean);
        imgMap.put("type", IMG);
        mList.add(imgMap);
    }

    private void addLinkDynamic() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("linkImg", R.mipmap.ic_launcher);
        map.put("linkName", "跑步5.04km");
        map.put("linkData", "用时36分17秒，平均配速07分17秒");
        DynamicLinkBean dynamicLinkBean = new DynamicLinkBean(R.drawable.head_photo, "无名",
                "04-15 07:15",
                "四月，你好",
                map,
                28, 4, LINK);
        HashMap<String, Object> imgMap = new HashMap<>();
        imgMap.put("link", dynamicLinkBean);
        imgMap.put("type", LINK);
        mList.add(imgMap);
    }

    //测试添加
    private void addTestDynamic() {
        DynamicImgBean dynamicImgBean = null;
        for (int i = 0; i < 7; i++) {
            List<Integer> list = new ArrayList<>();
            add(i, list, dynamicImgBean);
        }
    }

    private void add(int i, List<Integer> list, DynamicImgBean dynamicImgBean) {
        for (int j = 0; j < i; j++) {
            list.add(R.drawable.dynamic_test);
        }
        dynamicImgBean = new DynamicImgBean(
                R.drawable.head_photo, "无名", "0415 07:15",
                "四月，你好",
                list, 28, 4,
                IMG);
        HashMap<String, Object> imgMap = new HashMap<>();
        imgMap.put("img", dynamicImgBean);
        imgMap.put("type", IMG);
        mList.add(imgMap);
    }

    private void setListeners() {
        //头部左右两边图片的点击事件
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            //左边
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }

            //右边
            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
                Toast.makeText(mActivity, "发布动态", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //上拉刷新数据
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                //刷新相关逻辑操作

                mDynamicAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }
}
