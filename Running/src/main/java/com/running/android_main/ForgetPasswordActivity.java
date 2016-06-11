package com.running.android_main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

/**
 * 忘记密码
 */
public class ForgetPasswordActivity extends AppCompatActivity {
    private String mPath = MyApplication.HOST + "passwordServlet";
    private EditText mNewPwdEditText, mConfirmPwdEditText;
    private Button mEnsureButton;
    private Toast mToast;
    private ProgressDialog mProgressDialog;

    private String mPhone, mNewPwd, mConfirmPwd;
    private RequestQueue mRequestQueue = NoHttp.newRequestQueue(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Intent intent = getIntent();
        mPhone = intent.getStringExtra("phone");
        initView();
    }

    private void initView() {
        mNewPwdEditText = (EditText) findViewById(R.id.forget_newPwd);
        mConfirmPwdEditText = (EditText) findViewById(R.id.forget_confirmPwd);
        mEnsureButton = (Button) findViewById(R.id.forget_ensure);
        mEnsureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassword();
            }
        });
    }

    private void checkPassword() {
        //判断两次密码是否一致
        mNewPwd = mNewPwdEditText.getText().toString();
        mConfirmPwd = mConfirmPwdEditText.getText().toString();
        if (mNewPwd.trim().equals("")) {
            showToast("新密码不能为空");
            return;
        }
        if (mConfirmPwd.trim().equals("")) {
            showToast("确认密码不能为空");
            return;
        }
        if (mNewPwd.length() < 4) {
            showToast("密码长度至少为4位");
            return;
        }
        if (mNewPwd.equals(mConfirmPwd)) {
            mProgressDialog = ProgressDialog.show(ForgetPasswordActivity.this, "请等待...", "正在提交数据...");
            //提交服务器修改密码
            submitNewPwd(mPhone, mNewPwd);
        } else {
            showToast("密码不一致");
        }
    }

    private void submitNewPwd(String phone, String newPwd) {
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "changepassword2");
        request.add("phone", phone);
        request.add("newpassword", newPwd);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                String result = response.get();
                Log.e("my", "result=" + result);
                if (what == 1) {
                    mProgressDialog.dismiss();
                    int resultCode = Integer.parseInt(result);
                    if (resultCode == -1) {
                        showToast("提交数据失败，请稍后再试");
                        return;
                    }
                    if (resultCode == 1) {
                        showToast("修改成功");
                        startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                        ForgetPasswordActivity.this.finish();
                    }
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                mProgressDialog.dismiss();
                showToast("提交数据失败");
            }

            @Override
            public void onFinish(int what) {
            }
        });
        mRequestQueue.start();
    }

    public void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(ForgetPasswordActivity.this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
