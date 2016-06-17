package com.running.android_main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.running.beans.UserInfo;
import com.running.myviews.CustomProgressDialog;
import com.running.myviews.ImageTextView;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        RongIM.UserInfoProvider {
    private MyApplication mApplication;
    private String mPath = MyApplication.HOST + "loginServlet";
    public final String Login_OK = "0";
    public final String Login_Error_UserName = "1";
    public final String Login_Error_UserPassword = "2";
    //短信验证
    private final String APP_KEY = "12a1127a423ef";
    private final String APP_SECRET = "4d8275b912a9aceb7f39fdbf91b92da4";

    private Activity mContext;
    private EditText mNameEditText, mPasswordEditText;
    private TextView mForgetPasswordTextView;
    private Button mLoginButton, mRegisterButton;
    private CheckBox mRememberInfoCheckBox;
    //private ProgressDialog mProgressDialog;
    private CustomProgressDialog mProgressDialog;
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
    private Toast mToast;

    //第三方信息
    String userGender, userIcon, userName, token, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        mContext = LoginActivity.this;
        mApplication = (MyApplication) getApplication();
        //设置信息提供者
        RongIM.setUserInfoProvider(LoginActivity.this, true);
        initViews();
        //默认记住密码
        mRememberInfoCheckBox.setChecked(true);
        initListeners();
        initUserInfo();
        mProgressDialog = new CustomProgressDialog(this, "正在登录中", R.drawable.frame);
    }

    private void initViews() {
        mNameEditText = (EditText) findViewById(R.id.login_name);
        mPasswordEditText = (EditText) findViewById(R.id.login_password);
        mForgetPasswordTextView = (TextView) findViewById(R.id.forgetPassword);
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
        mForgetPasswordTextView.setOnClickListener(this);
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
                if (mAccount.equals("") || mPassword.equals("")) {
                    showToast("账号或密码不能为空");
                    return;
                }
                mProgressDialog.show();
                handleLogin(mAccount, mPassword);
                break;
            case R.id.regist:
                startActivity(new Intent(mContext, Register1Activity.class));
                LoginActivity.this.finish();
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
            case R.id.forgetPassword:
                SMSSDK.initSDK(this, APP_KEY, APP_SECRET);
                //打开注册页面
                RegisterPage registerPage = new RegisterPage();
                registerPage.setRegisterCallback(new EventHandler() {
                    public void afterEvent(int event, int result, Object data) {
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                Log.e("my", "验证成功");
                                HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                                String phone = (String) phoneMap.get("phone");
                                Intent inten = new Intent(LoginActivity.this,
                                        ForgetPasswordActivity.class);
                                inten.putExtra("phone", phone);
                                startActivity(inten);
                                LoginActivity.this.finish();
                            }
                        }
                    }
                });
                registerPage.show(LoginActivity.this);
                break;
        }
    }

    private OnResponseListener onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();// 响应结果
            Log.e("my", result);
            if (what == 0) {
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

                    //连接融云
                    connect(mApplication.getUserInfo().getRongToken());

                } else if (userInfo.getCode().equals(Login_Error_UserName)) {
                    mProgressDialog.dismiss();
                    showToast("该用户不存在");
                } else if (userInfo.getCode().equals(Login_Error_UserPassword)) {
                    mProgressDialog.dismiss();
                    showToast("密码错误");
                } else {
                    mProgressDialog.dismiss();
                    showToast("error");
                }
            } else if (what == 1) {
                //将用户信息保存在Application中
                UserInfo userInfo = new Gson().fromJson(result, UserInfo.class);
                if (userInfo.getCode().equals(Login_OK)) {
                    //表示已存在此Token
                    mApplication.setUserInfo(userInfo);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    //表示不存在此Token
                    Intent intent = new Intent(LoginActivity.this, Register3Activity.class);
                    intent.putExtra("userGender", userGender);
                    intent.putExtra("userIcon", userIcon);
                    intent.putExtra("userName", userName);
                    intent.putExtra("address", address);
                    intent.putExtra("qqtoken", token);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int
                responseCode, long networkMillis) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            showToast("登录失败,请稍后重试:" + exception.getMessage() + ";responseCode=" + responseCode);
        }

        @Override
        public void onFinish(int what) {
        }
    };

    private void handleLogin(final String username, final String password) {
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        if (username.length() == 9) {
            //账号登录
            if (username.substring(0, 3).equals("run")) {
                request.add("type", "account");
            } else {
                mProgressDialog.dismiss();
                showToast("账号错误，请以run开头");
            }
        } else if (username.length() == 11) {
            //手机号登录
            if (isMobileNO(username)) {
                request.add("type", "phone");
            } else {
                mProgressDialog.dismiss();
                showToast("手机号错误，请确认");
            }
        } else {
            mProgressDialog.dismiss();
            showToast("账号错误，请确认");
            return;
        }
        request.add("account", username);
        request.add("password", password);
        requestQueue.add(0, request, onResponseListener);
        requestQueue.start();
    }

    private void thirdLogin(String platformName) {
        ShareSDK.initSDK(LoginActivity.this);
        mPlatform = ShareSDK.getPlatform(mContext, platformName);
        //如果已经验证,取消验证信息，重新验证
        if (mPlatform.isValid()) {
            mPlatform.removeAccount(true);
        }
        mPlatform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.e("my", "hashMap=" + hashMap);
                if (platform.getName().equals(QZone.NAME)) {
                    //获取性别
                    userGender = (String) hashMap.get("gender");
                    //获取头像
                    userIcon = (String) hashMap.get("figureurl_qq_2");
                    //获取昵称
                    userName = (String) hashMap.get("nickname");
                    //获取省市
                    address = hashMap.get("province") + " " + hashMap.get("city");
                    //获取数平台数据DB
                    PlatformDb platDB = platform.getDb();
                    //获取token
                    token = platDB.getToken();
                    Log.e("my", "token=" + token);

                    //判断数据库中是否有此Token
                    isExistThisToken(token, "qq");
                } else if (platform.getName().equals(SinaWeibo.NAME)) {
                    //获取性别
                    userGender = (String) hashMap.get("gender");
                    if (userGender.equals("m")) {
                        userGender = "男";
                    } else {
                        userGender = "女";
                    }
                    //获取头像
                    userIcon = (String) hashMap.get("avatar_hd");
                    //获取昵称
                    userName = (String) hashMap.get("name");
                    //获取省市
                    address = (String) hashMap.get("location");
                    //获取数平台数据DB
                    PlatformDb platDB = platform.getDb();
                    //获取token
                    token = platDB.getToken();
                    Log.e("my", "token=" + token);

                    //判断数据库中是否有此Token
                    isExistThisToken(token, "weibo");
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("my", throwable.getMessage());
                platform.removeAccount();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e("my", "已取消");
            }
        });
        //获取用户资料
        mPlatform.showUser(null);
    }

    //是否存在此token
    public void isExistThisToken(String token, String platformName) {
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "third");
        request.add("platform", platformName);
        request.add("token", token);
        requestQueue.add(1, request, onResponseListener);
        requestQueue.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK();
    }

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
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                LoginActivity.this.finish();
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

    public void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    //消息提供者
    @Override
    public io.rong.imlib.model.UserInfo getUserInfo(String s) {

        io.rong.imlib.model.UserInfo userInfo = new io.rong.imlib.model.UserInfo(
                mApplication.getUserInfo().getAccount(),
                mApplication.getUserInfo().getNickName(),
                Uri.parse(mApplication.getUserInfo().getImageUrl()));

        Log.e("test123", "getUserInfo: getImageUrl " + mApplication.getUserInfo().getImageUrl());
        Log.e("test123", "getUserInfo: getPortraitUri " + userInfo.getPortraitUri());

        /*RongIM.getInstance().setCurrentUserInfo(userInfo);
        RongIM.getInstance().setMessageAttachedUserInfo(true);*/
        return userInfo;

    }

    /**
     * 验证手机格式
     */
    public boolean isMobileNO(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、152、155、156、185、186
        电信：133、153、180、189、（1349卫通）
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        */
        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，
        // "\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][358]\\d{9}";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }
}
