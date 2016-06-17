package com.running.android_main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.running.adapters.MyFragmentAdapter;
import com.running.beans.UserInfo;
import com.running.fragments.DongtaiFragment;
import com.running.fragments.FaxianFragment;
import com.running.fragments.PaobuFragment;
import com.running.fragments.XiaoxiFragment;
import com.running.myviews.CircleImageView;
import com.running.myviews.NoScrollViewPager;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static final String juheKey = "614e1a0a60edc2c5f8e91dd885df7eeb";
    private MyApplication mApplication;
    private UserInfo mUserInfo;
    //定位
    private LocationClient mLocationClient;
    private BDLocationListener mBDLocationListener;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NoScrollViewPager mViewPager;
    private MyFragmentAdapter mAdapter;
    private List<Fragment> mList;
    private FragmentManager mFragmentManager;
    private PaobuFragment mPaobuFragment;
    private FaxianFragment mFaxianFragment;
    private DongtaiFragment mDongtaiFragment;
    private XiaoxiFragment mXiaoxiFragment;
    private RadioGroup mRadioGroup;

    private CircleImageView mUserImage;
    private TextView mUserNickNameText;
    //设置、温度、城市
    private TextView mSettingTextView, mTempTextView, mCityTextView;
    //请求队列
    private RequestQueue mRequestQueue = NoHttp.newRequestQueue(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_main);
        mApplication = (MyApplication) getApplication();
        mUserInfo = mApplication.getUserInfo();
        mApplication.addActivity(this);
        initViews();
        //定位
        startLocating();
        initFragments();
        initViewPager();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        mUserInfo = mApplication.getUserInfo();
        mUserNickNameText.setText(mUserInfo.getNickName());
        Glide.with(MainActivity.this)
                .load(mUserInfo.getImageUrl())
                .centerCrop()
                .error(R.drawable.fail)
                .into(mUserImage);
    }

    private void initViews() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        mUserImage = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.user_image);
        mUserNickNameText = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_nickname);
        mSettingTextView = (TextView) navigationView.findViewById(R.id.setting);
        mCityTextView = (TextView) navigationView.findViewById(R.id.local_city);
        mTempTextView = (TextView) navigationView.findViewById(R.id.local_temperature);
    }

    private void initFragments() {
        mList = new ArrayList<>();
        mFragmentManager = getSupportFragmentManager();
        mPaobuFragment = new PaobuFragment();
        mFaxianFragment = new FaxianFragment();
        mDongtaiFragment = new DongtaiFragment();
        mXiaoxiFragment = new XiaoxiFragment();
        mList.add(mPaobuFragment);
        mList.add(mFaxianFragment);
        mList.add(mDongtaiFragment);
        mList.add(mXiaoxiFragment);
    }

    private void initViewPager() {
        mViewPager = (NoScrollViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setOffscreenPageLimit(4);
        mAdapter = new MyFragmentAdapter(mFragmentManager, mList);
        mViewPager.setAdapter(mAdapter);
    }

    private void startLocating() {
        mLocationClient = new LocationClient(MainActivity.this);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);        //是否打开GPS
        option.setCoorType("bd09ll");       //设置返回值的坐标类型。
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mBDLocationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                double latitude = bdLocation.getLatitude();
                double longitude = bdLocation.getLongitude();
                String city = bdLocation.getCity();
                Log.e("my", "latitude=" + latitude + ";longitude=" + longitude + ";city=" + city);
                mCityTextView.setText(city);
                //获取温度
                getTemperature(city);
                mUserInfo.setLatitude(latitude);
                mUserInfo.setLongitude(longitude);
                //更新数据库位置信息
                updateUserLocation(latitude, longitude);
            }
        };
        mLocationClient.registerLocationListener(mBDLocationListener);
        mLocationClient.start();
    }

    private OnResponseListener onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            if (what == 2) {
                String result = response.get();
                //Log.e("my", "result=" + result);
                try {
                    JSONObject resultObject = new JSONObject(result);
                    String resultcode = resultObject.getString("resultcode");
                    if (resultcode.equals("200")) {
                        JSONObject weatherObject = resultObject.getJSONObject("result");
                        JSONObject skObject = weatherObject.getJSONObject("sk");
                        String temp = skObject.getString("temp");
                        mTempTextView.setText(temp + "℃");
                    } else {
                        Toast.makeText(MainActivity.this, "获取气温失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            if (what == 2) {
                Toast.makeText(MainActivity.this, "获取气温失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish(int what) {
        }
    };

    private void updateUserLocation(double latitude, double longitude) {
        String url = MyApplication.HOST + "changeUserInfoServlet";
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
        request.add("type", "location");
        request.add("account", mUserInfo.getAccount());
        request.add("latitude", "" + latitude);
        request.add("longitude", "" + longitude);
        mRequestQueue.add(1, request, onResponseListener);
        mRequestQueue.start();
    }

    private void getTemperature(String city) {
        String url = "http://v.juhe.cn/weather/index";
        Request<String> request = NoHttp.createStringRequest(url, RequestMethod.GET);
        request.add("cityname", city);
        request.add("key", juheKey);
        mRequestQueue.add(2, request, onResponseListener);
        mRequestQueue.start();
    }

    private void initListener() {
        mViewPager.addOnPageChangeListener(new NoScrollViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                resetViewPager(checkedId);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        mUserImage.setOnClickListener(this);
        mSettingTextView.setOnClickListener(this);
    }

    /**
     * 监听后退键
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this).setTitle("确认退出吗？")
                    .setIcon(R.drawable.ic_dialog)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_record) {
            startActivity(new Intent(MainActivity.this, RecordActivity.class));
        } else if (id == R.id.nav_medal) {
            startActivity(new Intent(MainActivity.this, MedalActivity.class));
        } else if (id == R.id.nav_ranklist) {
            startActivity(new Intent(MainActivity.this, RankActivity.class));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_image:
                startActivity(new Intent(MainActivity.this, MyDetailsActivity.class));
                break;
            case R.id.setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
    }

    private void resetViewPager(int id) {
        switch (id) {
            case R.id.run:
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.find:
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.news:
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.chat:
                mViewPager.setCurrentItem(3, false);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.unRegisterLocationListener(mBDLocationListener);
        mLocationClient = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 123){
            mViewPager.setCurrentItem(3,false);
        }
    }
}
