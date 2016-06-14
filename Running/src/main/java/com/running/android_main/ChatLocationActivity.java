package com.running.android_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

public class ChatLocationActivity extends AppCompatActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private BDLocationListener mBDLocationListener;

    //位置
    private double mLatitude, mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_location);
        initMap();
        boolean b = (boolean) getIntent().getExtras().get("haslocation");
       /* if (!b){
           Toast.makeText(ChatLocationActivity.this, "000000000000000000", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(ChatLocationActivity.this, "++++++++++++", Toast.LENGTH_SHORT).show();
            MessageContent messageContent = (MessageContent) getIntent().getExtras().get("location");
        }*/
       // startMyLocation();
    }

    private void initMap() {
        mMapView = (MapView) findViewById(R.id.chat_location_map);
        mBaiduMap = mMapView.getMap();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void startMyLocation() {
        mLocationClient = new LocationClient(ChatLocationActivity.this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mBDLocationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                mLatitude = bdLocation.getLatitude();
                mLongitude = bdLocation.getLongitude();
                LatLng point = new LatLng(mLatitude, mLongitude);
                MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
            }
        };
        mLocationClient.registerLocationListener(mBDLocationListener);
        mLocationClient.start();
    }

    private void getFriendLocation(double latitude, double longitude) {
        LatLng point = new LatLng(latitude, longitude);
        MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        mMapView.onDestroy();
    }
}
