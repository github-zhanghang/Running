package com.running.android_main;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.running.eventandcontext.RongCloudContext;
import com.running.myviews.TopBar;

import javax.security.auth.Destroyable;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.MessageContent;
import io.rong.message.LocationMessage;

public class ChatLocationActivity extends AppCompatActivity {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private BDLocationListener mBDLocationListener;
    private TopBar mTopBar;

    //位置
    private double mLatitude, mLongitude;
    private String mPoi;
    //静态图
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_location);
        initViews();
        boolean b = (boolean) getIntent().getExtras().get("haslocation");
        if (!b){
            startMyLocation();
        }else {
            mTopBar.setRightImageShow(false);
            LocationMessage message = (LocationMessage) getIntent().getExtras().get("location");
            getFriendLocation(message.getLat(),message.getLng());
        }
        clickListener();
    }


    private void initViews() {
        mTopBar = (TopBar) findViewById(R.id.chat_location_topBar);
        mMapView = (MapView) findViewById(R.id.chat_location_map);
        mBaiduMap = mMapView.getMap();
    }
    private void clickListener() {
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {

               ChatLocationActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
                //获取静态图uri
                imageUri = Uri
                        .parse("http://api.map.baidu.com/staticimage/v2").buildUpon()
                        .appendQueryParameter("ak", "ZMPuS2AicciGGjhdoGAuBbBzSzVsZpP6")
                        .appendQueryParameter("width", "240")
                        .appendQueryParameter("height", "240")
                        .appendQueryParameter("zoom", "16")
                        .appendQueryParameter("center", + mLatitude+"," + mLongitude)
                        .build();
                LocationMessage message =
                        LocationMessage.obtain(mLatitude, mLongitude, mPoi, imageUri);
                if (message != null){
                    RongCloudContext.getInstance().
                            getLastLocationCallback().onSuccess(message);
                    RongCloudContext.getInstance().setLastLocationCallback(null);
                    ChatLocationActivity.this.finish();
                }else {
                    RongCloudContext.getInstance().
                            getLastLocationCallback().onFailure("定位失败");
                    ChatLocationActivity.this.finish();
                }


            }
        });
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

                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location));
                //在地图上添加Marker，并显示
                mBaiduMap.addOverlay(option);
                //
                mPoi = bdLocation.getAddrStr();
                Toast.makeText(ChatLocationActivity.this,mPoi
                        , Toast.LENGTH_SHORT).show();


            }
        };



        mLocationClient.registerLocationListener(mBDLocationListener);
        mLocationClient.start();
    }

    private void getFriendLocation(double latitude, double longitude) {
        LatLng point = new LatLng(latitude, longitude);
        MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location));
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

    }

    private void destroy() {
        if (mLocationClient!=null){
            mLocationClient.unRegisterLocationListener(mBDLocationListener);
            mMapView.onDestroy();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();
    }
}
