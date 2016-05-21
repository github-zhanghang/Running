package com.running.android_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MyDetailsActivity extends AppCompatActivity {
    private ImageView mBackImage;
    private TextView mTitleText;

    private ImageView mUserImaage;
    private TextView mAlertText;

    private View mNickItem, mHeightItem, mWeightItem, mSexItem, mAddressItem;

    private TextView mNickLabel, mHeightLabel, mWeightLabel, mSexLabel, mAddressLabel;
    private TextView mNickData, mHeightData, mWeightData, mSexData, mAddressData;
    private ImageView mNickImg, mHeightImg, mWeightImg, mSexImg, mAddressImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydetails);
        initViews();
        initData();

    }

    private void initViews() {
        mBackImage = (ImageView) findViewById(R.id.myheader_back);
        mTitleText = (TextView) findViewById(R.id.myheader_title);
        mUserImaage = (ImageView) findViewById(R.id.details_uimg);
        mAlertText = (TextView) findViewById(R.id.details_ulabel);

        mNickItem = findViewById(R.id.nick_item);
        mNickLabel = (TextView) mNickItem.findViewById(R.id.detail_label);
        mNickData = (TextView) mNickItem.findViewById(R.id.detail_data);
        mNickImg = (ImageView) mNickItem.findViewById(R.id.detail_img);

        mHeightItem = findViewById(R.id.height_item);
        mHeightLabel = (TextView) mHeightItem.findViewById(R.id.detail_label);
        mHeightData = (TextView) mHeightItem.findViewById(R.id.detail_data);
        mHeightImg = (ImageView) mHeightItem.findViewById(R.id.detail_img);

        mWeightItem = findViewById(R.id.weight_item);
        mWeightLabel = (TextView) mWeightItem.findViewById(R.id.detail_label);
        mWeightData = (TextView) mWeightItem.findViewById(R.id.detail_data);
        mWeightImg = (ImageView) mWeightItem.findViewById(R.id.detail_img);

        mSexItem = findViewById(R.id.sex_item);
        mSexLabel = (TextView) mSexItem.findViewById(R.id.detail_label);
        mSexData = (TextView) mSexItem.findViewById(R.id.detail_data);
        mSexImg = (ImageView) mSexItem.findViewById(R.id.detail_img);

        mAddressItem = findViewById(R.id.address_item);
        mAddressLabel = (TextView) mAddressItem.findViewById(R.id.detail_label);
        mAddressData = (TextView) mAddressItem.findViewById(R.id.detail_data);
        mAddressImg = (ImageView) mAddressItem.findViewById(R.id.detail_img);
    }

    private void initData() {
        mNickLabel.setText("昵称");
        mNickData.setText("桃子");

        mHeightLabel.setText("身高");
        mHeightData.setText("160 厘米");

        mWeightLabel.setText("体重");
        mWeightData.setText("45 公斤");

        mSexLabel.setText("性别");
        mSexData.setText("美女");

        mAddressLabel.setText("地区");
        mAddressData.setText("河北 石家庄");
    }
}
