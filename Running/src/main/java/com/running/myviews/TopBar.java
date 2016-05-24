package com.running.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.running.android_main.R;

/**
 * Created by ZhangHang on 2016/5/24.
 */
public class TopBar extends RelativeLayout {
    private boolean isLeftImageShow, isRightImageShow,
            isLeftTextShow, isMidTextShow, isRightTextShow;
    private ImageView leftImageView, rightImageView;
    private TextView leftTextView, midTextView, rightTextView;
    private LinearLayout mLinearLayout;

    private Drawable leftImageDrable, rightImageDrable;
    private String leftText, midText, rightText;
    private int leftTextColor, midTextColor, rightTextColor;
    private float leftTextSize, midTextSize, rightTextSize;

    private OnTopbarClickListener listener;

    public TopBar(Context context) {
        this(context, null);
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
        isLeftImageShow = typedArray.getBoolean(R.styleable.TopBar_isLeftImageShow, true);
        isRightImageShow = typedArray.getBoolean(R.styleable.TopBar_isRightImageShow, true);
        isLeftTextShow = typedArray.getBoolean(R.styleable.TopBar_isLeftTextShow, true);
        isMidTextShow = typedArray.getBoolean(R.styleable.TopBar_isMidTextShow, false);
        isRightTextShow = typedArray.getBoolean(R.styleable.TopBar_isRightTextShow, false);
        if (isLeftImageShow) {
            leftImageDrable = typedArray.getDrawable(R.styleable.TopBar_leftImage);
            LayoutParams leftImageParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            leftImageParams.addRule(ALIGN_PARENT_LEFT, TRUE);
            leftImageView = new ImageView(context);
            leftImageView.setLayoutParams(leftImageParams);
            leftImageView.setImageDrawable(leftImageDrable);
            this.addView(leftImageView);
            leftImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onTopbarLeftImageClick(leftImageView);
                    }
                }
            });
        }
        if (isRightImageShow) {
            rightImageDrable = typedArray.getDrawable(R.styleable.TopBar_rightImage);
            LayoutParams rightImageParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            rightImageParams.addRule(ALIGN_PARENT_RIGHT, TRUE);
            rightImageView = new ImageView(context);
            rightImageView.setLayoutParams(rightImageParams);
            rightImageView.setImageDrawable(rightImageDrable);
            this.addView(rightImageView);
            rightImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onTopbarRightImageClick(rightImageView);
                    }
                }
            });
        }
        mLinearLayout = new LinearLayout(context);
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams linearLayoutParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        linearLayoutParams.addRule(CENTER_IN_PARENT, TRUE);
        mLinearLayout.setLayoutParams(linearLayoutParams);
        if (isLeftTextShow) {
            leftText = typedArray.getString(R.styleable.TopBar_leftText);
            leftTextColor = typedArray.getColor(R.styleable.TopBar_leftTextColor, Color.WHITE);
            leftTextSize = typedArray.getDimension(R.styleable.TopBar_leftTextSize, 24);
            leftTextView = new TextView(context);
            leftTextView.setText(leftText);
            leftTextView.setTextSize(leftTextSize);
            leftTextView.setTextColor(leftTextColor);
            mLinearLayout.addView(leftTextView);
        }
        if (isMidTextShow) {
            midText = typedArray.getString(R.styleable.TopBar_midText);
            midTextColor = typedArray.getColor(R.styleable.TopBar_midTextColor, Color.BLACK);
            midTextSize = typedArray.getDimension(R.styleable.TopBar_midTextSize, 29);
            midTextView = new TextView(context);
            LinearLayout.LayoutParams midTextParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            midTextParams.setMargins(6, 0, 6, 0);
            midTextView.setLayoutParams(midTextParams);
            midTextView.setText(midText);
            midTextView.setTextSize(midTextSize);
            midTextView.setTextColor(midTextColor);
            mLinearLayout.addView(midTextView);
        }
        if (isRightTextShow) {
            rightText = typedArray.getString(R.styleable.TopBar_rightText);
            rightTextColor = typedArray.getColor(R.styleable.TopBar_rightTextColor, Color.GRAY);
            rightTextSize = typedArray.getDimension(R.styleable.TopBar_rightTextSize, 24);
            rightTextView = new TextView(context);
            rightTextView.setText(rightText);
            rightTextView.setTextSize(rightTextSize);
            rightTextView.setTextColor(rightTextColor);
            mLinearLayout.addView(rightTextView);
        }
        this.addView(mLinearLayout);
        typedArray.recycle();
        this.setGravity(CENTER_VERTICAL);
        this.setPadding(15, 5, 15, 5);
        this.setBackgroundColor(Color.parseColor("#ff9900"));
    }

    public void setOnTopbarClickListener(OnTopbarClickListener listener) {
        this.listener = listener;
    }

    public interface OnTopbarClickListener {
        void onTopbarLeftImageClick(ImageView imageView);

        void onTopbarRightImageClick(ImageView imageView);
    }

    //Getter和Setter
    public boolean isLeftImageShow() {
        return isLeftImageShow;
    }

    public void setLeftImageShow(boolean leftImageShow) {
        isLeftImageShow = leftImageShow;
        if (leftImageView != null) {
            leftImageView.setVisibility(View.GONE);
        } else {
            leftImageView.setVisibility(View.VISIBLE);
        }
    }

    public boolean isRightImageShow() {
        return isRightImageShow;
    }

    public void setRightImageShow(boolean rightImageShow) {
        isRightImageShow = rightImageShow;
        if (rightImageView != null) {
            rightImageView.setVisibility(View.GONE);
        } else {
            rightImageView.setVisibility(View.VISIBLE);
        }
    }

    public boolean isLeftTextShow() {
        return isLeftTextShow;
    }

    public void setLeftTextShow(boolean leftTextShow) {
        isLeftTextShow = leftTextShow;
        if (leftTextView != null) {
            leftTextView.setVisibility(View.GONE);
        } else {
            leftTextView.setVisibility(View.VISIBLE);
        }
    }

    public boolean isMidTextShow() {
        return isMidTextShow;
    }

    public void setMidTextShow(boolean midTextShow) {
        isMidTextShow = midTextShow;
        if (midTextView != null) {
            midTextView.setVisibility(View.GONE);
        } else {
            midTextView.setVisibility(View.VISIBLE);
        }
    }

    public boolean isRightTextShow() {
        return isRightTextShow;
    }

    public void setRightTextShow(boolean rightTextShow) {
        isRightTextShow = rightTextShow;
        if (rightTextView != null) {
            rightTextView.setVisibility(View.GONE);
        } else {
            rightTextView.setVisibility(View.VISIBLE);
        }
    }

    public ImageView getLeftImageView() {
        return leftImageView;
    }

    public void setLeftImageView(ImageView leftImageView) {
        this.leftImageView = leftImageView;
    }

    public ImageView getRightImageView() {
        return rightImageView;
    }

    public void setRightImageView(ImageView rightImageView) {
        this.rightImageView = rightImageView;
    }

    public TextView getLeftTextView() {
        return leftTextView;
    }

    public void setLeftTextView(TextView leftTextView) {
        this.leftTextView = leftTextView;
    }

    public TextView getMidTextView() {
        return midTextView;
    }

    public void setMidTextView(TextView midTextView) {
        this.midTextView = midTextView;
    }

    public TextView getRightTextView() {
        return rightTextView;
    }

    public void setRightTextView(TextView rightTextView) {
        this.rightTextView = rightTextView;
    }

    public Drawable getLeftImageDrable() {
        return leftImageDrable;
    }

    public void setLeftImageDrable(Drawable leftImageDrable) {
        this.leftImageDrable = leftImageDrable;
        leftImageView.setImageDrawable(leftImageDrable);
    }

    public Drawable getRightImageDrable() {
        return rightImageDrable;
    }

    public void setRightImageDrable(Drawable rightImageDrable) {
        this.rightImageDrable = rightImageDrable;
        rightImageView.setImageDrawable(rightImageDrable);
    }

    public String getLeftText() {
        return leftText;
    }

    public void setLeftText(String leftText) {
        this.leftText = leftText;
        leftTextView.setText(leftText);
    }

    public String getMidText() {
        return midText;
    }

    public void setMidText(String midText) {
        this.midText = midText;
        midTextView.setText(midText);
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
        rightTextView.setText(rightText);
    }

    public int getLeftTextColor() {
        return leftTextColor;
    }

    public void setLeftTextColor(int leftTextColor) {
        this.leftTextColor = leftTextColor;
        leftTextView.setTextColor(leftTextColor);
    }

    public int getMidTextColor() {
        return midTextColor;
    }

    public void setMidTextColor(int midTextColor) {
        this.midTextColor = midTextColor;
        midTextView.setTextColor(midTextColor);
    }

    public int getRightTextColor() {
        return rightTextColor;
    }

    public void setRightTextColor(int rightTextColor) {
        this.rightTextColor = rightTextColor;
        rightTextView.setTextColor(rightTextColor);
    }

    public float getLeftTextSize() {
        return leftTextSize;
    }

    public void setLeftTextSize(float leftTextSize) {
        this.leftTextSize = leftTextSize;
        leftTextView.setTextSize(leftTextSize);
    }

    public float getMidTextSize() {
        return midTextSize;
    }

    public void setMidTextSize(float midTextSize) {
        this.midTextSize = midTextSize;
        midTextView.setTextSize(midTextSize);
    }

    public float getRightTextSize() {
        return rightTextSize;
    }

    public void setRightTextSize(float rightTextSize) {
        this.rightTextSize = rightTextSize;
        rightTextView.setTextSize(rightTextSize);
    }
}
