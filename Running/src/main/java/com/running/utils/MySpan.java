package com.running.utils;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.running.android_main.R;
import com.running.beans.SecondCommentBean;

/**
 * Created by ldd on 2016/5/30.
 * 二级回复的TextView的格式
 */
public class MySpan extends ClickableSpan {

    public static final int NO_COLOR = -206;
    private Context mContext;
    private int id;
    private String mContent;
    private SecondCommentBean mBean;

    private int mColor;
    private OnClickListener mOnClickListener;

    public MySpan(Context context, int color, OnClickListener onClickListener) {
        mContext = context;
        mColor = color;
        mOnClickListener = onClickListener;
    }

    public MySpan(Context context, OnClickListener onClickListener) {
        this(context, NO_COLOR,onClickListener);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public SecondCommentBean getBean() {
        return mBean;
    }

    public void setBean(SecondCommentBean bean) {
        mBean = bean;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        if (mColor == NO_COLOR) {
            ds.setColor(mContext.getResources().getColor(R.color.text));
        } else {
            ds.setColor(mColor);
        }
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
        mOnClickListener.onClick(widget,this);
    }

    public interface OnClickListener<T extends MySpan>{
        void onClick(View view,T span);
    }
}
