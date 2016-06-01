package com.running.myviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.running.android_main.R;

/**
 * Created by ldd on 2016/5/31.
 * 自定义ToggleButton
 */
public class MyToggleButton extends View {

    private boolean currentState = false; // 开关当前的状态
    private Bitmap backgroundOn, backgroundOff; // 开关的背景
    private Bitmap slideBackground; // 滑动块的背景
    private int currentX; // 当前x轴的偏移量
    private boolean isSliding = false; // 是否正在滑动中
    private OnToggleStateChangeListener mOnToggleStateChangeListener; // 用户设置的监听事件

    public MyToggleButton(Context context) {
        super(context);
    }

    public MyToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBitmap();
    }

    /**
     * 初始化开关图片
     */
    private void initBitmap() {
        backgroundOn = BitmapFactory.decodeResource(getResources(),
                R.drawable.switch_on);
        backgroundOff = BitmapFactory.decodeResource(getResources(),
                R.drawable.switch_off);
        slideBackground = BitmapFactory.decodeResource(getResources(),
                R.drawable.switch_button);
    }

    /**
     * 设置当前控件的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置开关的宽和高
        setMeasuredDimension(backgroundOn.getWidth(),backgroundOn.getHeight());
    }

    /**
     * 绘制当前的控件
     */
    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制背景
        canvas.drawBitmap(backgroundOff, 0, 0, null);

        // 绘制滑动块

        if (isSliding) { // 正在滑动中
            int left = currentX - slideBackground.getWidth() / 2;

            if (left < 0) { // 当前超出了左边界, 赋值为0
                left = 0;
            } else if (left > (backgroundOff.getWidth() - slideBackground
                    .getWidth())) {
                // 当前超出了右边界, 赋值为: 背景的宽度 - 滑动块的宽度
                left = backgroundOff.getWidth() - slideBackground.getWidth();
            }

            canvas.drawBitmap(slideBackground, left, 0, null);
        } else {
            if (currentState) {
                // 绘制开的状态
                canvas.drawBitmap(backgroundOn, 0, 0, null);
                canvas.drawBitmap(slideBackground, 0, 0, null);
            } else {
                // 绘制关的状态
                int left = backgroundOn.getWidth()
                        - slideBackground.getWidth();
                canvas.drawBitmap(slideBackground, left, 0, null);

            }
        }
        super.onDraw(canvas);
    }

    /**
     * 捕获用户操作的事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = (int) event.getX();
                isSliding = true;
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                isSliding = false;
                currentX = (int) event.getX();

                int center = backgroundOn.getWidth() / 2;

                // 当前最新的状态
                boolean state = currentX < center;

                // if(currentX > center) {
                // // 设置开关为开的状态
                // currentState = true;
                // } else {
                // // 设置开关为关的状态
                // currentState = false;
                // }

                // 如果两个状态不一样并且监听事件不为null
                if (currentState != state && mOnToggleStateChangeListener != null) {
                    // 调用用户的回调事件
                    mOnToggleStateChangeListener.onToggleStateChange(state);
                }
                currentState = state;
                break;
            default:
                break;
        }
        invalidate(); // 此方法被调用会使onDraw方法重绘
        return true;
    }

    /**
     * 设置开关的状态
     *
     * @param state
     */
    public void setToggleState(boolean state) {
        currentState = state;
    }

    public boolean getToogleState() {
        return currentState;
    }

    public void setOnToggleStateChangeListener(
            OnToggleStateChangeListener listener) {
        mOnToggleStateChangeListener = listener;
    }

    //监听事件接口
    public interface OnToggleStateChangeListener {
        void onToggleStateChange(boolean state);
    }
}
