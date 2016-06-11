package com.running.android_main;



import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.running.adapters.TrendViewPagerAdapter;
import com.running.beans.History;
import com.running.beans.TrendData;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;

public class TrendActivity extends AppCompatActivity {
    private MyApplication myApplication;
    int uid;
    List<TrendData> mTrendDataList=new ArrayList<>();
    TextView mondayTextView,sundayTextView,walkTextView,distanceTextView,timeTextView,carlorieTextView;
    ViewPager mViewPager;
    TrendViewPagerAdapter mViewPagerAdapter;
   /* int cachePagers = 1; //ViewPager缓存页面数目;当前页面的相邻N各页面都会被缓存
    SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);*/
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private String dateString;
    Date date=new Date();

    // 取得当前日期所在周的第一天
    public static Date getFirstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek()); // Sunday
        return calendar.getTime();
    }

    // 取得当前日期所在周的最后一天
    public static Date getLastDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6); // Saturday
        return calendar.getTime();
    }
    //判断某天为周几
    public static int getWeekDay(Date date){
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        int week=cal.get(Calendar.DAY_OF_WEEK)-1;
        return week;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);

        myApplication = (MyApplication) getApplication();
        uid=myApplication.getUserInfo().getUid();

        initView();
        refreshData(0);
        initListener();

        //mViewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.trend_magin));
        //mViewPager.setOffscreenPageLimit(cachePagers);// 设置缓存页面，当前页面的相邻N各页面都会被缓存
    }

    private void initView() {
        mViewPager= (ViewPager) findViewById(R.id.vp_trend);
        mondayTextView= (TextView) findViewById(R.id.monday_trend);
        sundayTextView= (TextView) findViewById(R.id.sunday_trend);
        walkTextView= (TextView) findViewById(R.id.walk_trend);
        distanceTextView= (TextView) findViewById(R.id.distance_trend);
        timeTextView= (TextView) findViewById(R.id.time_trend);
        carlorieTextView= (TextView) findViewById(R.id.calorie_trend);
    }


    private void refreshData(final int position) {
        date=new Date(date.getTime()-position*7*24*60*60*1000);
        dateString= String.valueOf(date.getTime());

        OkHttpUtils.get()
                .url(MyApplication.HOST+"trendServlet")
                .addParams("date",dateString)
                .addParams("uid",uid+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("taozi", "refresh--onError: "+e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.e("taozi", "refresh--onResponse: "+response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            TrendData trendData=new TrendData();
                            for (int i = 0; i <jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                History history=new Gson().fromJson(jsonObject.toString(),History.class);

                                String monday=sdf.format(getFirstDayOfWeek(date));
                               // Log.e("taozimonday"," refresh:"+monday );
                                String sunday=sdf.format(getLastDayOfWeek(date));
                                trendData.setMonday(monday);
                                trendData.setSunday(sunday);
                                trendData.setWalkStep(trendData.getWalkStep()+history.getStepcount());
                                trendData.setDistance(trendData.getDistance()+history.getRundistance());
                                trendData.setTime(trendData.getTime()+history.getRuntime());
                                trendData.setCalorie(trendData.getCalorie()+history.getCalories());

                                //把每天的跑步距离存储到相应的日期中
                                if (getWeekDay(new Date(history.getRunstarttime()))==1){
                                    trendData.getValue()[0]+=history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime()))==2){
                                    trendData.getValue()[1]+=history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime()))==3){
                                    trendData.getValue()[2]+=history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime()))==4){
                                    trendData.getValue()[3]+=history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime()))==5){
                                    trendData.getValue()[4]+=history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime()))==6){
                                    trendData.getValue()[5]+=history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime()))==7){
                                    trendData.getValue()[6]+=history.getRundistance();
                                }
                            }
                            mTrendDataList.add(trendData);

                            mondayTextView.setText(mTrendDataList.get(position).getMonday());
                            sundayTextView.setText(mTrendDataList.get(position).getSunday());
                            walkTextView.setText((mTrendDataList.get(position).getWalkStep())/7+"");
                            distanceTextView.setText(mTrendDataList.get(position).getDistance()+"");
                            timeTextView.setText(mTrendDataList.get(position).getTime()/(1000*60*7)+"");
                            carlorieTextView.setText(mTrendDataList.get(position).getCalorie()+"");
                         /*   mondayTextView.setText(trendData.getMonday());
                            sundayTextView.setText(trendData.getSunday());
                            walkTextView.setText((trendData.getWalkStep())/7+"");
                            distanceTextView.setText(trendData.getDistance()+"");
                            timeTextView.setText(trendData.getTime()/(1000*60*7)+"");
                            carlorieTextView.setText(trendData.getCalorie()+"");*/
                            //设置适配器
                           // Log.e( "taozisize","zong"+ mTrendDataList.size()+"");
                            mViewPagerAdapter=new TrendViewPagerAdapter(TrendActivity.this,mTrendDataList);
                            mViewPager.setAdapter(mViewPagerAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

   //设置滑动监听事件
    private void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

              refreshData(position);

            }


            @Override
            public void onPageScrollStateChanged(int state) {
                //页面状态改变时调用
            }
        });
    }



}
