package com.running.android_main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    //短信验证
    private final static String APP_KEY = "12a1127a423ef";
    private final static String APP_SECRET = "4d8275b912a9aceb7f39fdbf91b92da4";
    private EditText mNameText, mPasswordText, mConfirmPwdText, mPhoneText, mCodeText;
    private Button mGetCodeButton, mRegisterButton;

    private String phoneNumber;
    private String phoneCode;

    private boolean isTimeRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        initViews();
        setListeners();
        initSSDK();
    }

    private void initSSDK() {
        SMSSDK.initSDK(this, APP_KEY, APP_SECRET);
        //注册短信回调
        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                switch (event) {
                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            Log.e("my", "验证成功");
                        } else {
                            Log.e("my", "验证失败");
                        }
                        break;
                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            Log.e("my", "获取验证码成功");
                            //默认的智能验证是开启的,我已经在后台关闭
                        } else {
                            Log.e("my", "获取验证码失败");
                        }
                        break;
                }
            }
        });
    }

    private void setListeners() {
        mGetCodeButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
    }

    private void initViews() {
        mNameText = (EditText) findViewById(R.id.register_name);
        mPasswordText = (EditText) findViewById(R.id.register_password);
        mConfirmPwdText = (EditText) findViewById(R.id.register_confirm);
        mPhoneText = (EditText) findViewById(R.id.register_phone);
        mCodeText = (EditText) findViewById(R.id.register_code);
        mGetCodeButton = (Button) findViewById(R.id.getCode);
        mRegisterButton = (Button) findViewById(R.id.goRegister);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getCode:
                mGetCodeButton.setClickable(false);
                updateGetCodeButton();
                phoneNumber = mPhoneText.getText().toString();
                if (phoneNumber.trim().equals("") || phoneNumber == null)
                    return;
                //获取验证码
                SMSSDK.getVerificationCode("86", phoneNumber, null);
                break;
            case R.id.goRegister:
                phoneCode = mCodeText.getText().toString();
                startRegister();
                break;
        }
    }

    private void startRegister() {
        //提交验证码
        SMSSDK.submitVerificationCode("86", phoneNumber, phoneCode);
    }

    private void updateGetCodeButton() {
        //倒计时60秒
        new AsyncTask<Integer, Integer, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                int time = params[0];
                try {
                    while (!isTimeRun) {
                        Thread.sleep(1000);
                        time--;
                        Log.e("my", "time=" + time);
                        if (time <= 0) {
                            isTimeRun = true;
                        }
                        publishProgress(time);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                mGetCodeButton.setText(values[0] + "秒后重新获取");
                if (isTimeRun) {
                    mGetCodeButton.setClickable(true);
                    mGetCodeButton.setText("重新获取");
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                isTimeRun = false;
            }
        }.execute(6);
    }

    private void submitUserInfo() {
        //// TODO: 将用户信息插入数据库
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}
