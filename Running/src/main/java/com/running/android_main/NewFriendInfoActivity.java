package com.running.android_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.running.beans.UserInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import io.rong.message.ContactNotificationMessage;
import okhttp3.Call;

public class NewFriendInfoActivity extends AppCompatActivity {

    public static final String ADD_FRIEND
            = "http://10.201.1.185:8080/Running/RequestFriendServlet";
    public static final String TAG = "NewFriendInfoActivity";
    UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend_info);
        mUserInfo = (UserInfo) getIntent().getSerializableExtra("NewFriendInfo");
        Log.e("test123", "NewFriendInfoActivity: "+mUserInfo.getNickName());
    }

    public void onClickAddNewFriend(View view) {
        request();
    }

    private void request() {
        OkHttpUtils
                .get()
                .url(ADD_FRIEND)
                .addParams("flag", ContactNotificationMessage.CONTACT_OPERATION_REQUEST)
                .addParams("sourceUserId","1")//用户id
                .addParams("targetUserId",mUserInfo.getUid()+"")
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
