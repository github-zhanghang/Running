package com.running.android_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.running.beans.SumRecord;
import com.running.myviews.CircleImageView;
import com.running.myviews.RecordBar;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class RecordActivity extends AppCompatActivity  implements View.OnClickListener{
    private MyApplication myApplication;
    private TopBar mTopBar;
    CircleImageView mImageView;
    private RecordBar historyRecordBar,trendRecordBar,bestRecordBar;
    TextView countTextView,disTextView,avgTextView;
    int suid=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mTopBar = (TopBar) findViewById(R.id.record_topbar);

        myApplication = (MyApplication) getApplication();
        suid=myApplication.getUserInfo().getUid();

        initView();
        addListener();
        initData();
    }

    private void initData() {
        Glide.with(RecordActivity.this)
                .load(myApplication.getUserInfo().getImageUrl())
                .into(mImageView);
        OkHttpUtils.get()
                .url(MyApplication.HOST+"sumRecordServlet")
                .addParams("suid",suid+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        //Log.e("taozi123", "RecordActivity onError: "+e.getMessage() );
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("taozi123", "onResponse: "+response);
                        SumRecord sumRecord = new Gson().fromJson(response,SumRecord.class);
                        countTextView.setText(sumRecord.getSumcount()+"");
                        disTextView.setText(sumRecord.getSumrundistance()+"");
                        java.text.DecimalFormat df=new java.text.DecimalFormat("#0.00");//保留两位小数
                        avgTextView.setText(df.format(sumRecord.getSumrundistance()/sumRecord.getSumcount()));
                    }
                });
    }

    private void initView() {
        mTopBar = (TopBar) findViewById(R.id.record_topbar);
        mImageView = (CircleImageView) findViewById(R.id.re_image);
        historyRecordBar= (RecordBar) findViewById(R.id.re_history);
        trendRecordBar= (RecordBar) findViewById(R.id.re_trend);
        bestRecordBar= (RecordBar) findViewById(R.id.re_best);
        countTextView= (TextView) findViewById(R.id.re_sumcount);
        disTextView= (TextView) findViewById(R.id.re_dis);
        avgTextView= (TextView) findViewById(R.id.re_avg_dis);
    }

    private void addListener() {
        historyRecordBar.setOnClickListener(this);
        trendRecordBar.setOnClickListener(this);
        bestRecordBar.setOnClickListener(this);
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                RecordActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.re_history:
                Intent intent=new Intent(RecordActivity.this,HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.re_trend:
                Intent intent2=new Intent(RecordActivity.this,TrendActivity.class);
                startActivity(intent2);
                break;
            case R.id.re_best:
                Intent intent3=new Intent(RecordActivity.this,BestActivity.class);
                startActivity(intent3);
                break;
        }

    }
}
