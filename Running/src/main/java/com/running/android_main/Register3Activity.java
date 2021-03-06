package com.running.android_main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.running.beans.UserInfo;
import com.running.myviews.ScaleRulerView;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class Register3Activity extends AppCompatActivity {
    private static final String mPath = MyApplication.HOST + "registerServlet";
    private MyApplication mApplication;

    private ScaleRulerView mHeightWheelView;
    private TextView mHeightValue;
    private ScaleRulerView mWeightWheelView;
    private TextView mWeightValue;

    private int mHeight = 170;
    private int mMaxHeight = 240;
    private int mMinHeight = 100;

    private int mWeight = 65;
    private int mMaxWeight = 250;
    private int mMinWeight = 25;

    private String mPassword, mTelephone, mSex, mUserIcon, mQQToken, mWeChatToken, mSinaToken,
            mAddress, mNickName;
    private ProgressDialog mProgressDialog;

    private String mAcount;
    //请求队列
    private RequestQueue requestQueue;
    private static final int WHAT_ONE = 1;
    private static final int WHAT_TWO = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);
        mApplication = (MyApplication) getApplication();
        mApplication.addActivity(this);
        Intent intent = getIntent();
        mPassword = intent.getStringExtra("password");
        mTelephone = intent.getStringExtra("telephone");
        mSex = intent.getStringExtra("userGender");
        if (mSex == null) {
            mSex = intent.getStringExtra("sex");
        }
        mUserIcon = intent.getStringExtra("userIcon");
        if (mUserIcon == null) {
            mUserIcon = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1564533037,3918553373&fm=116&gp=0.jpg";
        }
        mAddress = intent.getStringExtra("address");
        if (mAddress == null) {
            mAddress = "";
        }
        mNickName = intent.getStringExtra("userName");
        mQQToken = intent.getStringExtra("qqtoken");
        if (mQQToken == null) {
            mQQToken = "";
        }
        mWeChatToken = intent.getStringExtra("wxtoken");
        if (mWeChatToken == null) {
            mWeChatToken = "";
        }
        mSinaToken = intent.getStringExtra("sinatoken");
        if (mSinaToken == null) {
            mSinaToken = "";
        }
        initView();
        addListener();
        // 创建请求队列, 默认并发3个请求, 传入数字改变并发数量: NoHttp.newRequestQueue(1);
        requestQueue = NoHttp.newRequestQueue();
    }

    private void addListener() {
        mHeightWheelView.initViewParam(mHeight, mMaxHeight, mMinHeight);
        mHeightWheelView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mHeightValue.setText((int) value + "");
                mHeight = (int) value;
            }
        });

        mWeightWheelView.initViewParam(mWeight, mMaxWeight, mMinWeight);
        mWeightWheelView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mWeightValue.setText(value + "");
                mWeight = (int) value;
            }
        });
    }

    private void initView() {
        mHeightWheelView = (ScaleRulerView) findViewById(R.id.scaleWheelView_height);
        mHeightValue = (TextView) findViewById(R.id.tv_user_height_value);
        mWeightWheelView = (ScaleRulerView) findViewById(R.id.scaleWheelView_weight);
        mWeightValue = (TextView) findViewById(R.id.tv_user_weight_value);
    }

    public void submitMesssage(View view) {
        mProgressDialog = ProgressDialog.show(this, "请等待...", "正在提交注册...");
        // 请求对象
        //获取最大的账号，自动生成账号
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "maxacount");
        requestQueue.add(WHAT_ONE, request, onResponseListener);
        requestQueue.start();
    }

    private void submitUserInfo() {
        //提交用户信息，获取返回结果
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "register");
        request.add("account", mAcount);
        request.add("telephone", mTelephone);
        request.add("password", mPassword);
        request.add("sex", mSex);
        request.add("height", "" + mHeight);
        request.add("weight", "" + mWeight);
        request.add("address", "" + mAddress);
        request.add("nickname", "" + mNickName);
        request.add("icon", mUserIcon);
        request.add("qqtoken", mQQToken);
        request.add("wechattoken", mWeChatToken);
        request.add("sinatoken", mSinaToken);
        requestQueue.add(WHAT_TWO, request, onResponseListener);
        requestQueue.start();
    }

    /**
     * 回调对象，接受请求结果
     */
    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            if (what == WHAT_ONE) {
                // 请求成功
                String result = response.get();// 响应结果
                //获取最大的账号
                // Log.e("my", result);
                if (result == null || result.equals("")) {
                    mProgressDialog.dismiss();
                    Toast.makeText(Register3Activity.this, "提交注册信息失败，请稍后重试", Toast.LENGTH_LONG).show();
                    return;
                }
                //生成账号
                String countStr = result.substring(3, result.length());
                int countNo = Integer.parseInt(countStr);
                mAcount = "run" + (countNo + 1);
                if (mNickName == null) {
                    mNickName = mAcount;
                }
                //提交注册信息
                submitUserInfo();
            } else if (what == WHAT_TWO) {
                mProgressDialog.dismiss();
                String result = response.get();// 响应结果
                //注册成功返回的用户信息
                Log.e("my", result);
                //将用户信息保存在Application中
                UserInfo userInfo = new Gson().fromJson(result, UserInfo.class);
                mApplication.setUserInfo(userInfo);
                //跳转

               /* startActivity(new Intent(Register3Activity.this, MainActivity.class));
                Register3Activity.this.finish();*/
                //连接融云
                connect(mApplication.getUserInfo().getRongToken());
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            mProgressDialog.dismiss();
            Toast.makeText(Register3Activity.this, "提交信息失败,请稍后重试", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish(int what) {
        }
    };


    //融云的连接
    private void connect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                Log.e("12345: ", "过期");
            }

            @Override
            public void onSuccess(String s) {
                Log.e("12345: ", "融云连接成功");
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if (RongIM.getInstance() != null) {
                    io.rong.imlib.model.UserInfo userInfo = new io.rong.imlib.model.UserInfo(
                            mApplication.getUserInfo().getAccount(),
                            mApplication.getUserInfo().getNickName(),
                            Uri.parse(mApplication.getUserInfo().getImageUrl()));
                    Log.e("12345: ", "融云连接成功" + userInfo.getPortraitUri());
                    RongIM.getInstance().setCurrentUserInfo(userInfo);
                  /*  RongContext.getInstance().getUserInfoCache().
                            put(mApplication.getUserInfo().getAccount(),userInfo);*/
                }
                RongIM.getInstance().setMessageAttachedUserInfo(true);
                startActivity(new Intent(Register3Activity.this, MainActivity.class));
                Register3Activity.this.finish();
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                Log.e("12345: ", errorCode.toString());
            }
        });
    }
}
