package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.running.beans.UserInfo;
import com.running.myviews.CircleImageView;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import okhttp3.Call;

public class PersonInformationActivity extends AppCompatActivity {

    @Bind(R.id.person_information_topBar)
    TopBar mTopBar;
    @Bind(R.id.person_information_headImg)
    CircleImageView mHeadImg;
    @Bind(R.id.person_information_name)
    TextView mName;
    @Bind(R.id.person_information_sexAndAge)
    TextView mSexAndAge;
    @Bind(R.id.person_information_account)
    TextView mAccount;
    @Bind(R.id.person_information_location)
    TextView mLocation;
    @Bind(R.id.person_information_sumDistance)
    TextView mSumDistance;
    @Bind(R.id.person_information_sumTime)
    TextView mSumTime;
    @Bind(R.id.person_information_whoDynamic)
    TextView mWhoDynamic;
    @Bind(R.id.person_information_dynamicLayout)
    LinearLayout mDynamicLayout;
    @Bind(R.id.person_information_sendMessage)
    Button mSendMessage;
    private UserInfo mUserInfo;
    private HashMap<String, String> map;
    private PInformationCallBack mCallBack;
    private String url = MyApplication.HOST + "totalRecordServlet";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    map = mCallBack.map;
                    setData();
                    break;
            }
        }
    };
    //
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_information);
        Intent intent = getIntent();
        //得到个人信息bean
        mUserInfo = (UserInfo) intent.getExtras().get("UserInfo");
        ButterKnife.bind(this);
        //得到个人跑步数据总里程数和总时间
        getPersonSumData(mUserInfo.getUid());
        addListener();
    }

    private void getPersonSumData(int uid) {
        OkHttpUtils.post()
                .url(url)
                .addParams("type", "totaldata")
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(mCallBack = new PInformationCallBack());
    }

    //显示数据
    private void setData() {
        Glide.with(this)
                .load(mUserInfo.getImageUrl())
                .into(mHeadImg);
        mName.setText(mUserInfo.getNickName());
        mSexAndAge.setText(mUserInfo.getSex() + " " + mUserInfo.getAge());
        mAccount.setText(mUserInfo.getAccount());
        mLocation.setText(mUserInfo.getAddress());
        mSumDistance.setText(map.get("distance"));
        mSumTime.setText(map.get("time"));
        mWhoDynamic.setText(mUserInfo.getNickName()+"的动态");
    }

    private void addListener() {
        //TopBar监听事件
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                PersonInformationActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
                Toast.makeText(PersonInformationActivity.this, "备注", Toast.LENGTH_SHORT).show();
            }
        });
        //发送消息
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat
                            (PersonInformationActivity.this, mUserInfo.getAccount(), mUserInfo.getNickName());
                }
            }
        });

        //跳转至个人动态
        mDynamicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public class PInformationCallBack extends StringCallback {
        HashMap<String, String> map = new HashMap<>();

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(String response) {
            String[] result = response.split(",");
            map.put("distance", result[0]);
            Long sumTime = Long.valueOf(result[1]);
            Long h = sumTime / (60 * 60 * 1000);
            Long m = (sumTime % (60 * 60 * 1000)) / (60 * 1000);
            Long s = ((sumTime % (60 * 60 * 1000)) % (60 * 1000)) / 1000;
            String time = h + "h" + m + "m" + s + "s";
            map.put("time", time);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                }
            }).start();
        }
    }

}
