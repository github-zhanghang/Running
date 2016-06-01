package com.running.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.running.android_main.R;


/**
 * Created by C5-0 on 2016/5/26.
 */
public class BestBar extends LinearLayout{
    private TextView mAttrTextView,mDataTextView,mArrowTextView;
    private String mAttrText,mDataText;
    public BestBar(Context context) {
        super(context);
    }

    public BestBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BestBar);
        mAttrText = typedArray.getString(R.styleable.BestBar_best_attrText);
        mDataText = typedArray.getString(R.styleable.BestBar_best_dataText);
        typedArray.recycle();

        LayoutParams params1 = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 4);
        mAttrTextView = new TextView(context);
        mAttrTextView.setLayoutParams(params1);
        mAttrTextView.setText(mAttrText);
        mAttrTextView.setTextSize(20);

        LayoutParams params2 = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 8);
        mDataTextView = new TextView(context);
        mDataTextView.setLayoutParams(params2);
        mDataTextView.setText(mDataText);
        mDataTextView.setTextColor(Color.parseColor("#fa870b"));
        mDataTextView.setTextSize(24);
        mDataTextView.setGravity(Gravity.RIGHT);
        mDataTextView.setPadding(0,0,30,0);

        LayoutParams params3 = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        mArrowTextView = new TextView(context);
        mArrowTextView.setLayoutParams(params3);
        mArrowTextView.setText(">");
        //mArrowTextView.setPadding(10, 0, 0, 0);
        mArrowTextView.setTextColor(Color.GRAY);
        mArrowTextView.setTextSize(24);

        addView(mAttrTextView);
        addView(mDataTextView);
        addView(mArrowTextView);
        setBackgroundColor(Color.WHITE);
    }

    public String getAttrText() {
        return mAttrText;
    }

    public void setAttrText(String AttrText) {
        this.mAttrText = AttrText;
        mAttrTextView.setText(mAttrText);
    }

    public String getDataText() {
        return mDataText;
    }

    public void setDataText(String DataText) {
        this.mDataText = DataText;
        mDataTextView.setText(mDataText);
    }

}
