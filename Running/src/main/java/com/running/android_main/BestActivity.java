package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.running.myviews.BestBar;
import com.running.myviews.MyInfoItemView;

public class BestActivity extends AppCompatActivity implements View.OnClickListener{

    BestBar timeBestBar,speedBestBar,fiveBestBar,tenBestBar,halfBestBar,wholeBestBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best);

        initView();
        addListener();
    }

    private void addListener() {
        timeBestBar.setOnClickListener(this);
        speedBestBar.setOnClickListener(this);
        fiveBestBar.setOnClickListener(this);
        tenBestBar.setOnClickListener(this);
        halfBestBar.setOnClickListener(this);
        wholeBestBar.setOnClickListener(this);
    }

    private void initView() {
        timeBestBar= (BestBar) findViewById(R.id.time_best);
        speedBestBar= (BestBar) findViewById(R.id.speed_best);
        fiveBestBar= (BestBar) findViewById(R.id.five_best);
        tenBestBar= (BestBar) findViewById(R.id.ten_best);
        halfBestBar= (BestBar) findViewById(R.id.half_best);
        wholeBestBar= (BestBar) findViewById(R.id.whole_best);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.time_best:
                Intent intent=new Intent(BestActivity.this,HistoryDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.speed_best:
                Intent intent2=new Intent(BestActivity.this,HistoryDetailActivity.class);
                startActivity(intent2);
                break;
            case R.id.five_best:
                Intent intent3=new Intent(BestActivity.this,HistoryDetailActivity.class);
                startActivity(intent3);
                break;
            case R.id.ten_best:
                Intent intent4=new Intent(BestActivity.this,HistoryDetailActivity.class);
                startActivity(intent4);
                break;
            case R.id.half_best:
                Intent intent5=new Intent(BestActivity.this,HistoryDetailActivity.class);
                startActivity(intent5);
                break;
            case R.id.whole_best:
                Intent intent6=new Intent(BestActivity.this,HistoryDetailActivity.class);
                startActivity(intent6);
                break;
        }
    }
}
