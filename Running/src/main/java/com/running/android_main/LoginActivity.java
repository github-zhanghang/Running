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

import com.running.myviews.ImageTextView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String Login_OK = "0";
    private static final String mPath = "http://192.168.191.1:8080/Run/loginServlet";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        mContext = LoginActivity.this;
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
                String uName = mNameEditText.getText().toString();
                String uPwd = mPasswordEditText.getText().toString();
                if (uName.equals("") && uPwd.equals("")) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    LoginActivity.this.finish();
                } else {
                    mProgressDialog = ProgressDialog.show(this, "请等待...", "正在登录...");
                    handleLogin(uName, uPwd);
                }
                break;
            case R.id.regist:
                //跳转注册页面
                startActivity(new Intent(mContext, RegisterActivity.class));
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

    private void handleLogin(final String username, final String password) {
        OkHttpUtils
                .get()
                .url(mPath)
                .addParams("username", username)
                .addParams("password", password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(mContext, "登录失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("my", response);
                        if (response.equals(Login_OK)) {
                            mProgressDialog.dismiss();
                            //判断是否记住密码
                            if (mRememberInfoCheckBox.isChecked()) {
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putString("username", username);
                                editor.putString("password", password);
                                editor.putBoolean("isRememberPassword", true);
                                editor.apply();
                            } else {
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putBoolean("isRememberPassword", false);
                                editor.apply();
                            }
                            //登录成功跳转
                            startActivity(new Intent(mContext, MainActivity.class));
                            mContext.finish();
                        } else if (response.equals(Login_Error_UserName)) {
                            mProgressDialog.dismiss();
                            Toast.makeText(mContext, "该用户不存在", Toast.LENGTH_SHORT).show();
                        } else if (response.equals(Login_Error_UserPassword)) {
                            mProgressDialog.dismiss();
                            Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(mContext, "登录失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
