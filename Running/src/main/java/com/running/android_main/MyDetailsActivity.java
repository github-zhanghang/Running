package com.running.android_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.running.myviews.MyInfoItemView;

public class MyDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private MyApplication mApplication;
    private ImageView mBackImage;
    private TextView mTitleText;

    private View mDetailsHeadView;
    private ImageView mUserImage;
    private TextView mUserAccount;

    private MyInfoItemView mNickItem, mHeightItem, mWeightItem,
            mSexItem, mBirthdayItem, mAddressItem, mSignatureItem;

    private Button mSaveInfoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydetails);
        mApplication = (MyApplication) getApplication();
        mApplication.addActivity(this);
        initViews();
        initData();
        initListeners();
    }

    private void initViews() {
        mBackImage = (ImageView) findViewById(R.id.myheader_back);
        mTitleText = (TextView) findViewById(R.id.myheader_title);

        mDetailsHeadView = findViewById(R.id.details_head);
        mUserImage = (ImageView) mDetailsHeadView.findViewById(R.id.details_uimg);
        mUserAccount = (TextView) mDetailsHeadView.findViewById(R.id.details_uaccount);

        mNickItem = (MyInfoItemView) findViewById(R.id.nick_item);
        mHeightItem = (MyInfoItemView) findViewById(R.id.height_item);
        mWeightItem = (MyInfoItemView) findViewById(R.id.weight_item);
        mSexItem = (MyInfoItemView) findViewById(R.id.sex_item);
        mBirthdayItem = (MyInfoItemView) findViewById(R.id.birth_item);
        mAddressItem = (MyInfoItemView) findViewById(R.id.address_item);
        mSignatureItem = (MyInfoItemView) findViewById(R.id.signature_item);

        mSaveInfoButton = (Button) findViewById(R.id.saveinfo);
    }

    private void initData() {
        mTitleText.setText("个人资料");
    }

    private void initListeners() {
        mBackImage.setOnClickListener(this);
        mDetailsHeadView.setOnClickListener(this);
        mNickItem.setOnClickListener(this);
        mHeightItem.setOnClickListener(this);
        mWeightItem.setOnClickListener(this);
        mSexItem.setOnClickListener(this);
        mBirthdayItem.setOnClickListener(this);
        mAddressItem.setOnClickListener(this);
        mSignatureItem.setOnClickListener(this);
        mSaveInfoButton.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myheader_back:
                MyDetailsActivity.this.finish();
                mApplication.removeActivity(this);
                break;
            case R.id.saveinfo:
                //保存用户修改后的信息,提交到服务器
                Toast.makeText(MyDetailsActivity.this, "saveinfo", Toast.LENGTH_SHORT).show();
                break;
            case R.id.details_head:
                Toast.makeText(MyDetailsActivity.this, "修改头像", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nick_item:
                Toast.makeText(MyDetailsActivity.this, ((MyInfoItemView) v).getDataText(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.height_item:
                Toast.makeText(MyDetailsActivity.this, ((MyInfoItemView) v).getDataText(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.weight_item:
                Toast.makeText(MyDetailsActivity.this, ((MyInfoItemView) v).getDataText(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.sex_item:
                Toast.makeText(MyDetailsActivity.this, ((MyInfoItemView) v).getDataText(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.birth_item:
                Toast.makeText(MyDetailsActivity.this, ((MyInfoItemView) v).getDataText(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.address_item:
                Toast.makeText(MyDetailsActivity.this, ((MyInfoItemView) v).getDataText(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.signature_item:
                Toast.makeText(MyDetailsActivity.this, ((MyInfoItemView) v).getDataText(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
