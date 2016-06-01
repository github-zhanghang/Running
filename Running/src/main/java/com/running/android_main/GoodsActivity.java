package com.running.android_main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GoodsActivity extends AppCompatActivity {

    WebView goodsWebView;
    WebSettings mSettings;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String web;
    ProgressDialog mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);

        initWebView();
        initRefresh();
    }

    private void initWebView() {
        mProgressBar= ProgressDialog.show(GoodsActivity.this,"客观别慌","正在拼命加载中...");
        goodsWebView = (WebView) findViewById(R.id.webview_goods);
        mSettings= goodsWebView.getSettings();
        mSettings.setUseWideViewPort(true);//设定支持viewport
        mSettings.setSupportZoom(true);//设定支持缩放
        mSettings.setBuiltInZoomControls(true);// 设置出现缩放工具
        mSettings.setDisplayZoomControls(false);
        mSettings.setJavaScriptEnabled(true);//支持javascript
        mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//自适应屏幕
        mSettings.setLoadWithOverviewMode(true);
        mSettings.setBlockNetworkImage(true);// 将图片下载阻塞
        mSettings.setDomStorageEnabled(true);//设置可以使用缓存
        mSettings.setAppCacheEnabled(true);

        goodsWebView.setHorizontalScrollBarEnabled(false);//水平不显示滚动条
        goodsWebView.setVerticalScrollBarEnabled(false); //垂直不显示
        //设置使用内嵌的浏览器
        goodsWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

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
            goodsWebView.loadUrl(web);
        }

        //取消图片的延迟载入
        goodsWebView.setWebViewClient(new WebViewClient(){
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
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_goods_detail);
        mSwipeRefreshLayout.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.RED, Color.YELLOW);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goodsWebView.clearCache(true);
                        goodsWebView.clearFormData();
                        goodsWebView.loadUrl(goodsWebView.getUrl());
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                },1500);
            }
        });
    }

    //返回上一个html
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK&& goodsWebView.canGoBack()){
            goodsWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
