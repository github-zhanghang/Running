package com.running.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.running.android_main.R;
import com.running.android_main.RunMapActivity;
import com.running.android_main.RunTargetActivity;
import com.running.myviews.MyStartButton;

/**
 * Created by ZhangHang on 2016/5/5.
 */
public class PaobuRightFragment extends Fragment implements View.OnClickListener {
    public static final int REQUEST_CODE = 0;
    private View mRightView;
    private MyStartButton mStartButton;
    private TextView mTitleText, mTargetText, mUnitsText;

    private TranslateAnimation mTranslateAnimation;

    private static final String mDisUnits = "公里";
    private static final String mTimeUnits = "分钟";
    private static final String mCalUnits = "大卡";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRightView = inflater.inflate(R.layout.paobu_right, null);
        initViews();
        initListeners();
        initAnimation();
        return mRightView;
    }

    private void initAnimation() {
        //上下跳动
        mTranslateAnimation = new TranslateAnimation(0, 0, 0, 15);
        mTranslateAnimation.setDuration(200);
        mTranslateAnimation.setRepeatCount(Animation.INFINITE);
        mTranslateAnimation.setRepeatMode(Animation.REVERSE);
    }

    private void initViews() {
        mStartButton = (MyStartButton) mRightView.findViewById(R.id.startRun2);
        mTitleText = (TextView) mRightView.findViewById(R.id.title);
        mTargetText = (TextView) mRightView.findViewById(R.id.data);
        mUnitsText = (TextView) mRightView.findViewById(R.id.units);
    }

    private void initListeners() {
        mStartButton.setOnClickListener(this);
        mTargetText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startRun2:
                startActivity(new Intent(getContext(), RunMapActivity.class));
                break;
            case R.id.data:
                Intent intent = new Intent(getContext(), RunTargetActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REQUEST_CODE && requestCode == REQUEST_CODE) {
            if (data != null) {
                String text = data.getStringExtra("target");
                if (text.endsWith(mDisUnits)) {
                    mUnitsText.setText(mDisUnits);
                    mTargetText.setText(text.substring(0, text.length() - 2));
                } else if (text.endsWith(mTimeUnits)) {
                    mUnitsText.setText(mTimeUnits);
                    mTargetText.setText(text.substring(0, text.length() - 2));
                } else if (text.endsWith(mCalUnits)) {
                    mUnitsText.setText(mCalUnits);
                    mTargetText.setText(text.substring(0, text.length() - 2));
                } else if (text.endsWith("半程马拉松")) {
                    mUnitsText.setText(mDisUnits);
                    mTargetText.setText("21.1");
                } else if (text.endsWith("马拉松")) {
                    mUnitsText.setText(mDisUnits);
                    mTargetText.setText("42.2");
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mTitleText.startAnimation(mTranslateAnimation);
    }

    @Override
    public void onPause() {
        super.onPause();
        mTitleText.clearAnimation();
    }

}
