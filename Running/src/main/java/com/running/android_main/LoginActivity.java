package com.running.android_main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.running.myviews.ImageTextView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Activity mContext;
    private String mPath = "http://192.168.191.1:8080/JavaWeb-0406_homework/forwardServlet";
    private EditText mNameEditText, mPasswordEditText;
    private Button mLoginButton, mRegisterButton;
    private ProgressDialog mProgressDialog;
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
        initListeners();
    }

    private void initViews() {
        mNameEditText = (EditText) findViewById(R.id.login_name);
        mPasswordEditText = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login);
        mRegisterButton = (Button) findViewById(R.id.regist);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                //处理登录
                String uName = mNameEditText.getText().toString();
                String uPwd = mPasswordEditText.getText().toString();
                if (uName.equals("") && uPwd.equals("")) {
                    //仅用于测试时使用
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

    private void handleLogin(String username, String password) {
        RequestParams params = new RequestParams(mPath);
        params.addQueryStringParameter("username", username);
        params.addQueryStringParameter("password", password);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result.equals("success")) {
                    mProgressDialog.dismiss();
                    //登录成功跳转
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("username", mNameEditText.getText().toString());
                    startActivity(intent);
                    mContext.finish();
                } else {
                    Toast.makeText(mContext, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK();
    }
}
