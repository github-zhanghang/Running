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
    private TextView labelTextView, dataTextView, rightTextView;
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

        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 3);
        labelTextView = new TextView(context);
        labelTextView.setLayoutParams(params1);
        labelTextView.setText(mLabelText);
        labelTextView.setTextSize(24);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 10);
        dataTextView = new TextView(context);
        dataTextView.setLayoutParams(params2);
        dataTextView.setGravity(Gravity.CENTER_VERTICAL);
        dataTextView.setText(mDataText);
        dataTextView.setTextSize(24);

        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        rightTextView = new TextView(context);
        rightTextView.setLayoutParams(params3);
        rightTextView.setGravity(Gravity.CENTER_VERTICAL);
        rightTextView.setText(">");
        rightTextView.setTextSize(24);
        rightTextView.setPadding(5, 0, 0, 0);

        addView(labelTextView);
        addView(dataTextView);
        addView(rightTextView);
        setPadding(20, 10, 5, 10);
        setBackgroundColor(Color.WHITE);
    }

    public void setDataText(String dataText) {
        mDataText = dataText;
    }

    public String getDataText() {
        return mDataText;
    }

    public void setLabelText(String labelText) {
        mLabelText = labelText;
    }

    public String getLabelText() {
        return mLabelText;
    }
}
