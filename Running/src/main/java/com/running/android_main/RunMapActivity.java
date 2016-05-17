package com.running.android_main;

import android.graphics.PointF;
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
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.running.myviews.CircleButton;
import com.running.utils.MyOrientationListener;

import java.util.ArrayList;
import java.util.List;

import static com.running.android_main.R.id.run_distance_txt;

public class RunMapActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private TextView mRunTimeText, mRunSpeedText, mRunDistanceText, mRunCalorieText;
    private CircleButton mStopButton, mContinueButton, mOverButton;

    //方向
    private float mXDirection;
    private LocationClient mLocationClient = null;
    //定位监听
    private MyLocationListener mMyLocationListener = null;
    //经纬度
    private double mCurrentLantitude, mCurrentLongitude;
    //图标
    private BitmapDescriptor mCurrentMarker;
    //定位模式
    private LocationMode mLocationMode;
    //方向监听
    private MyOrientationListener myOrientationListener;

    //保存经纬度
    private List<String> mLanLngList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_runmap);
        initViews();
        initBaiduMap();
        //定位
        initMyLocation();
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

    private void initBaiduMap() {
        mBaiduMap = mMapView.getMap();
        // 开启图层定位
        mBaiduMap.setMyLocationEnabled(true);
        //设置默认角度和放大比例
        MapStatus ms = new MapStatus.Builder().overlook(-5).zoom(17.0f).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(ms));
    }

    private void initMyLocation() {
        // 定位初始化
        mLocationMode = LocationMode.COMPASS;
        mLocationClient = new LocationClient(this);
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setIsNeedAddress(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setPriority(LocationClientOption.NetWorkFirst); // 设置网络定位优先
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        //方向传感器
        myOrientationListener = new MyOrientationListener(RunMapActivity.this);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mXDirection = (int) x;
//                Log.e("my", "mXDirection=" + mXDirection);
            }
        });
        // 开启方向传感器
        myOrientationListener.start();
    }

    private void setListeners() {
        mStopButton.setOnClickListener(this);
        mContinueButton.setOnClickListener(this);
        mOverButton.setOnClickListener(this);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // mapview 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            Toast.makeText(RunMapActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(mXDirection)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            // 设置自定义图标
            mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
            //参数：模式、是否允许自定义图标、图标资源
            MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfigeration(config);
            //经纬度
            mCurrentLantitude = location.getLatitude();
            mCurrentLongitude = location.getLongitude();
            //将地图位置移动到当前位置
            LatLng latLng = new LatLng(mCurrentLantitude, mCurrentLongitude);
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(msu);
            //将经纬度点坐标加入集合
            mLanLngList.add(mCurrentLantitude + "," + mCurrentLongitude + "\n");
        }
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
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(mMyLocationListener);
            mLocationClient.stop();
        }
        myOrientationListener.stop();
        for (int i = 0; i < mLanLngList.size(); i++) {
            Log.e("my", mLanLngList.get(i));
        }
        super.onDestroy();
    }
}
