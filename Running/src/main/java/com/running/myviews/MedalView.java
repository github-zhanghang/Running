package com.running.myviews;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.running.android_main.R;

/**
 * Created by ZhangHang on 2016/5/26.
 */
public class MedalView extends LinearLayout {
    private ImageView mImageView;
    private TextView mTextView;

    public MedalView(Context context) {
        this(context, null);
    }

    public MedalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMedal(context);
    }

    private void initMedal(Context context) {
        this.setGravity(Gravity.CENTER);
        this.setOrientation(VERTICAL);
        this.setBackgroundColor(Color.WHITE);

        mImageView = new ImageView(context);
        mImageView.setImageResource(R.mipmap.ic_launcher);
        this.addView(mImageView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        mTextView = new TextView(context);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextSize(20);
        this.addView(mTextView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setImageResource(int resId) {
        mImageView.setImageResource(resId);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }
}
