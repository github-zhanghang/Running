package com.running.android_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.running.myviews.TopBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UpdatePhoneActivity extends AppCompatActivity {

    @Bind(R.id.update_phone_topBar)
    TopBar mUpdatePhoneTopBar;
    @Bind(R.id.update_phone_oldPhone)
    EditText mUpdatePhoneOldPhone;
    @Bind(R.id.update_phone_newPhone)
    EditText mUpdatePhoneNewPhone;
    @Bind(R.id.update_phone_ensurePhone)
    EditText mUpdatePhoneEnsurePhone;
    @Bind(R.id.update_phone_yes)
    Button mUpdatePhoneYes;
    @Bind(R.id.update_phone_cancel)
    Button mUpdatePhoneCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone);
        ButterKnife.bind(this);
        addListener();
    }

    private void addListener() {
        mUpdatePhoneTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {

            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });

        mUpdatePhoneYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdatePhoneActivity.this, "确定", Toast.LENGTH_SHORT).show();
            }
        });

        mUpdatePhoneCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UpdatePhoneActivity.this, "取消", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
