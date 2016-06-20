package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.running.adapters.DynamicOneselfAdapter;
import com.running.beans.DynamicOneselfBean;
import com.running.beans.UserInfo;
import com.running.myviews.TopBar;
import com.running.utils.GlideCircleTransform;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class DynamicOneselfActivity extends AppCompatActivity {

    public static final String IMG = "img";
    private List<HashMap<String, Object>> mList;
    private DynamicOneselfAdapter mAdapter;

    @Bind(R.id.dynamic_oneself_topBar)
    TopBar mDynamicOneselfTopBar;
    @Bind(R.id.dynamic_oneself_list_view)
    PullToRefreshListView mDynamicOneselfListView;
    View mHeaderView;
    ImageView uImg;
    ImageView sexImg;
    UserInfo mUserInfo;
    int uId;
    private DynamicOneselfCallBack mOneselfCallBack;
    String url = MyApplication.HOST + "dynamicOperateServlet";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initHeadView();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = format.format(new Date());
                    Log.e("LDD123", "Test" + time);
                    getLoadData(time);
                    break;
                case 1:
                    for (int i = 0; i < mOneselfCallBack.mOneselfBeen.size(); i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("type", IMG);
                        map.put("DynamicOneselfBean", mOneselfCallBack.mOneselfBeen.get(i));
                        mList.add(map);
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    for (int i = 0; i < mOneselfCallBack.mOneselfBeen.size(); i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("type", IMG);
                        map.put("DynamicOneselfBean", mOneselfCallBack.mOneselfBeen.get(i));
                        mList.add(map);
                    }
                    mAdapter.notifyDataSetChanged();
                    Log.e("LDD",mList.size()+"");
                    mDynamicOneselfListView.onRefreshComplete();
                    break;
                case 3:
                    mList.clear();
                    for (int i = 0; i < mOneselfCallBack.mOneselfBeen.size(); i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("type", IMG);
                        map.put("DynamicOneselfBean", mOneselfCallBack.mOneselfBeen.get(i));
                        mList.add(map);
                    }
                    mAdapter.notifyDataSetChanged();
                    Log.e("LDD",mList.size()+"");
                    mDynamicOneselfListView.onRefreshComplete();
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
        mDynamicOneselfListView.setMode(PullToRefreshBase.Mode.BOTH);
        initData();
        Log.e("LDD123", "meid" + ((MyApplication) getApplication()).getUserInfo().getUid());

        mAdapter = new DynamicOneselfAdapter(this, mList);
        mDynamicOneselfListView.setAdapter(mAdapter);
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

        mDynamicOneselfListView.setOnRefreshListener(new PullToRefreshBase
                .OnRefreshListener2<ListView>() {


            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getRefreshData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String refreshTime = "";
                if (mList.size()==0) {
                    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd " +
                            "HH:mm:ss");
                    refreshTime = dataFormat.format(System.currentTimeMillis());
                } else if (mList.size()>0) {
                    DynamicOneselfBean oneselfBean = (DynamicOneselfBean) mList.get(mList.size()
                            - 1).get
                            ("DynamicOneselfBean");
                    refreshTime = oneselfBean.getTime();
                }
                getLoadData(refreshTime);
            }
        });
    }

    private void getLoadData(String time) {
        Log.e("LDD123", "test" + "getLoadData");
        Log.e("LDD123", "meid" + ((MyApplication) getApplication()).getUserInfo().getUid());
        OkHttpUtils.post()
                .url(url)
                .addParams("appRequest", "DynamicOneselfLoad")
                .addParams("uId", String.valueOf(uId))
                .addParams("meId", ((MyApplication) getApplication()).getUserInfo().getUid() + "")
                .addParams("time", time)
                .build()
                .execute(mOneselfCallBack = new DynamicOneselfCallBack(2));
    }

    private void initData() {
        mList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        Intent intent = getIntent();
        uId = intent.getIntExtra("uId", -1);
        Log.d("TAG", "" + uId);
        if (uId == -1) {
        } else {
            OkHttpUtils.post()
                    .url(url)
                    .addParams("appRequest", "GetUserInfo")
                    .addParams("uId", String.valueOf(uId))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e) {

                        }

                        @Override
                        public void onResponse(String response) {
                            Log.e("LDD123", "header" + response);
                            Gson gson = new Gson();
                            mUserInfo = gson.fromJson(response, UserInfo.class);
                            mDynamicOneselfTopBar.setLeftText(mUserInfo.getNickName());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = 0;
                                    mHandler.sendMessage(message);
                                }
                            }).start();
                        }
                    });

        }
        //initHeadView();
    }

    private void initHeadView() {
        mHeaderView = LayoutInflater.from(this).inflate(R.layout.dynamic_header,
                mDynamicOneselfListView, false);
        ImageView imageView = (ImageView) mHeaderView.findViewById(R.id.dynamic_header_background);
        Glide.with(this).
                load(R.drawable.bg_dynamic).
                thumbnail(0.1f).
                into(imageView);
        uImg = (ImageView) mHeaderView.findViewById(R.id
                .dynamic_header_head_img);
        Glide.with(this)
                .load(mUserInfo.getImageUrl())
                .transform(new GlideCircleTransform(this))
                .into(uImg);
        uImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DynamicOneselfActivity.this,PersonInformationActivity.class);
                intent.putExtra("UserInfo",mUserInfo);
                startActivity(intent);
            }
        });
        sexImg = (ImageView) mHeaderView.findViewById(R.id.personSex);
        if (mUserInfo.getSex().equals("男")) {
            sexImg.setImageResource(R.drawable.ic_sex_man);
        } else {
            sexImg.setImageResource(R.drawable.ic_sex_woman);
        }
        TextView nameTextView = (TextView) mHeaderView.findViewById(R.id.dynamic_header_name);
        nameTextView.setText(mUserInfo.getNickName());
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams
                .MATCH_PARENT,AbsListView.LayoutParams
                .MATCH_PARENT);
        mHeaderView.setLayoutParams(layoutParams);
        mDynamicOneselfListView.getRefreshableView().addHeaderView(mHeaderView);
    }

    public void getRefreshData() {
        Log.e("LDD123", "test" + "getRefreshData");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd " +
                "HH:mm:ss");
        String time = format.format(System.currentTimeMillis());
        OkHttpUtils.post()
                .url(url)
                .addParams("appRequest", "DynamicOneselfLoad")
                .addParams("uId", String.valueOf(uId))
                .addParams("meId", String.valueOf(((MyApplication) getApplication())
                        .getUserInfo().getUid()))
                .addParams("time", time)
                .build()
                .execute(mOneselfCallBack = new DynamicOneselfCallBack(3));
    }

    public class DynamicOneselfCallBack extends StringCallback {
        List<DynamicOneselfBean> mOneselfBeen = new ArrayList<>();
        int type = 1;

        public DynamicOneselfCallBack() {
        }

        public DynamicOneselfCallBack(int type) {
            this.type = type;
        }

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String response) {
            Log.e("LDD123", "test" + response);
            Gson gson = new Gson();
            mOneselfBeen = gson.fromJson(response, new TypeToken<List<DynamicOneselfBean>>() {
            }.getType());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==1) {
            int uid = data.getIntExtra("uid",-1);
            Log.e("LDD",uid+"");
            if (uid==-1) {
            } else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd " +
                        "HH:mm:ss");
                String time = format.format(System.currentTimeMillis()+1000);
                OkHttpUtils.post()
                        .url(url)
                        .addParams("appRequest", "DynamicOneselfLoad")
                        .addParams("uId", String.valueOf(uid))
                        .addParams("meId", String.valueOf(((MyApplication) getApplication())
                                .getUserInfo().getUid()))
                        .addParams("time", time)
                        .build()
                        .execute(mOneselfCallBack = new DynamicOneselfCallBack(3));
            }
        }
    }
}
