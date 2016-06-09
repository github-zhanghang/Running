package com.running.android_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.running.myviews.TopBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutUsActivity extends AppCompatActivity {

    @Bind(R.id.about_us_topBar)
    TopBar mAboutUsTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        addListener();
    }

    private void addListener() {
        mAboutUsTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                AboutUsActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
    }
}
