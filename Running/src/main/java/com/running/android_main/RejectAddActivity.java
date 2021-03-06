package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.running.beans.Friend;
import com.running.myviews.TopBar;

public class RejectAddActivity extends AppCompatActivity {
    private int position = -1;
    private Friend mFriend;
    private ImageView  mImageView;
    private TextView nameTextView,resultTextView , conTextView;
    private Button rejectBtn,agreeBtn;
    private TopBar mTopBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_add);
        Intent intent = getIntent();
        position = (int) intent.getExtras().get("Position");
        mFriend = (Friend) intent.getExtras().get("Friend");
        Log.e("RejectAddActivity", "Position: "+position );
        Log.e("RejectAddActivity", "Friend: "+mFriend.getStatus() );
        InitViews();
        initData();
    }


    private void InitViews() {
        mTopBar = (TopBar) findViewById(R.id.reject_add_TopBar);
        mImageView = (ImageView) findViewById(R.id.reject_add_tx);
        nameTextView = (TextView) findViewById(R.id.reject_add_name);
        resultTextView = (TextView) findViewById(R.id.result_textView);
        conTextView  = (TextView) findViewById(R.id.reject_add_con);
        rejectBtn = (Button) findViewById(R.id.reject_add_rejectbtn);
        agreeBtn = (Button) findViewById(R.id.reject_add_agreebtn);
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                RejectAddActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });

    }

    private void initData() {
        Glide.with(RejectAddActivity.this)
                .load(mFriend.getPortrait())
                .into(mImageView);
        nameTextView.setText(mFriend.getRemark());
        String s = "你好,我是"+mFriend.getRemark()+"，可以加为好友吗？";
        conTextView.setText(s);
        //  1 好友, 2 请求添加, 3 请求被添加,4 拒绝 ,5 请求被拒绝
       if (mFriend.getStatus() == 3){
            rejectBtn.setVisibility(View.VISIBLE);
            agreeBtn.setVisibility(View.VISIBLE);
        }else if (mFriend.getStatus() == 4){
            resultTextView.setVisibility(View.VISIBLE);
            resultTextView.setText("已经拒绝该申请");
        }
    }

    public void rejectAgree(View view) {
        Intent intent = new Intent(RejectAddActivity.this, NewFriendListActivity.class);
        intent.putExtra("flag", true);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        RejectAddActivity.this.finish();
    }

    public void rejectAdd(View view) {
        Intent intent = new Intent(RejectAddActivity.this, NewFriendListActivity.class);
        intent.putExtra("flag", false);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        RejectAddActivity.this.finish();
    }
}
