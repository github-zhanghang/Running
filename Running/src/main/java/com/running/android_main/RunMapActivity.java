package com.running.android_main;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.Trace;
import com.running.beans.UserInfo;
import com.running.model.RealtimeTrackData;
import com.running.utils.GsonService;
import com.running.utils.MyOrientationListener;
import com.running.utils.MyStepListener;
import com.running.utils.ScreenShot;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class RunMapActivity extends Activity implements View.OnClickListener {
    private MyApplication mApplication;
    private UserInfo mUserInfo;
    private static final String mPath = MyApplication.HOST + "runDataServlet";
    private RequestQueue requestQueue = NoHttp.newRequestQueue();

    private int gatherInterval = 3;  //位置采集周期 (s)
    private int packInterval = 5;  //打包周期 (s)
    private String entityName = null;  // entity标识
    private long serviceId = 117790;// 鹰眼服务ID
    private int traceType = 2;  //轨迹服务类型
    private OnStartTraceListener mStartTraceListener = null;  //开启轨迹服务监听器
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private OnEntityListener mEntityListener = null;
    private RefreshThread mRefreshThread = null;  //刷新地图线程以获取实时点
    private MapStatusUpdate mMapStatusUpdate = null;
    private BitmapDescriptor mBitmapDescriptor;  //图标
    private OverlayOptions mOverlayOptions;  //覆盖物
    private List<LatLng> mLatLngList = new ArrayList<LatLng>();  //定位点的集合
    private PolylineOptions mPolylineOptions = null;  //路线覆盖物
    private Trace mTrace;  // 实例化轨迹服务
    private LBSTraceClient mTraceClient;  // 实例化轨迹服务客户端
    //传感器，计步
    private MyStepListener mStepListener;

    //是否停止
    private boolean isStop = false;
    private TextView mRunTimeText, mRunSpeedText, mRunDistanceText, mRunCalorieText;
    private Button mStopButton, mContinueButton, mOverButton;
    private String mImgPath;

    private double mWeight;
    //跑步的距离、速度,卡路里,步数,时间
    private double mDistance, mSpeed, mCalorie;
    private long mStepCount, mStartTime, mTime;
    //毫秒转成 时:分:秒 格式
    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。
    //保留小数点后两位
    DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

    //方向
    private float mDirection;
    private MyOrientationListener mOrientationListener;
    //目标
    private String mTarget;
    //目标数据
    private int mTargetData;
    //目标单位
    private String mTargetUnit;
    //目标是否完成,1表示完成，0表示未完成，-1表示未设置目标
    private int mComplete = 1;

    //我的勋章
    private List<Integer> mMedalIndex = new ArrayList<>();

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                mTime += 1000;
                //减去8小时的时差
                mRunTimeText.setText(mSimpleDateFormat.format(mTime + 1000 - 28800000));
                mRunDistanceText.setText(mDecimalFormat.format(mDistance) + " km");
                mSpeed = mDistance * 60 * 60 / mTime;
                mRunSpeedText.setText(mDecimalFormat.format(mSpeed) + " km/h");
                mCalorie = mDistance * mWeight * 1.036;
                mRunCalorieText.setText(mDecimalFormat.format(mCalorie) + " 大卡");
                mHandler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runmap);
        //获取目标
        Intent intent = getIntent();
        mTarget = intent.getStringExtra("target");
        if (mTarget != null) {
            mTargetData = Integer.parseInt(mTarget.substring(0, mTarget.length() - 2));
            mTargetUnit = mTarget.substring(mTarget.length() - 2, mTarget.length());
        } else {
            mComplete = -1;
        }

        mApplication = (MyApplication) getApplication();
        mUserInfo = mApplication.getUserInfo();
        mWeight = mUserInfo.getWeight();

        init();
        initOnEntityListener();
        mTraceClient.setOnEntityListener(mEntityListener);
        // 开启轨迹服务
        initOnStartTraceListener();
        mTraceClient.setOnStartTraceListener(mStartTraceListener);
        mTraceClient.startTrace(mTrace, mStartTraceListener);
        //开启计步
        initStepListener();
        mStepListener.start();
        initButtonListener();
        //获取方向
        mOrientationListener = new MyOrientationListener(RunMapActivity.this);
        initOrientationListener();
        mOrientationListener.start();
        //获取我的勋章
        getMyMedal();
    }


    private void init() {
        mMapView = (MapView) findViewById(R.id.mapview);
        mBaiduMap = mMapView.getMap();
        //地图移动到当前位置
        double latitude = mApplication.getUserInfo().getLatitude();
        double longitude = mApplication.getUserInfo().getLongitude();
        if (latitude != 0 && longitude != 0) {
            LatLng point = new LatLng(latitude, longitude);
            MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location));
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
            mHandler.sendEmptyMessage(1);
        }

        mStartTime = System.currentTimeMillis();
        //账号+当前时间来充当实体名
        entityName = "" + mApplication.getUserInfo().getUid() + mStartTime;
        //实例化轨迹服务客户端
        mTraceClient = new LBSTraceClient(this);
        //实例化轨迹服务
        mTrace = new Trace(this, serviceId, entityName, traceType);
        //设置位置采集和打包周期
        mTraceClient.setInterval(gatherInterval, packInterval);
        mStepListener = new MyStepListener(this);

        mRunTimeText = (TextView) findViewById(R.id.run_time_txt);
        mRunSpeedText = (TextView) findViewById(R.id.run_speed_txt);
        mRunDistanceText = (TextView) findViewById(R.id.run_distance_txt);
        mRunCalorieText = (TextView) findViewById(R.id.run_calorie_txt);
        mStopButton = (Button) findViewById(R.id.run_stop);
        mContinueButton = (Button) findViewById(R.id.run_continue);
        mOverButton = (Button) findViewById(R.id.run_over);
    }

    // 初始化设置实体状态监听器
    private void initOnEntityListener() {
        //实体状态监听器
        mEntityListener = new OnEntityListener() {
            @Override
            public void onRequestFailedCallback(String arg0) {
            }

            @Override
            public void onQueryEntityListCallback(String arg0) {
                //查询实体集合回调函数，此时调用实时轨迹方法
                showRealtimeTrack(arg0);
                //Log.e("my", "请求成功信息:" + arg0);
            }
        };
    }

    //追踪开始
    private void initOnStartTraceListener() {
        // 实例化开启轨迹服务回调接口
        mStartTraceListener = new OnStartTraceListener() {
            // 开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTraceCallback(int arg0, String arg1) {
                if (arg0 == 0 || arg0 == 10006) {
                    startRefreshThread(!isStop);
                }
            }

            // 轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
            @Override
            public void onTracePushCallback(byte arg0, String arg1) {
            }
        };
    }

    private void initStepListener() {
        mStepListener.setOnStepListener(new MyStepListener.OnStepListener() {
            @Override
            public void onOnStepListener(long step) {
                if (!isStop) {
                    mStepCount = step;
                    Log.e("my", "mStepCount=" + mStepCount);
                } else {
                    mStepListener.setStep(mStepCount);
                }
            }
        });
    }

    private void initOrientationListener() {
        mOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mDirection = x;
            }
        });
    }

    private void initButtonListener() {
        mStopButton.setOnClickListener(this);
        mContinueButton.setOnClickListener(this);
        mOverButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.run_stop:
                isStop = true;
                mHandler.removeMessages(1);
                mStopButton.setVisibility(View.GONE);
                mContinueButton.setVisibility(View.VISIBLE);
                mOverButton.setVisibility(View.VISIBLE);
                break;
            case R.id.run_continue:
                isStop = false;
                mHandler.sendEmptyMessage(1);
                mStopButton.setVisibility(View.VISIBLE);
                mContinueButton.setVisibility(View.GONE);
                mOverButton.setVisibility(View.GONE);
                break;
            case R.id.run_over:
                isStop = true;
                mHandler.removeMessages(1);
                //将跑步数据插入数据库
                saveRunData();
                //判断是否获得勋章
                checkMedal();
                //跑步结束弹框
                showDialog();
                break;
        }
    }

    /**
     * 判断是否获得勋章
     */
    private void checkMedal() {
        if (mDistance >= 100) {
            //超级马拉松
            if (mMedalIndex.contains(6)) {
                return;
            } else {
                obtainMedal(6);
            }
        } else if (mDistance >= 42.2) {
            //全程马拉松
            if (mMedalIndex.contains(5)) {
                return;
            } else {
                obtainMedal(5);
            }
        } else if (mDistance >= 21.1) {
            //半程马拉松
            if (mMedalIndex.contains(4)) {
                return;
            } else {
                obtainMedal(4);
            }
        } else if (mDistance >= 10) {
            //10公里
            if (mMedalIndex.contains(3)) {
                return;
            } else {
                obtainMedal(3);
            }
        } else if (mDistance >= 5) {
            //5公里
            if (mMedalIndex.contains(2)) {
                return;
            } else {
                obtainMedal(2);
            }
        } else {
            //开始运动
            if (mMedalIndex.contains(1)) {
                return;
            } else {
                obtainMedal(1);
            }
        }
    }

    /**
     * 保存获得的勋章
     *
     * @param i 勋章号
     */
    private void obtainMedal(int i) {
        String obtainMedalPath = MyApplication.HOST + "medalServlet";
        Request<String> request = NoHttp.createStringRequest(obtainMedalPath, RequestMethod.POST);
        request.add("type", "insert");
        request.add("uid", ((MyApplication) getApplication()).getUserInfo().getUid());
        request.add("mid", i);
        requestQueue.add(i + 2, request, onResponseListener);
        requestQueue.start();
    }

    /**
     * 获取我的勋章
     */
    private void getMyMedal() {
        String medalPath = MyApplication.HOST + "medalServlet";
        Request<String> request = NoHttp.createStringRequest(medalPath, RequestMethod.POST);
        request.add("type", "query");
        request.add("uid", ((MyApplication) getApplication()).getUserInfo().getUid());
        requestQueue.add(2, request, onResponseListener);
        requestQueue.start();
    }

    /**
     * 保存跑步数据
     */
    private void saveRunData() {
        //判断是否达到目标
        if (mTarget != null && mTargetUnit.equals("公里")) {
            if (mDistance >= mTargetData) {
                mComplete = 1;
            } else {
                mComplete = 0;
            }
        } else if (mTarget != null && mTargetUnit.equals("大卡")) {
            if (mCalorie >= mTargetData) {
                mComplete = 1;
            } else {
                mComplete = 0;
            }
        } else if (mTarget != null && mTargetUnit.equals("分钟")) {
            if (mTime >= mTargetData * 60 * 1000) {
                mComplete = 1;
            } else {
                mComplete = 0;
            }
        }
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("uid", mApplication.getUserInfo().getUid());
        request.add("distance", mDistance);
        request.add("runtime", mTime);
        request.add("speed", mSpeed);
        request.add("calorie", mCalorie);
        request.add("stepcount", mStepCount);
        request.add("starttime", mStartTime);
        request.add("target", mTarget);
        request.add("complete", mComplete);
        requestQueue.add(1, request, onResponseListener);
        requestQueue.start();
    }

    private OnResponseListener onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            if (result == null && result.equals("null")) {
                Toast.makeText(RunMapActivity.this, "提交数据失败", Toast.LENGTH_SHORT).show();
                return;
            }
            if (what == 1) {
            } else if (what == 2) {
                String mid[] = result.split(",");
                for (int i = 0; i < mid.length; i++) {
                    int id = Integer.parseInt(mid[i]);
                    mMedalIndex.add(id);
                    Log.e("myMedal:", "" + id);
                }
            } else if (what == 3) {
                Toast.makeText(RunMapActivity.this, "获得开始运动勋章", Toast.LENGTH_SHORT).show();
            } else if (what == 4) {
                Toast.makeText(RunMapActivity.this, "获得5公里勋章", Toast.LENGTH_SHORT).show();
            } else if (what == 5) {
                Toast.makeText(RunMapActivity.this, "获得10公里勋章", Toast.LENGTH_SHORT).show();
            } else if (what == 6) {
                Toast.makeText(RunMapActivity.this, "获得半程马拉松勋章", Toast.LENGTH_SHORT).show();
            } else if (what == 7) {
                Toast.makeText(RunMapActivity.this, "获得全称马拉松勋章", Toast.LENGTH_SHORT).show();
            } else if (what == 7) {
                Toast.makeText(RunMapActivity.this, "获得超级马拉松勋章", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            Toast.makeText(RunMapActivity.this, "提交数据失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish(int what) {
        }
    };

    private void showDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(RunMapActivity.this)
                .setTitle("分享")
                .setMessage("跑步结束，和朋友分享一下吧!")
                .setPositiveButton("分享", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShareSDK.initSDK(RunMapActivity.this);
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
                                Bitmap bgBitmap = ScreenShot.takeScreenShot(RunMapActivity.this);
                                //将两个Bitmap合并成一个
                                bitmap = ScreenShot.toConformBitmap(bgBitmap, bitmap);
                                //临时保存在本地
                                mImgPath = ScreenShot.saveBitmap(bitmap, RunMapActivity.this);
                                oks.setImagePath(mImgPath);
                                // 启动分享GUI
                                oks.show(RunMapActivity.this);
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

    //轨迹刷新线程
    private class RefreshThread extends Thread {
        protected boolean refresh = true;

        public void run() {
            while (refresh) {
                if (!isStop) {
                    queryRealtimeTrack();
                }
                try {
                    Thread.sleep(packInterval * 1000);
                } catch (InterruptedException e) {
                    System.out.println("线程休眠失败");
                }
            }
        }
    }

    // 查询实时线路
    private void queryRealtimeTrack() {
        String entityName = this.entityName;
        String columnKey = "";
        int returnType = 0;
        int activeTime = 0;
        int pageSize = 10;
        int pageIndex = 1;
        this.mTraceClient.queryEntityList(
                serviceId,
                entityName,
                columnKey,
                returnType,
                activeTime,
                pageSize,
                pageIndex,
                mEntityListener
        );
    }


    //展示实时线路图
    protected void showRealtimeTrack(String realtimeTrack) {
        if (mRefreshThread == null || !mRefreshThread.refresh) {
            return;
        }
        //数据以JSON形式存取
        RealtimeTrackData realtimeTrackData = GsonService.parseJson(realtimeTrack, RealtimeTrackData.class);
        if (realtimeTrackData != null && realtimeTrackData.getStatus() == 0) {
            LatLng latLng = realtimeTrackData.getRealtimePoint();
            if (latLng != null) {
                mLatLngList.add(latLng);
                drawRealtimePoint(latLng);
            }
            //计算距离，单位km
            if (mLatLngList.size() > 2) {
                mDistance += DistanceUtil.getDistance(
                        mLatLngList.get(mLatLngList.size() - 2),
                        mLatLngList.get(mLatLngList.size() - 1)) / 1000;
            }
        }
    }

    // 画出实时线路点
    private void drawRealtimePoint(LatLng point) {
        mBaiduMap.clear();
        MapStatus mapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(16)
                .overlook(-3)
                .rotate(mDirection)
                .build();
        mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);

        mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_location);
        mOverlayOptions = new MarkerOptions().position(point)
                .icon(mBitmapDescriptor).zIndex(9).draggable(true);
        if (mLatLngList.size() >= 2 && mLatLngList.size() <= 1000) {
            mPolylineOptions = new PolylineOptions().width(10).color(Color.RED).points(mLatLngList);
        }
        addMarker();
    }

    private void addMarker() {
        if (mMapStatusUpdate != null) {
            mBaiduMap.animateMapStatus(mMapStatusUpdate);
        }
        if (mPolylineOptions != null) {
            mBaiduMap.addOverlay(mPolylineOptions);
        }
        if (mOverlayOptions != null) {
            mBaiduMap.addOverlay(mOverlayOptions);
        }
    }

    //启动刷新线程
    private void startRefreshThread(boolean isStart) {
        if (mRefreshThread == null) {
            mRefreshThread = new RefreshThread();
        }
        mRefreshThread.refresh = isStart;
        if (isStart) {
            if (!mRefreshThread.isAlive()) {
                mRefreshThread.start();
            }
        } else {
            mRefreshThread = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (!isStop) {
            AlertDialog alertDialog = new AlertDialog
                    .Builder(RunMapActivity.this)
                    .setMessage("是否结束跑步?")
                    .setNegativeButton("结束", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            isStop = false;
                            mHandler.removeMessages(1);
                            showDialog();
                        }
                    })
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            alertDialog.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStop = true;
        mTraceClient.stopTrace(mTrace, null);
        mTraceClient.onDestroy();
        mStepListener.stop();
        mOrientationListener.stop();
        mMapView.onDestroy();
        mMapView = null;
    }
}