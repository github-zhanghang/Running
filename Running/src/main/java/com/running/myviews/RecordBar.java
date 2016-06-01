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
 * Created by C5-0 on 2016/5/26.
 */
public class RecordBar extends LinearLayout{

    private ImageView mImageView;
    private TextView mDataTextView,mArrowTextView;
    private Drawable mIconDrawable;
    private String mDataText;

    public RecordBar(Context context) {
        super(context);
    }

    public RecordBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecordBar);
        mIconDrawable=typedArray.getDrawable(R.styleable.RecordBar_re_imageView);
        mDataText = typedArray.getString(R.styleable.RecordBar_re_dataText);
        typedArray.recycle();

        LayoutParams params1=new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        params1.gravity = Gravity.CENTER_VERTICAL;
        mImageView=new ImageView(context);
        mImageView.setLayoutParams(params1);
        mImageView.setImageDrawable(mIconDrawable);

        LayoutParams params2 = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 8);
        mDataTextView = new TextView(context);
        mDataTextView.setLayoutParams(params2);
        mDataTextView.setText(mDataText);
        mDataTextView.setTextSize(22);

        LayoutParams params3 = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        mArrowTextView = new TextView(context);
        mArrowTextView.setLayoutParams(params3);
        mArrowTextView.setText(">");
        mArrowTextView.setTextColor(Color.parseColor("#fa870b"));
        mArrowTextView.setTextSize(24);

        addView(mImageView);
        addView(mDataTextView);
        addView(mArrowTextView);
        setBackgroundColor(Color.WHITE);
    }
}
