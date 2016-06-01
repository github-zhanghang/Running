package com.running.myviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class NoScrollView extends ScrollView {
    private int downX;
    private int downY;
    private int mTouchSlop;


    public NoScrollView(Context context) {
        super(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public NoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public NoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action= ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                downX= (int) ev.getRawX();
                downY= (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY= (int) ev.getRawY();
                if (Math.abs(moveY-downY)>mTouchSlop){
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
