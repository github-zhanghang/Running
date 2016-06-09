package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.running.beans.Friend;

public class RejectAddActivity extends AppCompatActivity {
    private int position;
    private Friend mFriend;
    private ImageView  mImageView;
    private TextView nameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_add);
        InitViews();
        initData();
    }


    private void InitViews() {
        Intent intent = getIntent();
        position = (int) intent.getExtras().get("Position");
        mFriend = (Friend) intent.getExtras().get("Friend");

    }
    private void initData() {
        Glide.with(RejectAddActivity.this)
                .load(mFriend.getPortrait())
                .into(mImageView);
        nameTextView.setText(mFriend.getRemark());
    }

    public void rejectAgree(View view) {
        Intent intent = new Intent(RejectAddActivity.this,NewFriendListActivity.class);
        intent.putExtra("flag",true);
        intent.putExtra("position",position);
        setResult(RESULT_OK, intent);
        RejectAddActivity.this.finish();
    }

    public void rejectAdd(View view) {
        Intent intent = new Intent(RejectAddActivity.this,NewFriendListActivity.class);
        intent.putExtra("flag",false);
        intent.putExtra("position",position);
        setResult(RESULT_OK, intent);
        RejectAddActivity.this.finish();
    }
}
