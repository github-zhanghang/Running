package com.running.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.running.android_main.R;

/**
 * Created by ZhangHang on 2016/5/5.
 */
public class PaobuRightFragment extends Fragment {
    private View mRightView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRightView = inflater.inflate(R.layout.paobu_right, null);
        return mRightView;
    }
}
