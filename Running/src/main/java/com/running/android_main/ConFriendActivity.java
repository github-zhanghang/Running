package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baidu.platform.comapi.map.C;
import com.running.adapters.ConFriendAdapter;
import com.running.beans.UserInfo;

import java.util.List;

public class ConFriendActivity extends AppCompatActivity {
    private List<UserInfo> mUserInfoList;
    private ListView mListView;
    private ConFriendAdapter mConFriendAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initData();
        clickListener();

    }



    private void initViews() {
        mUserInfoList = (List<UserInfo>) getIntent().getExtras().get("SearchConditions");
        mListView = (ListView) findViewById(R.id.confriend_lv);
        mConFriendAdapter = new ConFriendAdapter(ConFriendActivity.this,mUserInfoList);
        mListView.setAdapter(mConFriendAdapter);
    }
    private void initData() {

    }
    private void clickListener() {
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
