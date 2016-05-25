package com.running.android_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class NewFrinedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_frined);
        Boolean b  = (Boolean) getIntent().getExtras().get("has");
        Toast.makeText(this, b+"", Toast.LENGTH_SHORT).show();
    }
}
