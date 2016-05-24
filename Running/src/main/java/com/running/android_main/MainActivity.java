package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.running.adapters.MyFragmentAdapter;
import com.running.fragments.DongtaiFragment;
import com.running.fragments.FaxianFragment;
import com.running.fragments.PaobuFragment;
import com.running.fragments.XiaoxiFragment;
import com.running.myviews.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MyApplication mApplication;
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

    private String mUserName;
    private ImageView mUserImage;
    private TextView mUserNickNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_main);
        mApplication = (MyApplication) getApplication();
        mApplication.addActivity(this);
        //获取上个页面传过来的账号
        mUserName = getIntent().getStringExtra("username");
        initDrawerLayout();
        initFragments();
        initViews();
        initViewPager();
        initListener();
    }

    private void initViews() {
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        mUserImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_image);
        mUserNickNameText = (TextView) findViewById(R.id.user_nickname);
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

        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyDetailsActivity.class));
            }
        });
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

    private void initViewPager() {
        mViewPager = (NoScrollViewPager) findViewById(R.id.main_viewpager);
        mViewPager.setOffscreenPageLimit(4);
        mAdapter = new MyFragmentAdapter(mFragmentManager, mList);
        mViewPager.setAdapter(mAdapter);
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

    private void initDrawerLayout() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        } else if (id == R.id.nav_ranklist) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.removeActivity(this);
    }

}
