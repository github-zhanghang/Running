package com.running.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.running.adapters.DynamicAdapter;
import com.running.android_main.MainActivity;
import com.running.android_main.MyApplication;
import com.running.android_main.PublishDynamicActivity;
import com.running.android_main.R;
import com.running.beans.DynamicImgBean;
import com.running.myviews.TopBar;
import com.running.utils.GlideCircleTransform;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;


/**
 * Created by ldd on 2016/5/28.
 * 动态主界面
 */
public class DongtaiFragment extends Fragment {
    public static final String IMG = "img";
    public static final String LINK = "link";
    private MainActivity mActivity;

    //动态内容界面
    private View mView;
    private TopBar mTopBar;
    private ImageView uImg, sexImg;
    private PullToRefreshListView mListView;
    private List<HashMap<String, Object>> mList;
    private View mHeaderView;
    private LinearLayout mLinearLayout;
    boolean IS_LOADING = false;
    private DynamicAdapter mDynamicAdapter;

    MyApplication myApplication;

    String url = MyApplication.HOST + "dynamicOperateServlet";

    private DynamicCallBack dynamicCallBack = new DynamicCallBack();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    for (int i = 0; i < dynamicCallBack.imgBeanList.size(); i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("type", IMG);
                        map.put("DynamicBean", dynamicCallBack.imgBeanList.get(i));
                        mList.add(map);
                    }
                    mDynamicAdapter.notifyDataSetChanged();
                    mListView.onRefreshComplete();
                    Log.d("TAG", "" + mList.size());

                    break;
                case 2:
                    mList.clear();
                    for (int i = 0; i < dynamicCallBack.imgBeanList.size(); i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("type", IMG);
                        map.put("DynamicBean", dynamicCallBack.imgBeanList.get(i));
                        mList.add(map);
                    }
                    dynamicCallBack = new DynamicCallBack();
                    mDynamicAdapter.notifyDataSetChanged();
                    mListView.onRefreshComplete();
                    break;
                default:
                    break;
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dongtai, container, false);
        initViews();

        initData();

        setListeners();
        //下拉加载
        //addLoadListener();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
       /* Glide.with(this)
                .load(((MyApplication) getActivity().getApplication()).getUserInfo().getImageUrl())
                .transform(new GlideCircleTransform(getActivity()))
                .error(R.drawable.fail)
                .into(uImg);
        if (((MyApplication) getActivity().getApplication()).getUserInfo().getSex().equals("男")) {
            sexImg.setImageResource(R.drawable.ic_sex_man);
        } else {
            sexImg.setImageResource(R.drawable.ic_sex_woman);
        }*/
    }

    //初始化View
    private void initViews() {
        mActivity = (MainActivity) getActivity();
        mTopBar = (TopBar) mView.findViewById(R.id.dongtai_topbar);
        mListView = (PullToRefreshListView) mView.findViewById(R.id.dynamic_listView);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
    }

    //初始化数据
    private void initData() {
        mList = new ArrayList<>();
        myApplication = (MyApplication) getActivity().getApplication();
        getDynamicList(myApplication.getUserInfo().getUid(), String.valueOf(System.currentTimeMillis
                ()), "Long");

        //listView添加headView(添加headView和footView都要在添加适配器前进行)
        //HeaderView
        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.dynamic_header,
                mListView, false);
        ImageView imageView = (ImageView) mHeaderView.findViewById(R.id.dynamic_header_background);
        Glide.with(this).
                load(R.drawable.bg_dynamic).
                thumbnail(0.1f).
                into(imageView);
        uImg = (ImageView) mHeaderView.findViewById(R.id
                .dynamic_header_head_img);
        Glide.with(this)
                .load(((MyApplication) getActivity().getApplication()).getUserInfo().getImageUrl())
                .transform(new GlideCircleTransform(getActivity()))
                .into(uImg);
        sexImg = (ImageView) mHeaderView.findViewById(R.id.personSex);
        if (((MyApplication) getActivity().getApplication()).getUserInfo().getSex().equals("男")) {
            sexImg.setImageResource(R.drawable.ic_sex_man);
        } else {
            sexImg.setImageResource(R.drawable.ic_sex_woman);
        }
        TextView nameTextView = (TextView) mHeaderView.findViewById(R.id.dynamic_header_name);
        nameTextView.setText(((MyApplication) getActivity().getApplication()).getUserInfo()
                .getNickName());
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView
                .LayoutParams
                .MATCH_PARENT, AbsListView.LayoutParams
                .MATCH_PARENT);
        mHeaderView.setLayoutParams(layoutParams);
        mListView.getRefreshableView().addHeaderView(mHeaderView);

        //添加FootView
        /*mFootView = LayoutInflater.from(getActivity()).inflate(R.layout.dynamic_load_foot,
                mListView, false);
        mLinearLayout = (LinearLayout) mFootView.findViewById(R.id.dynamic_footer_LinearLayout);
        mLinearLayout.setVisibility(View.GONE);
        mListView.addFooterView(mFootView);*/

        mDynamicAdapter = new DynamicAdapter(getActivity(), mList);
        mListView.setAdapter(mDynamicAdapter);

    }

    private void getDynamicList(int id, String start, String timeType) {
        OkHttpUtils.post()
                .url(url)
                .addParams("appRequest", "GetDynamicLoad")
                .addParams("id", String.valueOf(id))
                .addParams("start", start)
                .addParams("timeType", timeType)
                .build()
                .execute(dynamicCallBack = new DynamicCallBack());
    }


    private void setListeners() {
        //顶部左右两边图片的点击事件
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
                //跳转至发布动态界面
                Intent intent = new Intent(getActivity(), PublishDynamicActivity.class);
                startActivity(intent);
            }
        });
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = format.format(System.currentTimeMillis());
                OkHttpUtils.post()
                        .url(url)
                        .addParams("appRequest", "GetDynamicLoad")
                        .addParams("id", String.valueOf(myApplication.getUserInfo().getUid()))
                        .addParams("start", time)
                        .addParams("timeType", "normal")
                        .build()
                        .execute(dynamicCallBack=new DynamicCallBack(2));
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String refreshTime = "";
                if (mList.size()==0) {
                    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd " +
                            "HH:mm:ss");
                    refreshTime = dataFormat.format(System.currentTimeMillis());
                } else if (mList.size()>0) {
                    DynamicImgBean bean = (DynamicImgBean) mList.get(mList.size()
                            - 1).get
                            ("DynamicBean");
                    refreshTime = bean.getTime();
                }
                getDynamicList(myApplication.getUserInfo().getUid(), refreshTime,
                        "normal");
            }
        });
    }
    /*//上拉刷新数据
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                //刷新相关逻辑操作
                if (mList.size() == 0) {
                    return;
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = format.format(new Date());
                OkHttpUtils.post()
                        .url(url)
                        .addParams("appRequest", "GetDynamicLoad")
                        .addParams("id", String.valueOf(myApplication.getUserInfo().getUid()))
                        .addParams("start", time)
                        .addParams("timeType", "normal")
                        .build()
                        .execute(dynamicCallBack = new DynamicCallBack(2));
            }
        }, 2000);
    }

    //监听加载
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
                            if (mList.size() == 0) {
                                mLinearLayout.setVisibility(View.GONE);
                                return;
                            }
                            //加载相关操作
                            DynamicImgBean bean = (DynamicImgBean) mList.get(mList.size() - 1)
                                    .get("DynamicBean");
                            getDynamicList(myApplication.getUserInfo().getUid(), bean.getTime(),
                                    "normal");
                            mLinearLayout.setVisibility(View.GONE);
                            //mDynamicAdapter.notifyDataSetChanged();
                            IS_LOADING = false;
                        }
                    }, 2000);
                }
            }
        });*/

    //自定义StringCallBack
    public class DynamicCallBack extends StringCallback {
        List<DynamicImgBean> imgBeanList = new ArrayList<DynamicImgBean>();
        int type = 1;

        public DynamicCallBack() {
        }

        public DynamicCallBack(int type) {
            this.type = type;
        }

        @Override
        public void onError(Call call, Exception e) {
            Log.e("Error",e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            imgBeanList = gson.fromJson(response, new TypeToken<List<DynamicImgBean>>
                    () {
            }.getType());
            Log.d("TAG",imgBeanList.size()+"");
            Log.e("Response",response);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = type;
                    mHandler.sendMessage(message);
                }
            }).start();
        }
    }
}