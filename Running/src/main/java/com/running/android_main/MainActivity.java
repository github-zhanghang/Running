package com.running.android_main;

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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.running.adapters.MyFragmentAdapter;
import com.running.fragments.DongtaiFragment;
import com.running.fragments.FaxianFragment;
import com.running.fragments.PaobuFragment;
import com.running.fragments.XiaoxiFragment;
import com.running.myviews.CircleImageView;
import com.running.myviews.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private MyApplication mApplication;
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

    private String mUserName, mNickName, mCityName;
    private CircleImageView mUserImage;
    private TextView mUserNickNameText;
    //设置、温度、城市
    private TextView mSettingTextView, mTempTextView, mCityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_main);
        mApplication = (MyApplication) getApplication();
        mApplication.addActivity(this);
        //定位
        startLocating();
        initViews();
        initTemperature();
        initFragments();
        initViewPager();
        initListener();
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
                mApplication.setCity(city);
                mCityTextView.setText(mCityName);
                mApplication.getUserInfo().setLatitude(latitude);
                mApplication.getUserInfo().setLongitude(longitude);
            }
        };
        mLocationClient.registerLocationListener(mBDLocationListener);
        mLocationClient.start();
    }

    //获取温度
    private void initTemperature() {
    }

    private void initViews() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView = (NavigationView) findViewById(R.id.nav_view);
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
}
