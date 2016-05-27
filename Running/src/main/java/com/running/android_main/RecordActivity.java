package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.running.myviews.RecordBar;
import com.running.myviews.TopBar;

public class RecordActivity extends AppCompatActivity  implements View.OnClickListener{
    private TopBar mTopBar;
   private RecordBar historyRecordBar,trendRecordBar,bestRecordBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mTopBar = (TopBar) findViewById(R.id.record_topbar);

        initView();
        addListener();
    }
    private void initView() {
        historyRecordBar= (RecordBar) findViewById(R.id.re_history);
        trendRecordBar= (RecordBar) findViewById(R.id.re_trend);
        bestRecordBar= (RecordBar) findViewById(R.id.re_best);
    }

    private void addListener() {
        historyRecordBar.setOnClickListener(this);
        trendRecordBar.setOnClickListener(this);
        bestRecordBar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.re_history:
                Intent intent=new Intent(RecordActivity.this,HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.re_trend:
                Intent intent2=new Intent(RecordActivity.this,TrendActivity.class);
                startActivity(intent2);
                break;
            case R.id.re_best:
                Intent intent3=new Intent(RecordActivity.this,BestActivity.class);
                startActivity(intent3);
                break;
        }

    }
}
