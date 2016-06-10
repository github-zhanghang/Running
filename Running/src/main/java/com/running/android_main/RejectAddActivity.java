package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.running.beans.Friend;

public class RejectAddActivity extends AppCompatActivity {
    private int position;
    private Friend mFriend;
    private ImageView  mImageView;
    private TextView nameTextView,resultTextView;
    private Button rejectBtn,agreeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_add);
       /* Intent intent = getIntent();
        position = (int) intent.getExtras().get("Position");
        mFriend = (Friend) intent.getExtras().get("Friend");
        InitViews();
        initData();*/
    }


    private void InitViews() {


        mImageView = (ImageView) findViewById(R.id.reject_add_tx);
        nameTextView = (TextView) findViewById(R.id.reject_add_name);
        resultTextView = (TextView) findViewById(R.id.result_textView);
        rejectBtn = (Button) findViewById(R.id.reject_add_rejectbtn);
        agreeBtn = (Button) findViewById(R.id.reject_add_agreebtn);

    }
    private void initData() {
        Glide.with(RejectAddActivity.this)
                .load(mFriend.getPortrait())
                .into(mImageView);
        nameTextView.setText(mFriend.getRemark());
        //  1 好友, 2 请求添加, 3 请求被添加,4 拒绝 ,5 请求被拒绝
        if (mFriend.getStatus() == 1 ){
            resultTextView.setVisibility(View.VISIBLE);
            resultTextView.setText("已经同意该申请");

        }else if (mFriend.getStatus() == 2){
           /* resultTextView.setVisibility(View.VISIBLE);
            resultTextView.setText("已经同意该申请");*/
        }else if (mFriend.getStatus() == 3){
            rejectBtn.setVisibility(View.VISIBLE);
            agreeBtn.setVisibility(View.VISIBLE);
        }else if (mFriend.getStatus() == 4){
            resultTextView.setVisibility(View.VISIBLE);
            resultTextView.setText("已经拒绝该申请");
        }else {

        }
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
