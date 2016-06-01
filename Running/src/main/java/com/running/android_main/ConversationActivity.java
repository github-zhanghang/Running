package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class ConversationActivity extends AppCompatActivity {

    /**
     * 会话界面
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Intent intent = getIntent();
        //获取 userid 和标题
        String  id = intent.getData().getQueryParameter("targetId");
        String title = intent.getData().getQueryParameter("title");
        String type = intent.getData().getQueryParameter("type");
        Log.e("test123", "id: "+id+"  title:"+title +"  type:"+ type);
    }
}
