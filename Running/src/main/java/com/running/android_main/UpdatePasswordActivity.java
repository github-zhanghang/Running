package com.running.android_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.running.myviews.TopBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UpdatePasswordActivity extends AppCompatActivity {

    @Bind(R.id.update_password_topBar)
    TopBar mUpdatePasswordTopBar;
    @Bind(R.id.update_pw_headImg)
    ImageView mUpdatePwHeadImg;
    @Bind(R.id.update_pw_name)
    TextView mUpdatePwName;
    @Bind(R.id.update_pw_oldPW)
    EditText mUpdatePwOldPW;
    @Bind(R.id.update_pw_newPW)
    EditText mUpdatePwNewPW;
    @Bind(R.id.update_pw_confirmNewPW)
    EditText mUpdatePwConfirmNewPW;
    @Bind(R.id.update_pw_submit)
    Button mUpdatePwSubmit;
    @Bind(R.id.update_pw_cancel)
    Button mUpdatePwCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        ButterKnife.bind(this);
        addListener();
    }

    private void addListener() {
        mUpdatePasswordTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {

            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
    }
}
