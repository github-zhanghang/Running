package com.running.android_main;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.running.myviews.TopBar;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UpdatePhoneActivity extends AppCompatActivity {
    private MyApplication mApplication;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private RequestQueue mRequestQueue = NoHttp.newRequestQueue(1);
    private String mPath = "http://192.168.191.1:8080/Running/passwordServlet";

    @Bind(R.id.update_phone_topBar)
    TopBar mUpdatePhoneTopBar;
    @Bind(R.id.update_phone_oldPhone)
    EditText mUpdatePhoneOldPhone;
    @Bind(R.id.update_phone_newPhone)
    EditText mUpdatePhoneNewPhone;
    @Bind(R.id.update_phone_ensurePhone)
    EditText mUpdatePhoneEnsurePhone;
    @Bind(R.id.update_phone_yes)
    Button mUpdatePhoneYes;
    @Bind(R.id.update_phone_cancel)
    Button mUpdatePhoneCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone);
        mApplication = (MyApplication) getApplication();
        ButterKnife.bind(this);
        addListener();
    }

    private void addListener() {
        mUpdatePhoneTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                UpdatePhoneActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });

        mUpdatePhoneYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPhone = mUpdatePhoneOldPhone.getText().toString();
                String newPnone = mUpdatePhoneNewPhone.getText().toString();
                String confirmPhone = mUpdatePhoneEnsurePhone.getText().toString();
                if (oldPhone.trim().equals("")) {
                    showToast("原手机号不能为空");
                    return;
                }
                if (!isMobileNO(oldPhone)) {
                    showToast("原手机号格式不正确");
                    return;
                }
                if (newPnone.trim().equals("")) {
                    showToast("新手机号不能为空");
                    return;
                }
                if (!isMobileNO(newPnone)) {
                    showToast("新手机号格式不正确");
                    return;
                }
                if (confirmPhone.trim().equals("")) {
                    showToast("确认手机号不能为空");
                    return;
                }
                if (!isMobileNO(newPnone)) {
                    showToast("确认手机号格式不正确");
                    return;
                }
                if (!newPnone.equals(confirmPhone)) {
                    showToast("两次手机号不一致，请确认手机号");
                    return;
                }
                mProgressDialog = ProgressDialog.show(UpdatePhoneActivity.this, "请等待...", "正在提交数据...");
                submitNewPhone(oldPhone, newPnone);
            }
        });

        mUpdatePhoneCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePhoneActivity.this.finish();
            }
        });
    }

    private void submitNewPhone(String oldPhone, String newPnone) {
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "changephone");
        request.add("account", mApplication.getUserInfo().getAccount());
        request.add("oldphone", oldPhone);
        request.add("newphone", newPnone);
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
                    showToast("原手机号错误");
                    return;
                }
                if (resultCode == 1) {
                    showToast("修改手机号成功");
                    UpdatePhoneActivity.this.finish();
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
        mToast = Toast.makeText(UpdatePhoneActivity.this, text, Toast.LENGTH_SHORT);
        mToast.show();
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
