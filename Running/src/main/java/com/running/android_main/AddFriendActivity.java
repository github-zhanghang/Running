package com.running.android_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
    }

    public void addFriend(View view) {
        /*应该先去服务端查找有无此账号，没有提示无此用户，
            有的话的返回  跳转到用户资料页，在那里再跳转加为好友?
        */

    }
}
