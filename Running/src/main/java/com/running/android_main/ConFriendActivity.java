package com.running.android_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.running.beans.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class ConFriendActivity extends AppCompatActivity {
    private List<UserInfo> mUserInfoList;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_con_friend);
        initViews();
        initDatas();
    }

    private void initViews() {
        mUserInfoList = (List<UserInfo>) getIntent().getExtras().get("");
        mListView = (ListView) findViewById(R.id.confriend_lv);

    }
    private void initDatas() {

    }
}
