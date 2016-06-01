package com.running.android_main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.running.myviews.ScaleRulerView;

public class Register3Activity extends AppCompatActivity {
    private static final String mPath = "http://192.168.191.1:8080/Running/registerServlet";

    private ScaleRulerView mHeightWheelView;
    private TextView mHeightValue;
    private ScaleRulerView mWeightWheelView;
    private TextView mWeightValue;

    private float mHeight = 170;
    private float mMaxHeight = 240;
    private float mMinHeight = 100;

    private float mWeight = 65;
    private float mMaxWeight = 250;
    private float mMinWeight = 25;

    private String mPassword, mTelephone, mSex;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);
        Intent intent = getIntent();
        mPassword = intent.getStringExtra("password");
        mTelephone = intent.getStringExtra("telephone");
        mSex = intent.getStringExtra("sex");

        initView();
        addListener();
    }

    private void addListener() {
        mHeightWheelView.initViewParam(mHeight, mMaxHeight, mMinHeight);
        mHeightWheelView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mHeightValue.setText((int) value + "");
                mHeight = value;
            }
        });

        mWeightWheelView.initViewParam(mWeight, mMaxWeight, mMinWeight);
        mWeightWheelView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mWeightValue.setText(value + "");
                mWeight = value;
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
        //mProgressDialog = ProgressDialog.show(this, "请等待...", "正在提交注册...");
        String result = "手机号:" + mTelephone + ";\n" +
                "密码:" + mPassword + ";\n" +
                "性别:" + mSex + ";\n" +
                "身高:" + mHeight + ";\n" +
                "体重:" + mWeight;
        Toast.makeText(Register3Activity.this, result, Toast.LENGTH_SHORT).show();
        Log.e("my", result);
        /*OkHttpUtils
                .get()
                .url(mPath)
                .addParams("telephone", mTelephone)
                .addParams("password", mPassword)
                .addParams("sex", mSex)
                .addParams("height", "" + mHeight)
                .addParams("height", "" + mWeight)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(Register3Activity.this, "登录失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response) {
                        if (response.equals("")) {

                        }
                    }
                });*/
    }
}
