package com.running.android_main;

import android.graphics.Point;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.running.myviews.CircleButton;

import java.util.ArrayList;
import java.util.List;

import static com.running.android_main.R.id.run_distance_txt;

public class RunMapActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private TextView mRunTimeText, mRunSpeedText, mRunDistanceText, mRunCalorieText;
    private CircleButton mStopButton, mContinueButton, mOverButton;

    private LocationClient mLocationClient = null;
    private MyLocationData mLocationData;
    //位置监听
    private BDLocationListener mBDLocationListener = null;
    //保存位置坐标,将Double转为浮点型
    private List<PointF> mPointList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_runmap);
        initViews();
        setListeners();
        //定位
        initLocation();
        //开始定位
        mLocationClient.start();
        // 设置为跟随模式,显示手机方向的箭头
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
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

    private void initLocation() {
        mBaiduMap = mMapView.getMap();
        //允许定位
        mBaiduMap.setMyLocationEnabled(true);
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //初始化监听类
        mBDLocationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location == null)
                    return;
                mLocationData = new MyLocationData.Builder().accuracy(location.getRadius()) // 定位的精确度
                        .direction(location.getDirection()) // 定位的方向 0~360度
                        .latitude(location.getLatitude()).longitude(location.getLongitude()) // 经纬度
                        .speed(location.getSpeed()).build();
                // 设置定位数据到定位图层上,就是显示当前位置的那个点和圆圈
                mBaiduMap.setMyLocationData(mLocationData);
                //设置地图默认位置为当前位置
                LatLng cenpt = new LatLng(location.getLatitude(), location.getLongitude());
                //定义地图状态
                MapStatus mMapStatus = new MapStatus.Builder()
                        .target(cenpt)
                        .zoom(18)
                        .build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                mBaiduMap.setMapStatus(mMapStatusUpdate);
                Log.e("my", location.getAddrStr());
                Toast.makeText(RunMapActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
            }
        };
        //注册监听函数
        mLocationClient.registerLocationListener(mBDLocationListener);
        //定位设置
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        int span = 2000;
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setScanSpan(span);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setLocationNotify(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        ;
        mLocationClient.setLocOption(option);
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
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        mLocationClient.stop();
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
