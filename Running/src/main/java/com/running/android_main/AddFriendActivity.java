package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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


    /**
     * 按条件查找好友的点击事件
     * @param view
     */
    public void search(View view) {
        Toast.makeText(this, "条件查找", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AddFriendActivity.this,SearchConditionsActivity.class));
    }
    /**
     * 搜索附近的点击事件
     * @param view
     */
    public void searchNearby(View view) {
        Toast.makeText(this, "搜附近", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(AddFriendActivity.this,NearbyActivity.class));
    }
}
