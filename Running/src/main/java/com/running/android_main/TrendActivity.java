package com.running.android_main;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.running.adapters.TrendViewPagerAdapter;
import com.running.beans.History;
import com.running.beans.TrendData;
import com.running.myviews.TopBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class TrendActivity extends AppCompatActivity {
    private TopBar mTopBar;
    List<TrendData> trendDatas;
    TextView mondayTextView, sundayTextView, walkTextView, distanceTextView, timeTextView, carlorieTextView;
    ViewPager mViewPager;
    TrendViewPagerAdapter mViewPagerAdapter;
    int cachePagers = 1; //ViewPager缓存页面数目;当前页面的相邻N各页面都会被缓存
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    List<History> mHistoryList;

    TrendData trendData = new TrendData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);

        initView();
        initData();

      /*  mViewPagerAdapter=new TrendViewPagerAdapter(TrendActivity.this, trendDatas);
        mViewPager.setOffscreenPageLimit(cachePagers);// 设置缓存页面，当前页面的相邻N各页面都会被缓存
        mViewPager.setAdapter(mViewPagerAdapter);*/

        initListener();
    }


    private void initView() {
        mTopBar = (TopBar) findViewById(R.id.trend_topbar);
        mViewPager = (ViewPager) findViewById(R.id.vp_trend);
        mondayTextView = (TextView) findViewById(R.id.monday_trend);
        sundayTextView = (TextView) findViewById(R.id.sunday_trend);
        walkTextView = (TextView) findViewById(R.id.walk_trend);
        distanceTextView = (TextView) findViewById(R.id.distance_trend);
        timeTextView = (TextView) findViewById(R.id.time_trend);
        carlorieTextView = (TextView) findViewById(R.id.calorie_trend);
    }

    private void initData() {
        //初始化数据
        trendDatas = new ArrayList<>();

        OkHttpUtils.get()
                .url(MyApplication.HOST+"trendServlet")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                History history = new Gson().fromJson(jsonObject.toString(), History.class);

                                trendData.setMonday(getFirstDayOfWeek(new Date(history.getRunstarttime())) + "");
                                trendData.setSunday(getLastDayOfWeek(new Date(history.getRunstarttime())) + "");
                                //Log.e( "taozidate", getFirstDayOfWeek(new Date(history.getRunstarttime()))+"");
                                trendData.setWalkStep(trendData.getWalkStep() + history.getStepcount());
                                trendData.setDistance(trendData.getDistance() + history.getRundistance());
                                trendData.setTime(trendData.getTime() + history.getRuntime());
                                trendData.setCalorie(trendData.getCalorie() + history.getCalories());
                                //把每天的跑步距离存储到相应的天数中
                                if (getWeekDay(new Date(history.getRunstarttime())) == 1) {
                                    trendData.getValue()[0] += history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime())) == 2) {
                                    trendData.getValue()[1] += history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime())) == 3) {
                                    trendData.getValue()[2] += history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime())) == 4) {
                                    trendData.getValue()[3] += history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime())) == 5) {
                                    trendData.getValue()[4] += history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime())) == 6) {
                                    trendData.getValue()[5] += history.getRundistance();
                                }
                                if (getWeekDay(new Date(history.getRunstarttime())) == 7) {
                                    trendData.getValue()[6] += history.getRundistance();
                                }


                            }
                            trendDatas.add(trendData);

                            mondayTextView.setText("本周一");
                            sundayTextView.setText("本周日");

                            walkTextView.setText((trendDatas.get(0).getWalkStep()) / 7 + "");
                            distanceTextView.setText(trendDatas.get(0).getDistance() + "");
                            timeTextView.setText(trendDatas.get(0).getTime() / (1000 * 60 * 7) + "");
                            carlorieTextView.setText(trendDatas.get(0).getCalorie() + "");

                            mViewPagerAdapter = new TrendViewPagerAdapter(TrendActivity.this, trendDatas);
                            mViewPager.setOffscreenPageLimit(cachePagers);// 设置缓存页面，当前页面的相邻N各页面都会被缓存
                            mViewPager.setAdapter(mViewPagerAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    //设置滑动监听事件
    private void initListener() {
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                TrendActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(TrendActivity.this,"huadong"+(position+1),Toast.LENGTH_SHORT).show();
              /*  mondayTextView.setText(trendDatas.get(position).getMonday());
                sundayTextView.setText(trendDatas.get(position).getSunday());*/
                walkTextView.setText((trendDatas.get(position).getWalkStep()) + "");
                distanceTextView.setText(trendDatas.get(position).getDistance() + "");
                timeTextView.setText(trendDatas.get(position).getTime() / (1000 * 60) + "");
                carlorieTextView.setText(trendDatas.get(position).getCalorie() + "");
                mViewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.trend_magin));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

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
    public static int getWeekDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return week;
    }

}
