package com.running.android_main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

public class Register1Activity extends AppCompatActivity implements View.OnClickListener {
    private MyApplication mApplication;
    //短信验证
    private final String APP_KEY = "12a1127a423ef";
    private final String APP_SECRET = "4d8275b912a9aceb7f39fdbf91b92da4";

    private EditText mPhoneEditText, mPasswordEditText, mConfirmEditText, mCodeEditText;
    private Button mGetCodeButton, mRegisterButton;
    private TextView mLoginTextView;

    private String mPhoneNumber;
    private String mPassword, mConfirmPassword;
    //是否已经获取验证码
    private boolean isSuccess = false;
    //倒计时
    private int seconds = 60;
    //倒计时的开关
    private boolean isRunning = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (seconds == 0) {
                    mHandler.removeMessages(1);
                    mGetCodeButton.setText("再次获取");
                    mGetCodeButton.setClickable(true);
                    mGetCodeButton.setBackgroundColor(Color.parseColor("#99a0dc4d"));
                } else {
                    mGetCodeButton.setText(seconds + "秒后重新获取");
                }
            } else if (msg.what == 4) {
                Toast.makeText(Register1Activity.this, "验证码错误", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);
        mApplication = (MyApplication) getApplication();
        mApplication.addActivity(this);
        initView();
        initListener();
        initMobileSDK();
    }

    private void initView() {
        mPhoneEditText = (EditText) findViewById(R.id.register_phone);
        mPasswordEditText = (EditText) findViewById(R.id.register_password);
        mConfirmEditText = (EditText) findViewById(R.id.register_confirm);
        mCodeEditText = (EditText) findViewById(R.id.register_code);
        mGetCodeButton = (Button) findViewById(R.id.getCode);
        mRegisterButton = (Button) findViewById(R.id.goRegister);
        mLoginTextView = (TextView) findViewById(R.id.goLogin);
    }

    private void initListener() {
        mGetCodeButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        mLoginTextView.setOnClickListener(this);
    }

    private void initMobileSDK() {
        SMSSDK.initSDK(this, APP_KEY, APP_SECRET);
        //注册短信回调
        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                switch (event) {
                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            Log.e("my", "验证成功");
                            Intent intent = new Intent(Register1Activity.this, Register2Activity.class);
                            intent.putExtra("password", mPassword);
                            intent.putExtra("telephone", mPhoneNumber);
                            startActivity(intent);
                            Register1Activity.this.finish();
                        } else {
                            Log.e("my", "验证失败");
                            mHandler.sendEmptyMessage(4);
                        }
                        break;
                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            Log.e("my", "获取验证码成功");
                            isSuccess = true;
                        } else {
                            Log.e("my", "获取验证码失败");
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getCode:
                mPhoneNumber = mPhoneEditText.getText().toString();
                if (!isMobileNO(mPhoneNumber)) {
                    Toast.makeText(Register1Activity.this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
                } else {
                    changeButtonStyle();
                    //获取验证码
                    SMSSDK.getVerificationCode("86", mPhoneNumber, new OnSendMessageHandler() {
                        @Override
                        public boolean onSendMessage(String s, String s1) {
                            return false;
                        }
                    });
                }
                break;
            case R.id.goRegister:
                mPhoneNumber = mPhoneEditText.getText().toString();
                if (mPhoneNumber.equals("")) {
                    Toast.makeText(Register1Activity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPassword = mPasswordEditText.getText().toString();
                if (mPassword.equals("")) {
                    Toast.makeText(Register1Activity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPassword.length() < 4 || mPassword.length() > 12) {
                    Toast.makeText(Register1Activity.this, "密码长度为4-12位", Toast.LENGTH_SHORT).show();
                    return;
                }
                mConfirmPassword = mConfirmEditText.getText().toString();
                if (!mPassword.equals(mConfirmPassword)) {
                    Toast.makeText(Register1Activity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isSuccess) {
                    Toast.makeText(Register1Activity.this, "请先获取验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                //提交验证码
                SMSSDK.submitVerificationCode("86", mPhoneNumber, mCodeEditText.getText().toString());
                break;
            case R.id.goLogin:
                startActivity(new Intent(Register1Activity.this, LoginActivity.class));
                Register1Activity.this.finish();
                break;
        }
    }

    private void changeButtonStyle() {
        isRunning = true;
        seconds = 60;
        mGetCodeButton.setClickable(false);
        mGetCodeButton.setBackgroundColor(Color.GRAY);
        //开启线程读秒
        new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    if (seconds >= 0) {
                        try {
                            mHandler.sendEmptyMessage(1);
                            Thread.sleep(1000);
                            seconds--;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            isRunning = false;
                        }
                    } else {
                        isRunning = false;
                    }
                }
            }
        }.start();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        mHandler = null;
        ShareSDK.stopSDK();
    }
}
