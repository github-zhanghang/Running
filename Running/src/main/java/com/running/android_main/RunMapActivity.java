package com.running.android_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.running.myviews.CircleButton;

import static com.running.android_main.R.id.find;
import static com.running.android_main.R.id.run_distance_txt;

public class RunMapActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView;
    private TextView mRunTimeText, mRunSpeedText, mRunDistanceText, mRunCalorieText;
    private CircleButton mStopButton, mContinueButton, mOverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_runmap);
        initViews();
        setListeners();
    }

    private void initViews() {
        mMapView = (MapView) findViewById(R.id.mapview);
        mRunTimeText = (TextView) findViewById(R.id.run_time_txt);
        mRunSpeedText = (TextView) findViewById(R.id.run_speed_txt);
        mRunDistanceText = (TextView) findViewById(run_distance_txt);
        mRunCalorieText = (TextView) findViewById(R.id.run_calorie_txt);
        mStopButton = (CircleButton) findViewById(R.id.run_stop);
        mContinueButton = (CircleButton) findViewById(R.id.run_continue);
        mOverButton = (CircleButton) findViewById(R.id.run_over);
    }

    private void setListeners() {
        mStopButton.setOnClickListener(this);
        mContinueButton.setOnClickListener(this);
        mOverButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_stop:
                handStop();
                break;
            case R.id.run_continue:
                handcontinue();
                break;
            case R.id.run_over:
                handover();
                break;
        }
    }

    private void handStop() {
        mStopButton.setVisibility(View.GONE);
        mContinueButton.setVisibility(View.VISIBLE);
        mOverButton.setVisibility(View.VISIBLE);
    }

    private void handcontinue() {
        mStopButton.setVisibility(View.VISIBLE);
        mContinueButton.setVisibility(View.GONE);
        mOverButton.setVisibility(View.GONE);
    }

    private void handover() {
        Toast.makeText(RunMapActivity.this, "Run Over", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
}
