package com.running.android_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.running.adapters.ConFriendAdapter;
import com.running.beans.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class ConFriendActivity extends AppCompatActivity {
    private List<UserInfo> mUserInfoList;
    private ListView mListView;
    private ConFriendAdapter mConFriendAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_con_friend);
        initViews();
        initData();

    }

    private void initViews() {
        mUserInfoList = (List<UserInfo>) getIntent().getExtras().get("SearchConditions");
        mListView = (ListView) findViewById(R.id.confriend_lv);
        mConFriendAdapter = new ConFriendAdapter(ConFriendActivity.this,mUserInfoList);
        mListView.setAdapter(mConFriendAdapter);
    }
    private void initData() {

    }
}
