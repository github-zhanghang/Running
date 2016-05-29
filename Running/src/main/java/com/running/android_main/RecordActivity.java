package com.running.android_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.running.myviews.TopBar;

public class RecordActivity extends AppCompatActivity {
    private TopBar mTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mTopBar = (TopBar) findViewById(R.id.record_topbar);
    }
}
