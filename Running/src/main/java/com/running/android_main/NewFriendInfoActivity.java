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
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import io.rong.message.ContactNotificationMessage;
import okhttp3.Call;

public class NewFriendInfoActivity extends AppCompatActivity {
    private ImageView mImageView;
    private TextView nameTextView,accountTextView,addressTextView;

    public static final String ADD_FRIEND
            = "http://10.201.1.185:8080/Running/RequestFriendServlet";
    public static final String TAG = "NewFriendInfoActivity";
    NearUserInfo mUserInfo;
    UserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend_info);

        initViews();
        initData();
    }


    private void initViews() {
        mImageView = (ImageView) findViewById(R.id.near_information_headImg);
        nameTextView = (TextView) findViewById(R.id.near_information_name);
        accountTextView  = (TextView) findViewById(R.id.near_information_account);
        addressTextView = (TextView) findViewById(R.id.near_information_location);
    }
    private void initData() {
        mUserInfo = (NearUserInfo)getIntent().getExtras().get("NearbyActivity");
        userInfo = mUserInfo;
        Log.e("test123", "NewFriendInfoActivity: "+userInfo.getNickName());
        Glide.with(NewFriendInfoActivity.this)
                .load(mUserInfo.getImageUrl())
                .into(mImageView);
        nameTextView.setText(mUserInfo.getNickName());
        accountTextView.setText(mUserInfo.getAccount());
        addressTextView.setText(mUserInfo.getAddress());
    }



    public void onClickAddNewFriend(View view) {
        request();
    }

    private void request() {
        OkHttpUtils
                .get()
                .url(ADD_FRIEND)
                .addParams("flag", ContactNotificationMessage.CONTACT_OPERATION_REQUEST)
                .addParams("sourceUserId",new Gson().toJson(userInfo))//user信息
                .addParams("targetUserId",new Gson().toJson(userInfo))//用户信息
                .addParams("message",mUserInfo.getNickName()+"请求加你为好友")
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
                            Toast.makeText(NewFriendInfoActivity.this, "好友请求已经发送", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
