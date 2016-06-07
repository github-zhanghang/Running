package com.running.android_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.running.beans.SumRecord;
import com.running.myviews.RecordBar;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class RecordActivity extends AppCompatActivity  implements View.OnClickListener{
    private TopBar mTopBar;
    private RecordBar historyRecordBar,trendRecordBar,bestRecordBar;
    TextView countTextView,disTextView,avgTextView;
    int suid=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mTopBar = (TopBar) findViewById(R.id.record_topbar);

        initView();
        addListener();
        initData();

    }

    private void initData() {
        OkHttpUtils.get()
                .url("http://10.201.1.172:8080/Run_zt/sumRecordServlet")
                .addParams("suid",suid+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        //Log.e("taozi123", "RecordActivity onError: "+e.getMessage() );
                    }

                    @Override
                    public void onResponse(String response) {
                       // Log.e("taozi123", "onResponse: "+response);
                        SumRecord sumRecord = new Gson().fromJson(response,SumRecord.class);
                        countTextView.setText(sumRecord.getSumcount()+"");
                        disTextView.setText(sumRecord.getSumrundistance()+"");
                        java.text.DecimalFormat df=new java.text.DecimalFormat("#.00");
                        avgTextView.setText(df.format(sumRecord.getSumrundistance()/sumRecord.getSumcount())+"");
                      /*  try {
                            JSONObject jsonObject=new JSONObject(response);
                            int sumuid=jsonObject.getInt("sumuid");
                            Log.e("taozisumuid",sumuid+"" );
                            double sumrundistance=jsonObject.getDouble("sumrundistance");
                            long sumruntime=jsonObject.getLong("sumruntime");
                            int sumcalories=jsonObject.getInt("sumcalories");
                            long sumstep=jsonObject.getLong("sumstep");
                            int sumcount=jsonObject.getInt("sumcount");
                            SumRecord sumRecord=new SumRecord(sumuid,sumrundistance,sumruntime,sumcalories, sumstep, sumcount);
                            countTextView.setText(sumRecord.getSumcount());
                            disTextView.setText(sumRecord.getSumrundistance()+"");
                            avgTextView.setText((sumRecord.getSumrundistance()/sumRecord.getSumcount())+"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                    }
                });
    }

    private void initView() {
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
