package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.running.myviews.TopBar;

public class ConversationActivity extends AppCompatActivity {
    private TopBar mTopBar;
    /**
     * 会话界面
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        initViews();

    }

    private void initViews() {
        Intent intent = getIntent();
        //获取 userid 和标题
        String  id = intent.getData().getQueryParameter("targetId");
        String title = intent.getData().getQueryParameter("title");
        String type = intent.getData().getQueryParameter("type");
        Log.e("test123", "id: "+id+"  title:"+title +"  type:"+ type);
        mTopBar = (TopBar) findViewById(R.id.conversation_TopBar);
        mTopBar.setLeftText(title);

    }
}
