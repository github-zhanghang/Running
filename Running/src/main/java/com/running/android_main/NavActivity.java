package com.running.android_main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class NavActivity extends AppCompatActivity implements View.OnClickListener {
    private String mPath = "http://192.168.191.1:8080/JavaWeb-0406_homework/forwardServlet";
    //短信验证
    private final static String APP_KEY = "12a1127a423ef";
    private final static String APP_SECRET = "4d8275b912a9aceb7f39fdbf91b92da4";
    private EditText mNameEditText, mPasswordEditText;
    private Button mLoginButton, mRegisterButton;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        initViews();
        initListeners();
        x.Ext.init(getApplication());
    }

    private void initListeners() {
        mLoginButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
    }

    private void initViews() {
        mNameEditText = (EditText) findViewById(R.id.login_name);
        mPasswordEditText = (EditText) findViewById(R.id.login_password);
        mLoginButton = (Button) findViewById(R.id.login);
        mRegisterButton = (Button) findViewById(R.id.regist);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                //处理登录
                mProgressDialog = ProgressDialog.show(this, "请等待...", "正在为您登录...");
                handleLogin(mNameEditText.getText().toString(), mPasswordEditText.getText().toString());
                break;
            case R.id.regist:
                //处理注册
                handleRegister();
                break;
        }
    }

    private void handleLogin(String username, String password) {
        RequestParams params = new RequestParams(mPath);
        params.addQueryStringParameter("username", username);
        params.addQueryStringParameter("password", password);
        params.setConnectTimeout(5000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result.equals("success")) {
                    mProgressDialog.dismiss();
                    //登录成功跳转
                    Intent intent = new Intent(NavActivity.this, MainActivity.class);
                    intent.putExtra("username", mNameEditText.getText().toString());
                    startActivity(intent);
                    NavActivity.this.finish();
                } else {
                    Toast.makeText(NavActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(NavActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
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

    private void handleRegister() {
        //初始化短信验证
        SMSSDK.initSDK(this, APP_KEY, APP_SECRET);
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                // 解析注册结果
                if (result == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");
                    // 提交用户信息
                    //参数uid/nickname昵称/avatar头像/country国家/phone电话
                    SMSSDK.submitUserInfo(phone, "行长", null, country, phone);
                } else {
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        registerPage.show(NavActivity.this);
    }
}
