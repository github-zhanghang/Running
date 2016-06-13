package com.running.myviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ZhangHang on 2016/5/14.
 */
public class MyStartView extends View {
    private Paint mPaint;
    private Path mPath;
    //控件高度
    private float mHeight;
    private float topHeight;
    //控件宽度
    private float width;
    //按钮宽度
    private float buttonWidth;

    public MyStartView(Context context) {
        super(context, null);
    }

    public MyStartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#eb4f38"));
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(10);
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight() * 5 / 6;
        width = getMeasuredWidth();
        buttonWidth = width * 1 / 4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        topHeight = 2 * 1.0f / 3 * mHeight;
        mPath.moveTo(0, 0);
        mPath.lineTo(0, topHeight);
        mPath.lineTo((width - buttonWidth) / 2, mHeight);

        float radius = buttonWidth / 2;
        float left = (width - buttonWidth) / 2;
        float top = mHeight - radius;
        float right = left + buttonWidth;
        float bottom = top + buttonWidth;
        RectF rectF = new RectF(left, top, right, bottom);
        mPath.arcTo(rectF, 180, 180, false);

        mPath.lineTo((width - buttonWidth) / 2 + buttonWidth, mHeight);
        mPath.lineTo(width, topHeight);
        mPath.lineTo(width, 0);
        mPath.lineTo(0, 0);
        canvas.drawPath(mPath, mPaint);
    }
}
