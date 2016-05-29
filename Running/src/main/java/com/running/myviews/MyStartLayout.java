package com.running.myviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by ZhangHang on 2016/5/18.
 */
public class MyStartLayout extends RelativeLayout {
    public MyStartLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        Button view = (Button) getChildAt(getChildCount() - 1);
        int viewWidth = width * 1 / 4 - 20;
        int left = (width - viewWidth) / 2;
        int top = height * 5 / 6 - viewWidth / 2;
        int right = left + viewWidth;
        int bottom = top + viewWidth;
        view.layout(left, top, right, bottom);
    }
}
