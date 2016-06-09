package com.running.android_main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.running.adapters.MedalAdapter;
import com.running.beans.MedalItemInfo;
import com.running.myviews.ImageTextView;
import com.running.myviews.TopBar;
import com.running.utils.ScreenShot;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MedalActivity extends AppCompatActivity {
    private String mPath = "http://192.168.191.1:8080/Running/medalServlet";
    private Activity mContext;
    private TopBar mTopBar;
    private GridView mGridView;
    private List<ImageTextView> mList = new ArrayList<>();
    private MedalAdapter mAdapter;
    private Toast mToast;

    private int[] mHaveImage = {R.drawable.ic_medal_m_day1, R.drawable.ic_medal_m_run5,
            R.drawable.ic_medal_m_run10, R.drawable.ic_medal_m_run21,
            R.drawable.ic_medal_m_run42, R.drawable.ic_medal_m_run100};
    private String[] mHaveString = {"开始运动\n(已获得)", "5公里\n(已获得)", "10公里\n(已获得)",
            "半程马拉松\n(已获得)", "全称马拉松\n(已获得)", "超级马拉松\n(已获得)"};

    private int[] mNotHaveImage = {R.drawable.ic_medal_m_day1_p, R.drawable.ic_medal_m_run5_p,
            R.drawable.ic_medal_m_run10_p, R.drawable.ic_medal_m_run21_p,
            R.drawable.ic_medal_m_run42_p, R.drawable.ic_medal_m_run100_p};
    private String[] mNotHaveString = {"开始运动\n(未获得)", "5公里\n(未获得)", "10公里\n(未获得)",
            "半程马拉松\n(未获得)", "全称马拉松\n(未获得)", "超级马拉松\n(未获得)"};

    private String[] mDescription = {"开始一次跑步", "单次跑步距离达到5公里",
            "单次跑步距离达到10公里", "单次跑步距离达到21.1公里",
            "单次跑步距离达到42.2公里", "单次跑步距离达到100公里"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medal);
        mContext = MedalActivity.this;
        initViews();
        initListener();
        initHaveGridViewData();
        mAdapter = new MedalAdapter(mList);
        mGridView.setAdapter(mAdapter);
        getMyMedal();
    }

    private void getMyMedal() {
        RequestQueue requestQueue = NoHttp.newRequestQueue(1);
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("uid", ((MyApplication) getApplication()).getUserInfo().getUid());
        requestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                if (what == 1) {
                    String result = response.get();
                    Log.e("my", "medal_result=" + result);
                    if (!result.equals("") || result != null) {
                        String mid[] = result.split(",");
                        for (int i = 0; i < mid.length; i++) {
                            int id = Integer.parseInt(mid[i]);
                            ImageTextView imageTextView = mList.get(id - 1);
                            imageTextView.setText(mHaveString[id - 1]);
                            imageTextView.setTextColor(Color.parseColor("#FFD350"));
                            imageTextView.setImageResource(mHaveImage[id - 1]);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                showToast("获取数据失败");
            }

            @Override
            public void onFinish(int what) {
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showToast(mDescription[position]);
            }
        });
    }

    private void initViews() {
        mTopBar = (TopBar) findViewById(R.id.medal_topbar);
        mGridView = (GridView) findViewById(R.id.medal_gridview);
    }

    private void initHaveGridViewData() {
        MedalItemInfo medalItemInfo;
        ImageTextView medalView;
        for (int i = 0; i < mNotHaveImage.length; i++) {
            medalItemInfo = new MedalItemInfo(mNotHaveImage[i], mNotHaveString[i]);
            medalView = new ImageTextView(mContext);
            medalView.setImageResource(medalItemInfo.getImgId());
            medalView.setText(medalItemInfo.getDescription());
            medalView.setBackgroundColor(Color.TRANSPARENT);
            mList.add(medalView);
        }
    }

    private void initListener() {
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                mContext.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
                ShareSDK.initSDK(mContext);
                OnekeyShare oks = new OnekeyShare();
                oks.disableSSOWhenAuthorize();
                oks.setTitle("我的勋章");
                oks.setText("看看我的勋章");
                Bitmap bitmap = ScreenShot.takeScreenShot(mContext);
                String path = ScreenShot.saveBitmap(bitmap, mContext);
                oks.setImagePath(path);
                oks.setUrl("http://www.baidu.com");
                oks.setSite(getString(R.string.app_name));
                oks.setSiteUrl("http://www.baidu.com");
                oks.setCallback(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        mContext.finish();
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        showToast("分享失败");
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        showToast("分享取消");
                    }
                });
                // 启动分享GUI
                oks.show(mContext);
            }
        });
    }

    public void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK();
    }
}
