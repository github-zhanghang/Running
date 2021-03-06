package com.running.android_main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.running.myviews.MyToggleButton;
import com.running.myviews.TopBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class SettingActivity extends AppCompatActivity implements
        MyToggleButton.OnToggleStateChangeListener {

    private PopupWindow mPopupWindow;
    private SharedPreferences mSharedPreferences;

    @Bind(R.id.setting_topBar)
    TopBar mSettingTopBar;
    @Bind(R.id.push_message)
    TextView mPushMessage;
    @Bind(R.id.setting_myToggleButton)
    MyToggleButton mSettingMyToggleButton;
    @Bind(R.id.update_password)
    TextView mUpdatePassword;
    @Bind(R.id.update_phone)
    TextView mUpdatePhone;
    @Bind(R.id.check_update)
    TextView mCheckUpdate;
    @Bind(R.id.feed_back)
    TextView mFeedBack;
    @Bind(R.id.recommend_friend)
    TextView mRecommendFriend;
    @Bind(R.id.about_us)
    TextView mAboutUs;
    @Bind(R.id.quit_account)
    Button mQuitAccount;
    @Bind(R.id.current_version)
    TextView mCurrentVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        mSharedPreferences = getSharedPreferences("JPushMessage", MODE_PRIVATE);
        boolean isReceiveMessage = mSharedPreferences.getBoolean("isReceiveMessage", true);
        mSettingMyToggleButton.setToggleState(isReceiveMessage);
        mSettingMyToggleButton.setOnToggleStateChangeListener(this);
        addListener();
    }

    private void addListener() {
        mSettingTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                SettingActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
            }
        });
    }

    //监听开启与关闭消息推送
    @Override
    public void onToggleStateChange(boolean state) {
        if (state) {
            show("已开启推送");
            JPushInterface.resumePush(getApplicationContext());
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("isRememberPassword", true);
            editor.apply();
        } else {
            show("已关闭推送");
            JPushInterface.stopPush(getApplicationContext());
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean("isRememberPassword", false);
            editor.apply();
        }
    }

    @OnClick({R.id.update_password, R.id.update_phone, R.id.check_update, R.id.feed_back,
            R.id.recommend_friend, R.id.about_us, R.id.quit_account})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_password:
                startActivity(new Intent(this, UpdatePasswordActivity.class));
                break;
            case R.id.update_phone:
                startActivity(new Intent(this, UpdatePhoneActivity.class));
                break;
            case R.id.check_update:
                final View prompt = getLayoutInflater().inflate(
                        R.layout.check_update_popup_window, null);
                mPopupWindow = new PopupWindow(prompt, LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, true);
                prompt.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (prompt != null && prompt.isShown()) {
                            mPopupWindow.dismiss();
                            mPopupWindow = null;
                        }
                        return false;
                    }
                });
                mPopupWindow.showAtLocation(prompt, Gravity.CENTER, 0, 0);
                break;
            case R.id.feed_back:
                //意见反馈
                final View feedBack = getLayoutInflater().inflate(
                        R.layout.feed_back_popup_window, null);
                mPopupWindow = new PopupWindow(feedBack, LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, true);
                feedBack.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (feedBack != null && feedBack.isShown()) {
                            mPopupWindow.dismiss();
                            mPopupWindow = null;
                        }
                        return false;
                    }
                });
                mPopupWindow.showAtLocation(feedBack, Gravity.CENTER, 0, 0);
                break;
            case R.id.recommend_friend:
                //推荐给好友(一键分享)
                ShareSDK.initSDK(SettingActivity.this);
                OnekeyShare oks = new OnekeyShare();
                oks.disableSSOWhenAuthorize();
                oks.setTitle("Running");
                oks.setImageUrl("http://img5.imgtn.bdimg.com/it/u=4183830072,930741777&fm=21&gp=0.jpg");
                oks.show(SettingActivity.this);
                break;
            case R.id.about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            case R.id.quit_account:
                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                ((MyApplication) getApplication()).finish();
                SettingActivity.this.finish();
                break;
        }
    }

    public void show(String text) {
        Toast.makeText(SettingActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}
