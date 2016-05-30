package com.running.android_main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.running.utils.MyOrientationListener;
import com.running.utils.MyStepListener;
import com.running.utils.ScreenShot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.running.android_main.R.id.run_distance_txt;

public class RunMapActivity extends AppCompatActivity implements View.OnClickListener {
    private MyApplication mApplication;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private TextView mRunTimeText, mRunSpeedText, mRunDistanceText, mRunCalorieText;
    private Button mStopButton, mContinueButton, mOverButton;
    private Activity mContext;
    private WifiManager wifiManager;

    //方向
    private float mDirection;
    private LocationClient mLocationClient = null;
    //定位监听
    private MyLocationListener mMyLocationListener = null;
    //BaiduMap配置
    private MyLocationConfiguration mMyLocationConfiguration;
    //图标
    private BitmapDescriptor mCurrentMarker;
    //定位信息
    private MyLocationData mLocationData;
    //定位模式
    private LocationMode mLocationMode;
    //方向监听
    private MyOrientationListener myOrientationListener;
    //计步
    private MyStepListener mStepListener;
    //保存经纬度
    private List<String> mPositionList = new ArrayList<>();

    //绘制路线的上一个点经纬度
    private double mPreLatitude, mPreLongitude;
    //绘制路线的当前点经纬度
    private double mLatitude, mLongitude;
    //起点和终点坐标
    private LatLng mLatLng1, mLatLng2;
    //是否是第一次定位
    private boolean isFirstLoc = true;
    //绘制轨迹时的起点和终点集合
    private List<LatLng> mLatLngList = new ArrayList<>();
    //绘制多边形
    private OverlayOptions mPolygonOption;

    //跑步的距离，时间,速度,卡路里,步数
    private double mDistance;
    private double mTime;
    private double mSpeed;
    private double mCalorie;
    //当前的确定步数
    private long mStepCount;
    //上一次的确定步数
    private long mPreStepCount;
    //当前临时获取的步数,可能包含无效的步数
    private long mTempStepCount = 1;
    //开始时间,结束时间
    private double startTime, endTime;
    //保留小数点后两位
    DecimalFormat mDecimalFormat = new DecimalFormat("0.00");
    //毫秒转成 时:分:秒 格式
    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。

    //体重62kg
    private double mWeight = 62;
    //是否停止
    private boolean isStop;
    //位置
    private String mAddress = "北京";
    private String mImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_runmap);
        mApplication = (MyApplication) getApplication();
        mApplication.addActivity(this);
        mContext = this;
        openWIFI();
        initViews();
        initBaiduMap();
        //初始化定位
        initMyLocation();
        // 开启方向传感器
        myOrientationListener.start();
        //开始计步
        mStepListener.start();
        //开始定位
        mLocationClient.start();
        setListeners();
    }

    private void initViews() {
        mMapView = (MapView) findViewById(R.id.mapview);
        mRunTimeText = (TextView) findViewById(R.id.run_time_txt);
        mRunSpeedText = (TextView) findViewById(R.id.run_speed_txt);
        mRunDistanceText = (TextView) findViewById(run_distance_txt);
        mRunCalorieText = (TextView) findViewById(R.id.run_calorie_txt);
        mStopButton = (Button) findViewById(R.id.run_stop);
        mContinueButton = (Button) findViewById(R.id.run_continue);
        mOverButton = (Button) findViewById(R.id.run_over);
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
        //注册监听
        mLocationClient.registerLocationListener(mMyLocationListener);
        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式:高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);// 打开gps
        option.setTimeOut(5000);
        //可选，默认false，设置是否需要地址信息
        option.setIsNeedAddress(true);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setLocationNotify(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        //定位间隔，4秒一次
        option.setScanSpan(4000);
        mLocationClient.setLocOption(option);
        //监听方向
        myOrientationListener = new MyOrientationListener(mContext);
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float direction) {
                mDirection = (int) direction;
            }
        });
        //步数监听
        mStepListener = new MyStepListener(mContext);
        mStepListener.setOnStepListener(new MyStepListener.OnStepListener() {
            @Override
            public void onOnStepListener(long step) {
                mTempStepCount = step;
            }
        });
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
            // 获取经纬度
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            mLocationData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(mDirection)
                    .latitude(mLatitude)
                    .longitude(mLongitude)
                    .build();
            mAddress = location.getAddrStr();
            // 设置定位数据
            mBaiduMap.setMyLocationData(mLocationData);
            // 设置自定义图标
            mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
            //参数：模式、是否允许自定义图标、图标资源
            mMyLocationConfiguration = new MyLocationConfiguration(mLocationMode, true, mCurrentMarker);
            mBaiduMap.setMyLocationConfigeration(mMyLocationConfiguration);
            //将地图位置移动到当前位置
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(
                    new LatLng(mLatitude, mLongitude)));
            //如果步数没有变化，表示没有跑步的加速度，则不绘制轨迹，
            if (mTempStepCount == mPreStepCount) {
                Toast.makeText(mContext, "未检测到跑步动作", Toast.LENGTH_SHORT).show();
                return;
            }
            //如果位置没改变，类似以原地摇手机，则重置计步数据为变化之前的
            Toast.makeText(mContext, "当前=" + mLatitude + "," + mLongitude + ";\n" +
                    "上一次=" + mPreLatitude + "," + mPreLongitude, Toast.LENGTH_SHORT).show();
            if (mLatitude == mPreLatitude && mLongitude == mPreLongitude) {
                Toast.makeText(mContext, "未检测到距离变化", Toast.LENGTH_SHORT).show();
                mStepListener.setStep(mStepCount);
                return;
            }
            mPreStepCount = mStepCount;
            mStepCount = mTempStepCount;
            mPreLatitude = mLatitude;
            mPreLongitude = mLongitude;
            //保存经纬度坐标
            savePosition(mLatitude, mLongitude);
            //绘制路线
            drawRoute(mLatitude, mLongitude);
        }
    }

    private void savePosition(double latitude, double longitude) {
        mPositionList.add(latitude + "," + longitude + "\n");
        //每到100条信息就保存到文件
        if (mPositionList.size() == 100) {
            //。。。。。。保存到文件。。。。。。
            mPositionList = new ArrayList<>();//保存后清空list
        }
    }

    private void drawRoute(double lantitude, double longitude) {
        if (isFirstLoc) {
            //第一次定位时的时间
            startTime = System.currentTimeMillis();
            mLatLng1 = new LatLng(lantitude, longitude);
            mLatLngList.add(mLatLng1);
            isFirstLoc = false;
            return;
        }
        //当前时间
        endTime = System.currentTimeMillis();
        mLatLng2 = new LatLng(lantitude, longitude);
        mLatLngList.add(mLatLng2);
        //绘制多边形路径
        mPolygonOption = new PolygonOptions()
                .points(mLatLngList)
                .fillColor(Color.TRANSPARENT)
                .stroke(new Stroke(10, 0xAA00FF00));
        mBaiduMap.addOverlay(mPolygonOption);

        //计算时间，因为时区问题有8小时的时差
        mTime = endTime - startTime - 28800000;
        //计算两点距离并加入总时间
        mDistance += DistanceUtil.getDistance(mLatLngList.get(0), mLatLngList.get(1)) / 1000;
        Log.e("my", "mTime=" + mTime + ";mDistance=" + mDistance);
        //计算平均速度
        mSpeed = mDistance * 360000 / mTime;
        //计算消耗的卡路里，跑步热量（kcal）＝体重（kg）×距离（公里）×1.036
        mCalorie = mWeight * mDistance * 1.036;
        //更新数据
        updateData(mDistance, mTime, mSpeed, mCalorie);
        //清除起点
        mLatLngList.remove(0);
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
        //步数
        Log.e("my", "mStepCount=" + mStepCount);
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
        if (!isStop) {
            mLocationClient.stop();
            myOrientationListener.stop();
            mStepListener.stop();
            isStop = true;
            Toast.makeText(mContext, "stop", Toast.LENGTH_SHORT).show();
        }
    }

    private void handcontinue() {
        mStopButton.setVisibility(View.VISIBLE);
        mContinueButton.setVisibility(View.GONE);
        mOverButton.setVisibility(View.GONE);
        if (isStop) {
            mStepListener.start();
            mLocationClient.start();
            myOrientationListener.start();
            isStop = false;
        }
        Toast.makeText(mContext, "continue", Toast.LENGTH_SHORT).show();
    }

    private void handover() {
        mLocationClient.unRegisterLocationListener(mMyLocationListener);
        mLocationClient.stop();
        mStepListener.stop();
        myOrientationListener.stop();
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle("分享")
                .setMessage("跑步结束，和朋友分享一下吧!")
                .setPositiveButton("分享", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShareSDK.initSDK(mContext);
                        final OnekeyShare oks = new OnekeyShare();
                        oks.disableSSOWhenAuthorize();
                        oks.setTitle("一键分享测试");
                        oks.setText("我是分享文本");
                        oks.setSite(getString(R.string.app_name));
                        oks.setSiteUrl("http://www.baidu.com");
                        oks.setUrl("http://www.baidu.com");
                        oks.setCallback(new PlatformActionListener() {
                            @Override
                            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                                RunMapActivity.this.finish();
                            }

                            @Override
                            public void onError(Platform platform, int i, Throwable throwable) {
                                Toast.makeText(RunMapActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancel(Platform platform, int i) {
                                Toast.makeText(RunMapActivity.this, "分享取消", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //地图截图
                        mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
                            @Override
                            public void onSnapshotReady(Bitmap bitmap) {
                                //去掉状态栏的截图
                                Bitmap bgBitmap = ScreenShot.takeScreenShot(mContext);
                                //将两个Bitmap合并成一个
                                bitmap = ScreenShot.toConformBitmap(bgBitmap, bitmap);
                                //临时保存在本地
                                mImgPath = ScreenShot.saveBitmap(bitmap, mContext);
                                oks.setImagePath(mImgPath);
                                // 启动分享GUI
                                oks.show(mContext);
                            }
                        });
                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RunMapActivity.this.finish();
                    }
                }).create();
        Window window = alertDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.DialogAnim);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    private void openWIFI() {
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
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
        mStepListener.stop();
        mApplication.removeActivity(this);
        ShareSDK.stopSDK();
        super.onDestroy();
    }
}
