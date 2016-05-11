package com.running.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.running.adapters.XiaoxiFragmentAdapter;
import com.running.android_main.R;
import com.running.android_main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhdong on 2016/4/29.
 */
public class XiaoxiFragment extends Fragment {
    private MainActivity mActivity;
    private View mView;
    private ViewPager mViewPager;
    private List<Fragment> mList;
    private XiaoxiFragmentAdapter mAdapter;
    private FragmentManager mFragmentManager;
    private XiaoxiLeftFragment mXiaoxiLeftFragment;
    private XiaoxiRightFragment mXiaoxiRightFragment;

    private TextView mLeftTextView, mRightTextView;
    private ImageView mLeftImage, mRightImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.xiaoxi, null);
        initViews();
        initFragments();
        initViewPager();
        setListeners();
        return mView;
    }

    private void initViews() {
        mActivity = (MainActivity) getActivity();
        mLeftTextView = (TextView) mView.findViewById(R.id.leftText);
        mLeftTextView.setText("聊天");
        mRightTextView = (TextView) mView.findViewById(R.id.rightText);
        mRightTextView.setText("好友");
        mLeftImage = (ImageView) mView.findViewById(R.id.leftImgage);
        mRightImage = (ImageView) mView.findViewById(R.id.rightImage);
        mRightImage.setImageResource(R.mipmap.ic_launcher);
    }

    private void setListeners() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mLeftTextView.setTextColor(Color.WHITE);
                        mRightTextView.setTextColor(Color.GRAY);
                        break;
                    case 1:
                        mLeftTextView.setTextColor(Color.GRAY);
                        mRightTextView.setTextColor(Color.WHITE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mLeftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        mRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "添加好友", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViewPager() {
        mViewPager = (ViewPager) mView.findViewById(R.id.xiaoxiViewpager);
        mAdapter = new XiaoxiFragmentAdapter(mFragmentManager, mList);
        mViewPager.setAdapter(mAdapter);
    }

    private void initFragments() {
        mList = new ArrayList<>();
        mFragmentManager = mActivity.getSupportFragmentManager();
        mXiaoxiLeftFragment = new XiaoxiLeftFragment();
        mXiaoxiRightFragment = new XiaoxiRightFragment();
        mList.add(mXiaoxiLeftFragment);
        mList.add(mXiaoxiRightFragment);
    }
}
