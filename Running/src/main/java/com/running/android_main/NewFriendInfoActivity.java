package com.running.android_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.running.beans.NearUserInfo;
import com.running.beans.UserInfo;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import io.rong.message.ContactNotificationMessage;
import okhttp3.Call;

public class NewFriendInfoActivity extends AppCompatActivity {
    private TopBar mTopBar;
    private ImageView mImageView;
    private TextView nameTextView,accountTextView,addressTextView,sumDistanceTextView,sumTimeTextView;
    public static final String ADD_FRIEND = MyApplication.HOST + "RequestFriendServlet";
    public static final String TAG = "NewFriendInfoActivity";
    NearUserInfo mUserInfo;
    UserInfo userInfo;
    private Object mDistanceAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend_info);

        initViews();
        initData();
    }


    private void initViews() {
        mTopBar = (TopBar) findViewById(R.id.addFriend_topbar);
        mImageView = (ImageView) findViewById(R.id.near_information_headImg);
        nameTextView = (TextView) findViewById(R.id.near_information_name);
        accountTextView  = (TextView) findViewById(R.id.near_information_account);
        addressTextView = (TextView) findViewById(R.id.near_information_location);
        sumDistanceTextView = (TextView) findViewById(R.id.new_information_sumDistance);
        sumTimeTextView = (TextView) findViewById(R.id.new_information_sumTime);
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                NewFriendInfoActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
    }

    private void initData() {
        userInfo = (UserInfo) getIntent().getExtras().get("FriendInfo");
        Log.e("test123", "NewFriendInfoActivity: " + userInfo.getNickName());
        Glide.with(NewFriendInfoActivity.this)
                .load(userInfo.getImageUrl())
                .into(mImageView);
        nameTextView.setText(userInfo.getNickName());
        accountTextView.setText(userInfo.getAccount());
        addressTextView.setText(userInfo.getAddress());
        //获取运动总时间和总路程
        setDistanceAndTime();
    }

    private void setDistanceAndTime() {
        OkHttpUtils.post()
                .url(MyApplication.HOST + "totalRecordServlet")
                .addParams("type", "totaldata")
                .addParams("uid",userInfo.getUid()+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("lihaotian", "onError: "+e.getMessage() );
                    }

                    @Override
                    public void onResponse(String response) {
                        java.text.DecimalFormat df=new java.text.DecimalFormat("#0.0");//保留1位小数
                        String[] result = response.split(",");
                        double distance = Double.parseDouble(result[0]);
                        Log.e("lihaotian", "onResponse: "+distance );
                        Long sumTime = Long.valueOf(result[1]);
                        Long h = sumTime / (60 * 60 * 1000);
                        Long m = (sumTime % (60 * 60 * 1000)) / (60 * 1000);
                        Long s = ((sumTime % (60 * 60 * 1000)) % (60 * 1000)) / 1000;
                        String time = h + "h" + m + "m" + s + "s";
                        sumDistanceTextView.setText(df.format(distance)+"km");
                        sumTimeTextView.setText(time);
                    }
                });

    }

    public void onClickAddNewFriend(View view) {
        request();
    }

    private void request() {
        OkHttpUtils
                .post()
                .url(ADD_FRIEND)
                .addParams("flag", ContactNotificationMessage.CONTACT_OPERATION_REQUEST)
                .addParams("sourceUserId",new Gson().toJson(((MyApplication) getApplication()).getUserInfo()))//user信息
                .addParams("targetUserId",new Gson().toJson(userInfo))//用户信息
                .addParams("message",((MyApplication) getApplication()).getUserInfo().getNickName()+"请求加你为好友")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                    }
                    @Override
                    public void onResponse(String response) {
                        if (response.equals(ContactNotificationMessage.CONTACT_OPERATION_REQUEST)){
                            Toast.makeText(NewFriendInfoActivity.this, "好友请求发送成功", Toast.LENGTH_SHORT).show();
                        }else if (response.equals("Requested")){
                            Toast.makeText(NewFriendInfoActivity.this, "好友请求已经发送过了", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}
