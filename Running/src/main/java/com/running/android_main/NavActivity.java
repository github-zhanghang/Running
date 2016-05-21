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
                String uName = mNameEditText.getText().toString();
                String uPwd = mPasswordEditText.getText().toString();
                //仅用于测试时使用
                if (uName.equals("") && uPwd.equals("")) {
                    Intent intent = new Intent(NavActivity.this, MainActivity.class);
                    intent.putExtra("username", mNameEditText.getText().toString());
                    startActivity(intent);
                    NavActivity.this.finish();
                } else {
                    //真实登录代码
                    mProgressDialog = ProgressDialog.show(this, "请等待...", "正在为您登录...");
                    handleLogin(uName, uPwd);
                }
                break;
            case R.id.regist:
                //跳转注册页面
                startActivity(new Intent(NavActivity.this, RegisterActivity.class));
                break;
        }
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
}
