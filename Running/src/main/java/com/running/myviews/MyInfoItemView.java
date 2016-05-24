package com.running.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.running.android_main.R;

/**
 * Created by ZhangHang on 2016/5/21.
 */
public class MyInfoItemView extends LinearLayout {
    private TextView mLabelTextView, mDataTextView, mRightTextView;
    private String mLabelText, mDataText;

    public MyInfoItemView(Context context) {
        this(context, null);
    }

    public MyInfoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyInfoItemView);
        mLabelText = typedArray.getString(R.styleable.MyInfoItemView_labelText);
        mDataText = typedArray.getString(R.styleable.MyInfoItemView_dataText);
        typedArray.recycle();

        LayoutParams params1 = new LayoutParams(0, LayoutParams.MATCH_PARENT, 3);
        mLabelTextView = new TextView(context);
        mLabelTextView.setLayoutParams(params1);
        mLabelTextView.setText(mLabelText);
        mLabelTextView.setTextSize(24);

        LayoutParams params2 = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 12);
        mDataTextView = new TextView(context);
        mDataTextView.setLayoutParams(params2);
        mDataTextView.setGravity(Gravity.CENTER_VERTICAL);
        mDataTextView.setText(mDataText);
        mDataTextView.setTextColor(Color.parseColor("#C5C5C5"));
        mDataTextView.setTextSize(24);

        LayoutParams params3 = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        mRightTextView = new TextView(context);
        mRightTextView.setLayoutParams(params3);
        mRightTextView.setGravity(Gravity.CENTER_VERTICAL);
        mRightTextView.setText(">");
        mRightTextView.setTextSize(24);
        mRightTextView.setPadding(5, 0, 0, 0);

        addView(mLabelTextView);
        addView(mDataTextView);
        addView(mRightTextView);
        setPadding(20, 15, 20, 15);
        setBackgroundResource(R.drawable.myinfoitem);
    }

    public void setDataText(String dataText) {
        mDataText = dataText;
        mDataTextView.setText(mDataText);
    }

    public String getDataText() {
        return mDataText;
    }

    public void setLabelText(String labelText) {
        mLabelText = labelText;
        mLabelTextView.setText(mLabelText);
    }

    public String getLabelText() {
        return mLabelText;
    }
}