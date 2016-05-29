package com.running.myviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by ZhangHang on 2016/5/14.
 */
public class MyStartButton extends Button {
    public MyStartButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = heightSize;
        }
        setMeasuredDimension(widthSize * 1 / 4 - 10, widthSize);
    }

}
