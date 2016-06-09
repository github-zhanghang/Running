package com.running.android_main;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnTrackListener;
import com.running.model.HistoryTrackData;
import com.running.utils.GsonService;

import java.util.ArrayList;
import java.util.List;

public class HistoryDetailActivity extends AppCompatActivity {
    private MyApplication mApplication;

    private MapView mHisDetailMapView;
    private long serviceId = 117790;// 鹰眼服务ID
    private OnTrackListener trackListener;
    private BaiduMap mBaiduMap;
    private MapStatusUpdate msUpdate;
    private BitmapDescriptor bmStart, bmEnd;
    private MarkerOptions startMarker, endMarker;
    private PolylineOptions polyline;

    private int startTime = 0;
    private int endTime = 0;

    private LBSTraceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_history_detail);
        mApplication = (MyApplication) getApplication();
        mHisDetailMapView = (MapView) findViewById(R.id.map_his_detail);
        mClient = new LBSTraceClient(HistoryDetailActivity.this);
        //initOnTrackListener();
        //queryHistoryTrack();
    }

    private void queryHistoryTrack() {
        // entity标识
        String entityName = "" + mApplication.getUserInfo().getUid();
        // 是否返回精简的结果（0 : 否，1 : 是）
        int simpleReturn = 0;
        // 开始时间
        if (startTime == 0) {
            startTime = (int) (System.currentTimeMillis() / 1000 - 12 * 60 * 60);
        }
        if (endTime == 0) {
            endTime = (int) (System.currentTimeMillis() / 1000);
        }
        // 分页大小
        int pageSize = 1000;
        // 分页索引
        int pageIndex = 1;

        mClient.queryHistoryTrack(serviceId, entityName, simpleReturn, startTime, endTime,
                pageSize,
                pageIndex,
                trackListener);
    }

    /**
     * 初始化OnTrackListener
     */
    private void initOnTrackListener() {
        trackListener = new OnTrackListener() {
            // 请求失败回调接口
            @Override
            public void onRequestFailedCallback(String arg0) {
                // TODO Auto-generated method stub
                Looper.prepare();
                Toast.makeText(HistoryDetailActivity.this, "track请求失败回调接口消息 : " + arg0, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            // 查询历史轨迹回调接口
            @Override
            public void onQueryHistoryTrackCallback(String arg0) {
                // TODO Auto-generated method stub
                super.onQueryHistoryTrackCallback(arg0);
                showHistoryTrack(arg0);
            }
        };
    }

    /**
     * 显示历史轨迹
     */
    public void showHistoryTrack(String historyTrack) {

        HistoryTrackData historyTrackData = GsonService.parseJson(historyTrack,
                HistoryTrackData.class);
        List<LatLng> latLngList = new ArrayList<LatLng>();
        if (historyTrackData != null && historyTrackData.getStatus() == 0) {
            if (historyTrackData.getListPoints() != null) {
                latLngList.addAll(historyTrackData.getListPoints());
            }
            // 绘制历史轨迹
            drawHistoryTrack(latLngList);
        }
    }

    /**
     * 绘制历史轨迹
     *
     * @param points
     */
    public void drawHistoryTrack(final List<LatLng> points) {
        // 绘制新覆盖物前，清空之前的覆盖物
        mBaiduMap.clear();
        if (points == null || points.size() == 0) {
            Looper.prepare();
            Toast.makeText(HistoryDetailActivity.this, "当前查询无轨迹点", Toast.LENGTH_SHORT).show();
            Looper.loop();
            resetMarker();
        } else if (points.size() > 1) {
            LatLng llC = points.get(0);
            LatLng llD = points.get(points.size() - 1);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(llC).include(llD).build();
            msUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);

            bmStart = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
            bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
            // 添加起点图标
            startMarker = new MarkerOptions()
                    .position(points.get(points.size() - 1)).icon(bmStart)
                    .zIndex(9).draggable(true);
            // 添加终点图标
            endMarker = new MarkerOptions().position(points.get(0))
                    .icon(bmEnd).zIndex(9).draggable(true);
            // 添加路线（轨迹）
            polyline = new PolylineOptions().width(10)
                    .color(Color.RED).points(points);
            addMarker();
        }
    }

    /**
     * 重置覆盖物
     */
    private void resetMarker() {
        startMarker = null;
        endMarker = null;
        polyline = null;
    }

    /**
     * 添加覆盖物
     */
    public void addMarker() {
        if (null != msUpdate) {
            mBaiduMap.setMapStatus(msUpdate);
        }
        if (null != startMarker) {
            mBaiduMap.addOverlay(startMarker);
        }
        if (null != endMarker) {
            mBaiduMap.addOverlay(endMarker);
        }
        if (null != polyline) {
            mBaiduMap.addOverlay(polyline);
        }
    }
}
