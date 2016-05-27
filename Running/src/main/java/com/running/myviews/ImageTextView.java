package com.running.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
public class ImageTextView extends LinearLayout {
    private ImageView mImageView;
    private Drawable mDrawable;
    private TextView mTextView;
    private int mTextColor;
    private float mTextSize;
    private String mText;

    public ImageTextView(Context context) {
        this(context, null);
    }

    public ImageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageTextView);
        mDrawable = typedArray.getDrawable(R.styleable.ImageTextView_image);
        mText = typedArray.getString(R.styleable.ImageTextView_text);
        mTextColor = typedArray.getColor(R.styleable.ImageTextView_textColor, Color.BLACK);
        mTextSize = typedArray.getDimension(R.styleable.ImageTextView_textSize, 20);
        typedArray.recycle();
        initMedal(context);
    }

    private void initMedal(Context context) {
        this.setGravity(Gravity.CENTER);
        this.setOrientation(VERTICAL);

        mImageView = new ImageView(context);
        if (mDrawable != null) {
            mImageView.setImageDrawable(mDrawable);
        }
        this.addView(mImageView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        mTextView = new TextView(context);
        mTextView.setText(mText);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextSize(mTextSize);
        mTextView.setTextColor(mTextColor);
        this.addView(mTextView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setImageResource(int resId) {
        mImageView.setImageResource(resId);
    }

    public void setText(String text) {
        mText = text;
        mTextView.setText(mText);
    }

    public void setTextColor(int color) {
        mTextColor = color;
        mTextView.setTextColor(mTextColor);
    }

    public void setTextSize(float size) {
        mTextSize = size;
        mTextView.setTextSize(size);
    }
}
