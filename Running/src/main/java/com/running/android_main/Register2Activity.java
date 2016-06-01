package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Register2Activity extends AppCompatActivity implements View.OnClickListener {
    private ImageView girlImageView, boyImageView;
    private String mPassword, mTelephone, mSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        Intent intent = getIntent();
        mPassword = intent.getStringExtra("password");
        mTelephone = intent.getStringExtra("telephone");
        initView();
        addListener();
    }

    private void initView() {
        girlImageView = (ImageView) findViewById(R.id.girl);
        boyImageView = (ImageView) findViewById(R.id.boy);
    }

    private void addListener() {
        girlImageView.setOnClickListener(this);
        boyImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent = new Intent(Register2Activity.this, Register3Activity.class);
        switch (id) {
            case R.id.girl:
                Toast.makeText(this, "您选择了：女", Toast.LENGTH_SHORT).show();
                mSex = "女";
                break;
            case R.id.boy:
                Toast.makeText(this, "您选择了：男", Toast.LENGTH_SHORT).show();
                mSex = "男";
                break;
        }
        intent.putExtra("password", mPassword);
        intent.putExtra("telephone", mTelephone);
        intent.putExtra("sex", mSex);
        startActivity(intent);
    }
}
