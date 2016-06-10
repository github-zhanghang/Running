package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.running.beans.History;
import com.running.myviews.BestBar;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class BestActivity extends AppCompatActivity implements View.OnClickListener{
    private TopBar mTopBar;
    BestBar distanceBestBar,timeBestBar,calorieBestBar,speedBestBar,fiveBestBar,tenBestBar,halfBestBar,wholeBestBar;
    List<History> mHistoryList=new ArrayList<>();
    Bundle bundle=new Bundle();
    Intent intent;
    DateFormat dateFormat=new SimpleDateFormat("hh:mm:ss");
    int uid=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best);
        initView();
        initData();
        addListener();
    }

    private void setData() {

        if (mHistoryList.get(0).isExist()){
            distanceBestBar.setDataText(mHistoryList.get(0).getRundistance()+"");
        }else {
            distanceBestBar.setDataText("无记录");
        }

        if (mHistoryList.get(1).isExist()){
            timeBestBar.setDataText(dateFormat.format(mHistoryList.get(1).getRuntime()));
        }else {
            timeBestBar.setDataText("无记录");
        }
        if (mHistoryList.get(2).isExist()){
            calorieBestBar.setDataText(mHistoryList.get(2).getCalories()+"");
        }else {
            calorieBestBar.setDataText("无记录");
        }
        if (mHistoryList.get(3).isExist()){
            java.text.DecimalFormat df=new java.text.DecimalFormat("#0.00");//保留两位小数
           speedBestBar.setDataText(df.format((mHistoryList.get(3).getRuntime()/(1000*60*60))/mHistoryList.get(3).getRundistance()));
        }else {
            speedBestBar.setDataText("无记录");
        }
        if (mHistoryList.get(4).isExist()){
           fiveBestBar.setDataText(dateFormat.format(mHistoryList.get(4).getRuntime()));
        }else {
            fiveBestBar.setDataText("未完成");
        }
        if (mHistoryList.get(5).isExist()){
           tenBestBar.setDataText(dateFormat.format(mHistoryList.get(5).getRuntime()));
        }else {
           tenBestBar.setDataText("未完成");
        }
        if (mHistoryList.get(6).isExist()){
            halfBestBar.setDataText(dateFormat.format(mHistoryList.get(6).getRuntime()));
        }else {
            halfBestBar.setDataText("未完成");
        }
        if (mHistoryList.get(7).isExist()){
           wholeBestBar.setDataText(dateFormat.format(mHistoryList.get(7).getRuntime()));
        }else {
           wholeBestBar.setDataText("未完成");
        }

    }

    private void initData() {

        OkHttpUtils.get()
                .url("http://10.201.1.172:8080/Run_zt/bestRecordServlet")
                .addParams("uid",uid+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("taozibest",e.getMessage() );
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                History history =new Gson().fromJson(jsonObject.toString(),History.class);
                                mHistoryList.add(history);
                            }
                            setData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void addListener() {
        distanceBestBar.setOnClickListener(this);
        timeBestBar.setOnClickListener(this);
        calorieBestBar.setOnClickListener(this);
        speedBestBar.setOnClickListener(this);
        fiveBestBar.setOnClickListener(this);
        tenBestBar.setOnClickListener(this);
        halfBestBar.setOnClickListener(this);
        wholeBestBar.setOnClickListener(this);
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                BestActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {

            }
        });
    }

    private void initView() {
        mTopBar = (TopBar) findViewById(R.id.best_topbar);
        distanceBestBar= (BestBar) findViewById(R.id.distance_best);
        timeBestBar= (BestBar) findViewById(R.id.time_best);
        calorieBestBar= (BestBar) findViewById(R.id.caloie_best);
        speedBestBar= (BestBar) findViewById(R.id.speed_best);
        fiveBestBar= (BestBar) findViewById(R.id.five_best);
        tenBestBar= (BestBar) findViewById(R.id.ten_best);
        halfBestBar= (BestBar) findViewById(R.id.half_best);
        wholeBestBar= (BestBar) findViewById(R.id.whole_best);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.distance_best:
                if (mHistoryList.get(0).isExist()) {
                    bundle.putSerializable("history", mHistoryList.get(0));
                    intent = new Intent(BestActivity.this, HistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.time_best:
                if (mHistoryList.get(1).isExist()) {
                    bundle.putSerializable("history", mHistoryList.get(1));
                    intent = new Intent(BestActivity.this, HistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.caloie_best:
                if (mHistoryList.get(2).isExist()) {
                    bundle.putSerializable("history", mHistoryList.get(2));
                    intent = new Intent(BestActivity.this, HistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.speed_best:
                if (mHistoryList.get(3).isExist()) {
                    bundle.putSerializable("history", mHistoryList.get(3));
                    intent = new Intent(BestActivity.this, HistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.five_best:
                if (mHistoryList.get(4).isExist()) {
                    bundle.putSerializable("history", mHistoryList.get(4));
                    intent = new Intent(BestActivity.this, HistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.ten_best:
                if (mHistoryList.get(5).isExist()) {
                    bundle.putSerializable("history", mHistoryList.get(5));
                    intent = new Intent(BestActivity.this, HistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.half_best:
                if (mHistoryList.get(6).isExist()) {
                    bundle.putSerializable("history", mHistoryList.get(6));
                    intent = new Intent(BestActivity.this, HistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.whole_best:
                if (mHistoryList.get(7).isExist()) {
                    bundle.putSerializable("history", mHistoryList.get(7));
                    intent = new Intent(BestActivity.this, HistoryDetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }
}
