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
import android.widget.Toast;

import com.running.android_main.MainActivity;
import com.running.android_main.R;
import com.running.myviews.TopBar;


/**
 * Created by mhdong on 2016/4/29.
 */
public class DongtaiFragment extends Fragment {
    private MainActivity mActivity;
    private View mView;

    private TopBar mTopBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dongtai, null);
        initViews();
        setListeners();
        return mView;
    }

    private void initViews() {
        mActivity = (MainActivity) getActivity();
        mTopBar = (TopBar) mView.findViewById(R.id.dongtai_topbar);
    }

    private void setListeners() {
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                DrawerLayout drawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
                Toast.makeText(mActivity, "发布动态", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
