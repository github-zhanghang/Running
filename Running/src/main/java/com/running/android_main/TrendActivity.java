package com.running.android_main;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.running.adapters.TrendViewPagerAdapter;
import com.running.beans.History;
import com.running.beans.TrendData;
import com.running.myviews.TopBar;
import com.running.utils.TimeUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TrendActivity extends AppCompatActivity {
    private final String mPath = MyApplication.HOST + "trendServlet";
    private MyApplication myApplication;
    private TopBar mTopBar;
    private ViewPager mViewPager;
    private TrendViewPagerAdapter mViewPagerAdapter;
    private int uid;
    private TextView mondayTextView, sundayTextView, walkTextView, distanceTextView, timeTextView, carlorieTextView;

    private RequestQueue mRequestQueue = NoHttp.newRequestQueue(1);
    //周数
    private int mPage;
    //第几周
    private int mPosition;
    //一周的数据
    private double[] datas = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

    private List<GraphicalView> mList = null;
    //一周的开始时间
    private long startTime = TimeUtil.getWeekBegin(0);
    //七天的毫秒
    private long sevenDays = 7 * 24 * 60 * 60 * 1000;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    DecimalFormat df = new DecimalFormat("######0.00");

    private boolean isFirst = true;
    private long maxTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);
        myApplication = (MyApplication) getApplication();
        uid = myApplication.getUserInfo().getUid();
        initView();
        addListener();
        //获取一共有多少周
        getTotalWeek();
    }

    private void initView() {
        mTopBar = (TopBar) findViewById(R.id.trend_topbar);
        mViewPager = (ViewPager) findViewById(R.id.vp_trend);
        mondayTextView = (TextView) findViewById(R.id.monday_trend);
        sundayTextView = (TextView) findViewById(R.id.sunday_trend);
        mondayTextView.setText(sdf.format(startTime));
        sundayTextView.setText(sdf.format(startTime + sevenDays));
        walkTextView = (TextView) findViewById(R.id.walk_trend);
        distanceTextView = (TextView) findViewById(R.id.distance_trend);
        timeTextView = (TextView) findViewById(R.id.time_trend);
        carlorieTextView = (TextView) findViewById(R.id.calorie_trend);
    }

    private void getTotalWeek() {
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "totalweek");
        request.add("uid", myApplication.getUserInfo().getUid());
        mRequestQueue.add(1, request, mOnResponseListener);
        mRequestQueue.start();
    }

    private OnResponseListener<String> mOnResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            Log.e("my", "trend.result=" + result);
            if (what == 1 && result != null) {
                String[] results = result.split(",");
                mPage = Integer.parseInt(results[0]);
                maxTime = Long.parseLong(results[1]);
                startTime = TimeUtil.getWeekBeginByTime(maxTime);
                long st = TimeUtil.getWeekBeginByTime(startTime);
                mondayTextView.setText(sdf.format(st));
                sundayTextView.setText(sdf.format(st + sevenDays));
                //加载本周数据
                getWeekData(startTime);
            } else if (what == 2) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    TrendData trendData = new TrendData();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        History history = new Gson().fromJson(jsonObject.toString(), History.class);

                        trendData.setWalkStep(trendData.getWalkStep() + history.getStepcount());
                        trendData.setDistance(trendData.getDistance() + history.getRundistance());
                        trendData.setTime(trendData.getTime() + history.getRuntime());
                        trendData.setCalorie(trendData.getCalorie() + history.getCalories());

                        //把每天的跑步距离存储到相应的日期中
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
                    for (int i = 0; i < datas.length; i++) {
                        Log.e("my", "datas[" + i + "]=" + datas[i]);
                    }
                    datas = trendData.getValue();
                    if (isFirst) {
                        mList = new ArrayList<>();
                        for (int i = 0; i < mPage; i++) {
                            mList.add(xychar(datas));
                        }
                        mViewPagerAdapter = new TrendViewPagerAdapter(TrendActivity.this, mList);
                        mViewPager.setAdapter(mViewPagerAdapter);
                        mViewPager.setCurrentItem(0);
                        isFirst = false;
                    } else {
                        mList.set(mPosition, xychar(datas));
                        mViewPagerAdapter.notifyDataSetChanged();
                    }
                    walkTextView.setText((trendData.getWalkStep()) / 7 + "");
                    distanceTextView.setText(df.format(trendData.getDistance()) + "");
                    timeTextView.setText(trendData.getTime() / (1000 * 60 * 7) + "");
                    carlorieTextView.setText(trendData.getCalorie() + "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
            Toast.makeText(TrendActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish(int what) {
        }
    };

    private void getWeekData(long startTime) {
        Request<String> request = NoHttp.createStringRequest(mPath, RequestMethod.POST);
        request.add("type", "weekdata");
        request.add("uid", myApplication.getUserInfo().getUid());
        request.add("start", startTime);
        mRequestQueue.add(2, request, mOnResponseListener);
        mRequestQueue.start();
    }

    private void addListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                mondayTextView.setText(sdf.format(startTime - (position) * sevenDays));
                sundayTextView.setText(sdf.format(startTime - (position - 1) * sevenDays));
                getWeekData(startTime - (mPage - position) * sevenDays);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mTopBar.setOnTopbarClickListener(new TopBar.OnTopbarClickListener() {
            @Override
            public void onTopbarLeftImageClick(ImageView imageView) {
                TrendActivity.this.finish();
            }

            @Override
            public void onTopbarRightImageClick(ImageView imageView) {
            }
        });
    }

    //判断某天为周几
    public int getWeekDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return week;
    }

    //柱状图
    public GraphicalView xychar(double[] yLable) {
        //存放柱状图的值
        int[] xLable = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
        //设置柱状图的范围
        double[] range = new double[]{0.5, 7.5, 0, 10};

        //渲染,创建你需要的图表最下面的图层
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        //创建你需要在图层上显示的具体内容的图层
        XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
        // 将要绘制的点添加到坐标绘制中
        renderer.addSeriesRenderer(xyRenderer);
        //创建数据层
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        //创建具体的数据层
        XYSeries series = new XYSeries("");

        for (int j = 0; j < yLable.length; j++) {
            series.add(xLable[j + 1], yLable[j]);
        }
        dataset.addSeries(series);
        // 设置颜色
        int color = Color.argb(255, 235, 79, 56);
        xyRenderer.setColor(color);
        // 设置 X 轴不显示数字（改用我们手动添加的文字标签）
        renderer.setShowLegend(false);
        renderer.setXLabels(0);
        //设置x轴标签数
        renderer.setXLabels(xLable.length);
        //设置x轴和y轴的标签对齐方式
        renderer.setXLabelsAlign(Paint.Align.RIGHT);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        // 设置现实网格
        renderer.setShowGrid(false);
        //设置是否需要显示坐标轴
        renderer.setShowAxes(true);
        // 设置轴标签文本大小
        renderer.setLabelsTextSize(25);
        // 设置条形图之间的距离
        renderer.setBarSpacing(1.0);
        // 调整合适的位置
        renderer.setFitLegend(true);
        //表盘移动
        renderer.setPanEnabled(false, false);
        //不可缩放
        renderer.setZoomEnabled(false, false);
        renderer.setClickEnabled(false);
        renderer.setZoomButtonsVisible(false);
        //设置图例的字体大小
        renderer.setLegendTextSize(0);
        //设置x轴和y轴的最大最小值
        renderer.setRange(range);
        //设置标签倾斜度
        renderer.setXLabelsAngle(-45f);
        renderer.setMarginsColor(0x00888888);

        for (int i = 0; i < renderer.getSeriesRendererCount(); i++) {
            SimpleSeriesRenderer ssr = renderer.getSeriesRendererAt(i);
            ssr.setChartValuesTextAlign(Paint.Align.RIGHT);
            ssr.setChartValuesTextSize(30);
            ssr.setDisplayChartValues(true);// 在柱子顶端显示数值
        }

        GraphicalView mChartView = ChartFactory.getBarChartView(TrendActivity.this,
                dataset, renderer, BarChart.Type.DEFAULT);
        return mChartView;
    }
}
