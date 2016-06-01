package com.running.android_main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RaceActivity extends AppCompatActivity {

    WebView raceWebView;
    WebSettings mSettings;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String web;
    ProgressDialog mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        initWebView();
        initRefresh();
    }

    private void initWebView() {
        mProgressBar= ProgressDialog.show(RaceActivity.this,"客观别慌","正在拼命加载中...");
        raceWebView= (WebView) findViewById(R.id.webview_race);
        mSettings=raceWebView.getSettings();
        mSettings.setUseWideViewPort(true);//设定支持viewport
        mSettings.setSupportZoom(true);//设定支持缩放
        mSettings.setBuiltInZoomControls(true);// 设置出现缩放工具
        mSettings.setDisplayZoomControls(false);
        mSettings.setJavaScriptEnabled(true);//支持javascript
        mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//自适应屏幕
        mSettings.setLoadWithOverviewMode(true);
        mSettings.setBlockNetworkImage(true);// 将图片下载阻塞
        raceWebView.setHorizontalScrollBarEnabled(false);//水平不显示滚动条
        raceWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        //设置使用内嵌的浏览器
        raceWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        // 接收到网址
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null){
            web=bundle.getString("weburl");
            raceWebView.loadUrl(web);
        }
        //取消图片的延迟载入
        raceWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mSettings.setBlockNetworkImage(false);
                if (mProgressBar.isShowing()){
                    mProgressBar.dismiss();
                }
            }
        });

    }

    private void initRefresh() {
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_race_detail);
        mSwipeRefreshLayout.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED, Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        raceWebView.clearCache(true);
                        raceWebView.clearFormData();
                        raceWebView.loadUrl(raceWebView.getUrl());
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },1500);
            }
        });
    }

    //返回上一个html
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK&&raceWebView.canGoBack()){
            raceWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
