package com.running.android_main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.running.adapters.MedalAdapter;
import com.running.beans.MedalItemInfo;
import com.running.myviews.ImageTextView;
import com.running.myviews.MyGridView;
import com.running.myviews.TopBar;
import com.running.utils.ScreenShot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MedalActivity extends AppCompatActivity {
    private Activity mContext;
    private TopBar mTopBar;
    private MyGridView mHaveGridView;
    private List<ImageTextView> mHaveList = new ArrayList<>();
    private MedalAdapter mHaveAdapter;

    private MyGridView mNotHaveGridView;
    private List<ImageTextView> mNotHaveList = new ArrayList<>();
    private MedalAdapter mNotHaveAdapter;

    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medal);
        mContext = MedalActivity.this;
        initViews();
        initListener();

        initHaveGridViewData();
        mHaveAdapter = new MedalAdapter(mHaveList);
        mHaveGridView.setAdapter(mHaveAdapter);

        initNotHaveGridViewData();
        mNotHaveAdapter = new MedalAdapter(mNotHaveList);
        mNotHaveGridView.setAdapter(mNotHaveAdapter);
    }

    private void initViews() {
        mTopBar = (TopBar) findViewById(R.id.medal_topbar);
        mScrollView = (ScrollView) findViewById(R.id.scrollview);
        mHaveGridView = (MyGridView) findViewById(R.id.medal_have);
        mHaveGridView.setBackgroundColor(Color.WHITE);
        mNotHaveGridView = (MyGridView) findViewById(R.id.medal_nothave);
        mNotHaveGridView.setBackgroundColor(Color.WHITE);
    }

    private void initHaveGridViewData() {
        MedalItemInfo medalItemInfo;
        ImageTextView medalView;
        for (int i = 1; i <= 5; i++) {
            medalItemInfo = new MedalItemInfo(R.mipmap.ic_launcher, "第" + i + "个勋章");
            medalView = new ImageTextView(mContext);
            medalView.setImageResource(medalItemInfo.getImgId());
            medalView.setText(medalItemInfo.getDescription());
            mHaveList.add(medalView);
        }
    }

    private void initNotHaveGridViewData() {
        MedalItemInfo medalItemInfo;
        ImageTextView medalView;
        for (int i = 1; i <= 15; i++) {
            medalItemInfo = new MedalItemInfo(R.mipmap.ic_launcher, "第" + i + "个勋章");
            medalView = new ImageTextView(mContext);
            medalView.setImageResource(medalItemInfo.getImgId());
            medalView.setText(medalItemInfo.getDescription());
            mNotHaveList.add(medalView);
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
                        Toast.makeText(mContext, "分享失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        Toast.makeText(mContext, "分享取消", Toast.LENGTH_SHORT).show();
                    }
                });
                // 启动分享GUI
                oks.show(mContext);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK();
    }
}
