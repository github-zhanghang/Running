package com.running.android_main;

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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.running.myviews.CircleButton;
import com.running.utils.MyOrientationListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static com.running.android_main.R.id.run_distance_txt;
import static com.running.android_main.R.id.start;

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
    //图标
    private BitmapDescriptor mCurrentMarker;
    //定位模式
    private LocationMode mLocationMode;
    //方向监听
    private MyOrientationListener myOrientationListener;
    //保存经纬度
    private List<String> mPositionList = new ArrayList<>();

    //绘制路线的起点经纬度
    private double mLatitude, mLongitude;
    //起点和终点坐标
    private LatLng mLatLng1, mLatLng2;
    //是否是第一次定位
    private boolean isFirstLoc = true;
    //绘制轨迹时的起点和终点集合
    private List<LatLng> mLatLngList = new ArrayList<>();
    //绘制多边形
    private OverlayOptions mPolygonOption;

    //跑步的距离，时间,速度,卡路里
    private double mDistance;
    private double mTime;
    private double mSpeed;
    private double mCalorie;
    //开始时间,结束时间
    private double startTime, endTime;
    //保留小数点后两位
    DecimalFormat mDecimalFormat = new DecimalFormat(".00");
    //毫秒转成 时:分:秒 格式
    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。

    //体重62kg
    private double mWeight = 62;

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
        option.setPriority(LocationClientOption.GpsFirst); // 设置GPS定位优先
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        //监听方向
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
        //开始定位
        mLocationClient.start();
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
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            //将地图位置移动到当前位置
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(msu);
            //将经纬度点坐标加入集合
            mPositionList.add(mLatitude + "," + mLongitude + "\n");
            drawRoute(mLatitude, mLongitude);
        }
    }

    private void drawRoute(double lantitude, double longitude) {
        if (isFirstLoc) {
            //第一次定位时的时间
            startTime = System.currentTimeMillis();
            mLatLng1 = new LatLng(lantitude, longitude);
            mLatLngList.add(mLatLng1);
            isFirstLoc = false;
        } else {
            //当前时间时间
            endTime = System.currentTimeMillis();
            //因为时区问题有8小时的时差
            mTime = endTime - startTime - 8 * 60 * 60 * 1000;
            mLatLng2 = new LatLng(lantitude, longitude);
            //计算两点距离并加入总时间
            mDistance += DistanceUtil.getDistance(mLatLng1, mLatLng2);
            //计算速度
            mSpeed = mDistance / ((endTime - start) / (1000 * 60 * 60));
            //计算消耗的卡路里
            // 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036
            mCalorie = mWeight * mDistance * 1.036;
            updateData(mDistance, mTime, mSpeed, mCalorie);
            mLatLngList.add(mLatLng2);
            mPolygonOption = new PolygonOptions()
                    .points(mLatLngList)
                    .stroke(new Stroke(10, 0xAA00FF00));
            //在地图上添加多边形Option，用于显示
            mBaiduMap.addOverlay(mPolygonOption);
            //清除起点
            mLatLngList.remove(0);
        }
    }

    private void updateData(double distance, double time, double speed, double calorie) {
        //距离保留两位小数(km)
        mRunDistanceText.setText(mDecimalFormat.format(distance) + " km");
        //时间（HH:mm:ss）
        mRunTimeText.setText(mSimpleDateFormat.format(time));
        //速度保留两位小数(km/h)
        mRunSpeedText.setText(mDecimalFormat.format(speed) + " km/h");
        //卡路里
        mRunCalorieText.setText(mDecimalFormat.format(mCalorie) + " kcal");
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
        //停止位置监听
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(mMyLocationListener);
            mLocationClient.stop();
        }
        //停止方向传感器
        myOrientationListener.stop();
        //输出一下经纬度
        for (int i = 0; i < mPositionList.size(); i++) {
            Log.e("my", mPositionList.get(i));
        }
        super.onDestroy();
    }
}
