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
import com.running.utils.Medal;
import com.running.utils.ScreenShot;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MedalActivity extends AppCompatActivity {
    private String mPath = MyApplication.HOST + "medalServlet";
    private Activity mContext;
    private TopBar mTopBar;
    private GridView mGridView;
    private List<ImageTextView> mList = new ArrayList<>();
    private MedalAdapter mAdapter;
    private Toast mToast;
    RequestQueue requestQueue = NoHttp.newRequestQueue(1);

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
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("uid", ((MyApplication) getApplication()).getUserInfo().getUid());
        request.add("type", "query");
        requestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                if (what == 1) {
                    String result = response.get();
                    Log.e("my", "medal_result=" + result);
                    if (result != null && !result.equals("null")) {
                        String mid[] = result.split(",");
                        for (int i = 0; i < mid.length; i++) {
                            int id = Integer.parseInt(mid[i]);
                            ImageTextView imageTextView = mList.get(id - 1);
                            imageTextView.setText(Medal.mHaveString[id - 1]);
                            imageTextView.setTextColor(Color.parseColor("#FFD350"));
                            imageTextView.setImageResource(Medal.mHaveImage[id - 1]);
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
                showToast(Medal.mDescription[position]);
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
        for (int i = 0; i < Medal.mNotHaveImage.length; i++) {
            medalItemInfo = new MedalItemInfo(Medal.mNotHaveImage[i], Medal.mNotHaveString[i]);
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
