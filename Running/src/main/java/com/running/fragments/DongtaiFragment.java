package com.running.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.running.android_main.R;
import com.running.android_main.MainActivity;


/**
 * Created by mhdong on 2016/4/29.
 */
public class DongtaiFragment extends Fragment {
    private MainActivity mActivity;
    private View mView;
    private TextView mLeftTextView, mMidTextView, mRightTextView;
    private ImageView mLeftImage, mRightImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dongtai, null);
        initViews();
        setListeners();
        return mView;
    }

    private void setListeners() {
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
                Toast.makeText(mActivity, "发布动态", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        mActivity = (MainActivity) getActivity();
        mLeftTextView = (TextView) mView.findViewById(R.id.leftText);
        mLeftTextView.setText("好友动态");
        mMidTextView = (TextView) mView.findViewById(R.id.midText);
        mMidTextView.setVisibility(View.GONE);
        mRightTextView = (TextView) mView.findViewById(R.id.rightText);
        mRightTextView.setVisibility(View.GONE);
        mLeftImage = (ImageView) mView.findViewById(R.id.leftImgage);
        mRightImage = (ImageView) mView.findViewById(R.id.rightImage);
        mRightImage.setImageResource(R.mipmap.ic_launcher);
    }
}
