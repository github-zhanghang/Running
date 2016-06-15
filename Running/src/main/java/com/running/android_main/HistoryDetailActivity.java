package com.running.android_main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.running.beans.History;
import com.running.model.HistoryTrackData;
import com.running.myviews.TopBar;
import com.running.utils.GsonService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryDetailActivity extends AppCompatActivity {
    private MyApplication mApplication;
    private TopBar mTopBar;
    private MapView mHisDetailMapView;
    private long serviceId = 117790;// 鹰眼服务ID
    private OnTrackListener trackListener;
    private BaiduMap mBaiduMap;
    private LBSTraceClient mClient;
    private MapStatusUpdate msUpdate;
    private BitmapDescriptor bmStart, bmEnd;
    private MarkerOptions startMarker, endMarker;
    private PolylineOptions polyline;

    private int startTime = 0;
    private int endTime = 0;

    TextView dateTextView, distanceTextView, timeTextView, speedTextView, walkTextView, calorieTextView,targetTextView,completeTextView,tarTextView,staTextView;
    LinearLayout targetLinearLayout;
    History history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_history_detail);
        mApplication = (MyApplication) getApplication();
        mClient = new LBSTraceClient(HistoryDetailActivity.this);

        getData();
        initView();
        setData();
        initOnTrackListener();
        queryHistoryTrack();
    }

    private void getData() {
        // 接收到网址
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            history = (History) bundle.getSerializable("history");
            //Log.e("taozihistory", history.toString() );
        }
    }

    private void initView() {
        mTopBar = (TopBar) findViewById(R.id.history_detail_topbar);
        mHisDetailMapView = (MapView) findViewById(R.id.map_his_detail);
        mBaiduMap = mHisDetailMapView.getMap();
        dateTextView = (TextView) findViewById(R.id.starttime_his_detail);
        distanceTextView = (TextView) findViewById(R.id.distance_his_detail);
        timeTextView = (TextView) findViewById(R.id.time_his_detail);
        speedTextView = (TextView) findViewById(R.id.speed_his_detail);
        walkTextView = (TextView) findViewById(R.id.walk_his_detail3);
        calorieTextView = (TextView) findViewById(R.id.calorie_his_detail);
        targetTextView= (TextView) findViewById(R.id.target_text);
        completeTextView= (TextView) findViewById(R.id.complete_text);
        targetLinearLayout= (LinearLayout) findViewById(R.id.taget_view);
        tarTextView=(TextView) findViewById(R.id.tar_text);
        staTextView=(TextView) findViewById(R.id.sta_text);

        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                HistoryDetailActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
    }

    private void setData() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
        dateTextView.setText(dateFormat.format(history.getRunstarttime()));
        distanceTextView.setText(history.getRundistance() + "");
        timeTextView.setText(dateFormat2.format(history.getRuntime() - 28800000));
        speedTextView.setText(history.getRunspeed() + "");
        walkTextView.setText(history.getStepcount() + "");
        calorieTextView.setText(history.getCalories() + "");
        if (history.getComplete()==(-1)){
            targetLinearLayout.setVisibility(View.GONE);
            targetTextView.setVisibility(View.GONE);
            tarTextView.setVisibility(View.GONE);
            staTextView.setVisibility(View.GONE);
            completeTextView.setVisibility(View.GONE);

        }else {
            targetTextView.setText(history.getTarget());
            if (history.getComplete()==0){
                completeTextView.setText("未完成");
            }else {
                completeTextView.setText("已完成");
            }
        }
    }

    private void queryHistoryTrack() {
        // entity标识
        String entityName = "" + mApplication.getUserInfo().getUid() + history.getRunstarttime();
        // 是否返回精简的结果（0 : 否，1 : 是）
        int simpleReturn = 0;

        startTime = (int) (history.getRunstarttime() / 1000);
        endTime = startTime + (int) (history.getRuntime() / 1000);
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
                Log.e("my", "trace=" + arg0);
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

            bmStart = BitmapDescriptorFactory.fromResource(R.drawable.ic_location);
            bmEnd = BitmapDescriptorFactory.fromResource(R.drawable.ic_location);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.onDestroy();
    }
}
