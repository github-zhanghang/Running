package com.lost.zou.scaleruler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.lost.zou.scaleruler.view.ScaleRulerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zoubo on 16/3/16.
 * 横向滚动刻度尺测试类
 */

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.scaleWheelView_height)
    ScaleRulerView mHeightWheelView;
    @Bind(R.id.tv_user_height_value)
    TextView mHeightValue;

    @Bind(R.id.scaleWheelView_weight)
    ScaleRulerView mWeightWheelView;
    @Bind(R.id.tv_user_weight_value)
    TextView mWeightValue;

    private float mHeight = 170;
    private float mMaxHeight = 220;
    private float mMinHeight = 100;


    private float mWeight = 65;
    private float mMaxWeight = 200;
    private float mMinWeight = 25;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);  //依赖注入

        init();
    }

    private void init() {
        mHeightWheelView.initViewParam(mHeight, mMaxHeight, mMinHeight);

        mHeightWheelView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mHeightValue.setText((int) value + "");
                mHeight = value;
            }
        });

        mWeightWheelView.initViewParam(mWeight, mMaxWeight, mMinWeight);
        mWeightWheelView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mWeightValue.setText(value + "");

                mWeight = value;
            }
        });
    }

    @OnClick(R.id.btn_choose_result)
    void onChooseResultClick() {
        Toast.makeText(this, "选择身高： " + mHeight + " 体重： " + mWeight, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
