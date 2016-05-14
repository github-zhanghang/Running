package com.running.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.running.android_main.R;
import com.running.android_main.RunMapActivity;
import com.running.myviews.CircleButton;

/**
 * Created by ZhangHang on 2016/5/5.
 */
public class PaobuLeftFragment extends Fragment {
    private View mLeftView;
    private CircleButton mStartButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLeftView = inflater.inflate(R.layout.paobu_left, null);
        mStartButton = (CircleButton) mLeftView.findViewById(R.id.startRun);
        initListeners();
        return mLeftView;
    }

    private void initListeners() {
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RunMapActivity.class));
            }
        });
    }
}
