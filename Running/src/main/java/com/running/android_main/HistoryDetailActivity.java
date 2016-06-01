package com.running.android_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;

public class HistoryDetailActivity extends AppCompatActivity {

    MapView mHisDetailMapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_history_detail);
        mHisDetailMapView= (MapView) findViewById(R.id.map_his_detail);
    }
}
