package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.platform.comapi.map.C;
import com.running.adapters.ConFriendAdapter;
import com.running.beans.UserInfo;
import com.running.myviews.TopBar;

import java.util.List;

public class ConFriendActivity extends AppCompatActivity {
    private List<UserInfo> mUserInfoList;
    private ListView mListView;
    private ConFriendAdapter mConFriendAdapter;
    private TopBar mTopBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_con_friend);

        initViews();
        initData();
        clickListener();

    }



    private void initViews() {

        mUserInfoList = (List<UserInfo>) getIntent().getExtras().get("SearchConditions");
        mListView = (ListView) findViewById(R.id.confriend_lv);
        mTopBar = (TopBar) findViewById(R.id.confriend_TopBar);
        Log.e("Ezio123", "initViews: "+mUserInfoList.size());

    }
    private void initData() {
        mConFriendAdapter = new ConFriendAdapter(ConFriendActivity.this,mUserInfoList);
        mListView.setAdapter(mConFriendAdapter);
    }
    private void clickListener() {
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                ConFriendActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ConFriendActivity.this, NewFriendInfoActivity.class);
                intent.putExtra("FriendInfo", mUserInfoList.get(position));
                startActivity(intent);
            }
        });
    }
}
