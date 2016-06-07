package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.running.beans.History;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class HistoryDetailActivity extends AppCompatActivity {

    MapView mHisDetailMapView;
    TextView dateTextView,distanceTextView,timeTextView,speedTextView,walkTextView,calorieTextView;
    History history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_history_detail);

        getData();
        initView();
        setData();
    }

    private void getData() {
        // 接收到网址
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null){
            history= (History) bundle.getSerializable("history");
            Log.e("taozihistory", history.toString() );
        }
    }

    private void initView() {
        mHisDetailMapView= (MapView) findViewById(R.id.map_his_detail);
        dateTextView= (TextView) findViewById(R.id.starttime_his_detail);
        distanceTextView=(TextView) findViewById(R.id.distance_his_detail);
        timeTextView=(TextView) findViewById(R.id.time_his_detail);
        speedTextView=(TextView) findViewById(R.id.speed_his_detail);
        walkTextView=(TextView) findViewById(R.id.speed_his_detail);
        calorieTextView=(TextView) findViewById(R.id.calorie_his_detail);
    }

    private void setData() {
        DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat dateFormat2=new SimpleDateFormat("hh:mm:ss");
        dateTextView.setText(dateFormat.format(history.getRunstarttime()));
        distanceTextView.setText(history.getRundistance()+"");
        Log.e("taozihistory",history.getRundistance()+"" );
        timeTextView.setText(dateFormat2.format(history.getRuntime()));
        speedTextView.setText(history.getRunspeed()+"");
        walkTextView.setText(history.getStepcount()+"");
        calorieTextView.setText(history.getCalories()+"");
    }
}
