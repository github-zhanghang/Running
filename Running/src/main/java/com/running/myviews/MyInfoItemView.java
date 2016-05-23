package com.running.myviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.running.android_main.R;

/**
 * Created by ZhangHang on 2016/5/21.
 */
public class MyInfoItemView extends LinearLayout {
    private TextView mLabelTextView, mRightTextView;
    private String mLabelText, mDataText;
    private EditText mDataEditText;
    private boolean mDataTextEditable;
    private InputMethodManager mInputMethodManager;

    public MyInfoItemView(Context context) {
        this(context, null);
    }

    public MyInfoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyInfoItemView);
        mLabelText = typedArray.getString(R.styleable.MyInfoItemView_labelText);
        mDataText = typedArray.getString(R.styleable.MyInfoItemView_dataText);
        mDataTextEditable = typedArray.getBoolean(R.styleable.MyInfoItemView_dataEditable, false);
        typedArray.recycle();

        LayoutParams params1 = new LayoutParams(0, LayoutParams.MATCH_PARENT, 3);
        params1.setMargins(0, 22, 0, 0);
        mLabelTextView = new TextView(context);
        mLabelTextView.setLayoutParams(params1);
        mLabelTextView.setText(mLabelText);
        mLabelTextView.setTextSize(24);

        LayoutParams params2 = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 10);
        params2.setMargins(0, 5, 0, 0);
        mDataEditText = new EditText(context);
        mDataEditText.setLayoutParams(params2);
        mDataEditText.setGravity(Gravity.CENTER_VERTICAL);
        mDataEditText.setText(mDataText);
        mDataEditText.setTextColor(Color.parseColor("#C5C5C5"));
        mDataEditText.setTextSize(24);
        mDataEditText.setBackgroundColor(Color.TRANSPARENT);
        mDataEditText.setEnabled(mDataTextEditable);

        LayoutParams params3 = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        mRightTextView = new TextView(context);
        mRightTextView.setLayoutParams(params3);
        mRightTextView.setGravity(Gravity.CENTER_VERTICAL);
        mRightTextView.setText(">");
        mRightTextView.setTextSize(24);
        mRightTextView.setPadding(5, 0, 0, 0);

        addView(mLabelTextView);
        addView(mDataEditText);
        addView(mRightTextView);
        setPadding(20, 5, 5, 5);
        setBackgroundResource(R.drawable.myinfoitem);
        setFocusable(true);
        mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void setDataText(String dataText) {
        mDataText = dataText;
        mDataEditText.setText(mDataText);
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

    public boolean isDataTextEditable() {
        return mDataTextEditable;
    }

    public void setDataTextEditable(boolean dataTextEditaable) {
        mDataTextEditable = dataTextEditaable;
        mDataEditText.setEnabled(mDataTextEditable);
    }

    public void dataTextRequestFocus() {
        mDataEditText.requestFocus();
        mInputMethodManager.showSoftInput(mDataEditText, InputMethodManager.SHOW_FORCED);
    }
}
