package com.running.android_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.running.myviews.TopBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PersonInformationActivity extends AppCompatActivity {

    @Bind(R.id.person_information_topBar)
    TopBar mTopBar;
    @Bind(R.id.person_information_headImg)
    ImageView mHeadImg;
    @Bind(R.id.person_information_name)
    TextView mName;
    @Bind(R.id.person_information_sexAndAge)
    TextView mSexAndAge;
    @Bind(R.id.person_information_account)
    TextView mAccount;
    @Bind(R.id.person_information_location)
    TextView mLocation;
    @Bind(R.id.person_information_sumDistance)
    TextView mSumDistance;
    @Bind(R.id.person_information_sumTime)
    TextView mSumTime;
    @Bind(R.id.person_information_whoDynamic)
    TextView mWhoDynamic;
    @Bind(R.id.person_information_dynamicLayout)
    LinearLayout mDynamicLayout;
    @Bind(R.id.person_information_sendMessage)
    Button mSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_information);
        ButterKnife.bind(this);
        addListener();
    }

    private void addListener() {
        //TopBar监听事件
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                Toast.makeText(PersonInformationActivity.this, "返回", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
                Toast.makeText(PersonInformationActivity.this, "备注", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
