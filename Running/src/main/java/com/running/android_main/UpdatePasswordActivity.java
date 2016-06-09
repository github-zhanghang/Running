package com.running.android_main;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.running.myviews.TopBar;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UpdatePasswordActivity extends AppCompatActivity {
    private MyApplication mApplication;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private RequestQueue mRequestQueue = NoHttp.newRequestQueue(1);
    private String mPath = "http://192.168.191.1:8080/Running/passwordServlet";

    @Bind(R.id.update_password_topBar)
    TopBar mUpdatePasswordTopBar;
    @Bind(R.id.update_pw_headImg)
    ImageView mUpdatePwHeadImg;
    @Bind(R.id.update_pw_name)
    TextView mUpdatePwName;
    @Bind(R.id.update_pw_oldPW)
    EditText mUpdatePwOldPW;
    @Bind(R.id.update_pw_newPW)
    EditText mUpdatePwNewPW;
    @Bind(R.id.update_pw_confirmNewPW)
    EditText mUpdatePwConfirmNewPW;
    @Bind(R.id.update_pw_submit)
    Button mUpdatePwSubmit;
    @Bind(R.id.update_pw_cancel)
    Button mUpdatePwCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        mApplication = (MyApplication) getApplication();
        ButterKnife.bind(this);
        initData();
        addListener();
    }

    private void initData() {
        mUpdatePwName.setText(mApplication.getUserInfo().getNickName());
        Glide.with(this)
                .load(mApplication.getUserInfo().getImageUrl())
                .fitCenter()
                .error(R.mipmap.ic_launcher)
                .into(mUpdatePwHeadImg);
    }

    private void addListener() {
        mUpdatePasswordTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                UpdatePasswordActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
        mUpdatePwSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = mUpdatePwOldPW.getText().toString();
                String newPwd = mUpdatePwNewPW.getText().toString();
                String confirmPwd = mUpdatePwConfirmNewPW.getText().toString();
                if (oldPwd.trim().equals("")) {
                    showToast("原密码不能为空");
                    return;
                }
                if (newPwd.trim().equals("")) {
                    showToast("新密码不能为空");
                    return;
                }
                if (confirmPwd.trim().equals("")) {
                    showToast("确认密码不能为空");
                    return;
                }
                if (!newPwd.equals(confirmPwd)) {
                    showToast("两次密码不一致，请确认密码");
                    return;
                }
                mProgressDialog = ProgressDialog.show(UpdatePasswordActivity.this, "请等待...", "正在提交数据...");
                submitNewPwd(oldPwd, newPwd);
            }
        });
        mUpdatePwCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePasswordActivity.this.finish();
            }
        });
    }

    private void submitNewPwd(String oldPwd, String newPwd) {
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "changepassword");
        request.add("account", mApplication.getUserInfo().getAccount());
        request.add("oldpassword", oldPwd);
        request.add("newpassword", newPwd);
        mRequestQueue.add(1, request, mOnResponseListener);
        mRequestQueue.start();
    }

    private OnResponseListener<String> mOnResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            //Log.e("my", "result=" + result);
            if (what == 1) {
                mProgressDialog.dismiss();
                int resultCode = Integer.parseInt(result);
                if (resultCode == -1) {
                    showToast("提交数据失败，请稍后再试");
                    return;
                }
                if (resultCode == 0) {
                    showToast("原密码错误");
                    return;
                }
                if (resultCode == 1) {
                    showToast("修改密码成功");
                    UpdatePasswordActivity.this.finish();
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
    };

    public void showToast(String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(UpdatePasswordActivity.this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
