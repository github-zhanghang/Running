package com.running.android_main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.running.myviews.wheelview.LoopView;
import com.running.myviews.wheelview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class RunTargetActivity extends AppCompatActivity {
    public static final int RESULT_CODE = 0;
    private ImageView mBackImageView;
    private RadioGroup mRadioGroup;
    private RadioButton mDistanceRadioButton, mTimeRadioButton, mCalorieRadioButton;
    private ListView mListView;
    private String[] mStrings;
    private ArrayAdapter<String> mAdapter;

    private List<String> mDistanceList, mTimeList, mCalorieList;
    //时间转为分钟
    private int timeMinute;
    private int mSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_target);
        initViews();
        initTargetList();
        initAdapter();
        mListView.setAdapter(mAdapter);
        setListeners();
    }

    private void initTargetList() {
        //可选的距离
        mDistanceList = new ArrayList<>();
        float dis = 1.0f;
        for (int i = 0; i < 199; i++) {
            mDistanceList.add(dis + i * (0.5) + "公里");
        }
        //可选的时间
        mTimeList = new ArrayList<>();
        long time = 10;//十分钟
        for (int i = 0; i < 47; i++) {
            //8小时时差
            mTimeList.add(time + 5 * i + "分钟");//加5分钟
        }
        //可选的卡路里
        mCalorieList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            mCalorieList.add("" + 100 * (i + 1) + "大卡");//加100大卡
        }
    }

    private void initViews() {
        mBackImageView = (ImageView) findViewById(R.id.target_back);
        mRadioGroup = (RadioGroup) findViewById(R.id.target_radioGroup);
        mDistanceRadioButton = (RadioButton) findViewById(R.id.target_distance);
        mTimeRadioButton = (RadioButton) findViewById(R.id.target_time);
        mCalorieRadioButton = (RadioButton) findViewById(R.id.target_calorie);
        mListView = (ListView) findViewById(R.id.target_listview);
    }

    private void setListeners() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.target_distance:
                        mStrings = getResources().getStringArray(R.array.runTargetDistance);
                        mAdapter = new ArrayAdapter<String>(RunTargetActivity.this, R.layout.run_target_textview, mStrings);
                        mListView.setAdapter(mAdapter);
                        break;
                    case R.id.target_time:
                        mStrings = getResources().getStringArray(R.array.runTargetTime);
                        mAdapter = new ArrayAdapter<String>(RunTargetActivity.this, R.layout.run_target_textview, mStrings);
                        mListView.setAdapter(mAdapter);
                        break;
                    case R.id.target_calorie:
                        mStrings = getResources().getStringArray(R.array.runTargetCalorie);
                        mAdapter = new ArrayAdapter<String>(RunTargetActivity.this, R.layout.run_target_textview, mStrings);
                        mListView.setAdapter(mAdapter);
                        break;
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                Intent intent = new Intent();
                if (position != mStrings.length - 1) {
                    intent.putExtra("target", textView.getText().toString());
                    RunTargetActivity.this.setResult(RESULT_CODE, intent);
                    RunTargetActivity.this.finish();
                } else {
                    showAlertDialog(intent);
                }
            }
        });
    }

    private void showAlertDialog(final Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RunTargetActivity.this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (mRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.target_distance:
                        intent.putExtra("target", mDistanceList.get(mSelectedItem));
                        break;
                    case R.id.target_time:
                        intent.putExtra("target", mTimeList.get(mSelectedItem));
                        break;
                    case R.id.target_calorie:
                        intent.putExtra("target", mCalorieList.get(mSelectedItem));
                        break;
                }
                RunTargetActivity.this.setResult(RESULT_CODE, intent);
                RunTargetActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        //滚动选择器
        LoopView wheelView = new LoopView(RunTargetActivity.this);
        wheelView.setLayoutParams(params);
        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.target_distance:
                wheelView.setItems(mDistanceList);
                break;
            case R.id.target_time:
                wheelView.setItems(mTimeList);
                break;
            case R.id.target_calorie:
                wheelView.setItems(mCalorieList);
                break;
        }
        wheelView.setNotLoop();
        wheelView.setTextSize(30);
        wheelView.setInitPosition(5);
        wheelView.setBackgroundColor(Color.GRAY);
        wheelView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mSelectedItem = index;
            }
        });
        builder.setView(wheelView).create().show();
    }

    private void initAdapter() {
        mStrings = getResources().getStringArray(R.array.runTargetDistance);
        mAdapter = new ArrayAdapter<String>(this, R.layout.run_target_textview, mStrings);
    }
}
