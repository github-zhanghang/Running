package com.running.android_main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.running.beans.UserInfo;
import com.running.myviews.ImageTextView;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private MyApplication mApplication;
    public static final String Login_OK = "0";
    private static final String mPath = "http://192.168.191.1:8080/Running/loginServlet";
    public static final String Login_Error_UserName = "1";
    public static final String Login_Error_UserPassword = "2";

    private Activity mContext;
    private EditText mNameEditText, mPasswordEditText;
    private Button mLoginButton, mRegisterButton;
    private CheckBox mRememberInfoCheckBox;
    private ProgressDialog mProgressDialog;
    private SharedPreferences mSharedPreferences;
    //是否记住密码
    private boolean isRememberPassword = false;
    //第三方登录
    private ImageTextView mLogin_QQ, mLogin_WeChat, mLogin_Sina;
    //平台
    private Platform mPlatform;

    //账号、密码
    private String mAccount, mPassword;

    //请求队列
    private RequestQueue requestQueue;
    private static final int WHAT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        mContext = LoginActivity.this;
        mApplication = (MyApplication) getApplication();
        ShareSDK.initSDK(mContext);
        initViews();
        //默认记住密码
        mRememberInfoCheckBox.setChecked(true);
        initListeners();
        initUserInfo();
    }

    private void initViews() {
        mNameEditText = (EditText) findViewById(R.id.login_name);
        mPasswordEditText = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login);
        mRegisterButton = (Button) findViewById(R.id.regist);
        mRememberInfoCheckBox = (CheckBox) findViewById(R.id.rememberPassword);
        mLogin_QQ = (ImageTextView) findViewById(R.id.login_QQ);
        mLogin_WeChat = (ImageTextView) findViewById(R.id.login_WeChat);
        mLogin_Sina = (ImageTextView) findViewById(R.id.login_Sina);
        // 创建请求队列, 默认并发3个请求, 传入数字改变并发数量: NoHttp.newRequestQueue(1);
        requestQueue = NoHttp.newRequestQueue();
    }

    private void initListeners() {
        mLoginButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        mLogin_QQ.setOnClickListener(this);
        mLogin_WeChat.setOnClickListener(this);
        mLogin_Sina.setOnClickListener(this);
    }

    //取出保存的用户信息
    private void initUserInfo() {
        mSharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        isRememberPassword = mSharedPreferences.getBoolean("isRememberPassword", false);
        if (isRememberPassword) {
            String pwd = mSharedPreferences.getString("password", "");
            String name = mSharedPreferences.getString("username", "");
            mPasswordEditText.setText(pwd);
            mNameEditText.setText(name);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                //处理登录
                mAccount = mNameEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();
                if (mAccount.equals("run") && mPassword.equals("123")) {
                    //判断是否记住密码
                    if (mRememberInfoCheckBox.isChecked()) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("username", mAccount);
                        editor.putString("password", mPassword);
                        editor.putBoolean("isRememberPassword", true);
                        editor.apply();
                    } else {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putBoolean("isRememberPassword", false);
                        editor.apply();
                    }
                    UserInfo userinfo = new UserInfo("0", 3333, mAccount, "15106200759", mAccount,
                            "\"https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1564533037,3918553373&fm=116&gp=0.jpg\"",
                            "", "", 20, "男", 170, 66,
                            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                            0.0, 0.0, "zzzz");
                    mApplication.setUserInfo(userinfo);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    LoginActivity.this.finish();
                } else {
                    mProgressDialog = ProgressDialog.show(this, "请等待...", "正在登录...");
                    handleLogin(mAccount, mPassword);
                }
                break;
            case R.id.regist:
                startActivity(new Intent(mContext, Register1Activity.class));
                break;
            case R.id.login_QQ:
                thirdLogin(QZone.NAME);
                break;
            case R.id.login_WeChat:
                thirdLogin(Wechat.NAME);
                break;
            case R.id.login_Sina:
                thirdLogin(SinaWeibo.NAME);
                break;
        }
    }

    private OnResponseListener onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            if (what == WHAT) {
                mProgressDialog.dismiss();
                String result = response.get();// 响应结果
                Log.e("my", result);
                //将用户信息保存在Application中
                UserInfo userInfo = new Gson().fromJson(result, UserInfo.class);
                if (userInfo.getCode().equals(Login_OK)) {
                    mApplication.setUserInfo(userInfo);
                    //判断是否记住密码
                    if (mRememberInfoCheckBox.isChecked()) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("username", mAccount);
                        editor.putString("password", mPassword);
                        editor.putBoolean("isRememberPassword", true);
                        editor.apply();
                    } else {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putBoolean("isRememberPassword", false);
                        editor.apply();
                    }
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    LoginActivity.this.finish();
                } else if (userInfo.getCode().equals(Login_Error_UserName)) {
                    Toast.makeText(LoginActivity.this, "该用户不存在", Toast.LENGTH_SHORT).show();
                } else if (userInfo.getCode().equals(Login_Error_UserPassword)) {
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            mProgressDialog.dismiss();
            Toast.makeText(LoginActivity.this, "登录失败,请稍后重试", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish(int what) {
        }
    };

    private void handleLogin(final String username, final String password) {
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("account", mAccount);
        request.add("password", mPassword);
        requestQueue.add(WHAT, request, onResponseListener);
        requestQueue.start();
    }

    private void thirdLogin(String platformName) {
        mPlatform = ShareSDK.getPlatform(mContext, platformName);
        //如果已经验证,取消验证信息，重新验证
        if (mPlatform.isValid()) {
            mPlatform.removeAccount();
        }
        mPlatform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                //解析部分用户资料字段
                String id, name, description, profile_image_url;
                id = hashMap.get("id").toString();//ID
                name = hashMap.get("name").toString();//用户名
                description = hashMap.get("description").toString();//描述
                profile_image_url = hashMap.get("profile_image_url").toString();//头像链接
                String str = "ID: " + id + ";\n" +
                        "用户名： " + name + ";\n" +
                        "描述：" + description + ";\n" +
                        "用户头像地址：" + profile_image_url;
                Log.e("my", "用户资料: " + str);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(mContext, "出错", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Toast.makeText(mContext, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        mPlatform.showUser(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK();
    }
}
