package com.running.android_main;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.running.adapters.TrendViewPagerAdapter;
import com.running.beans.TrendData;
import com.running.utils.TimeUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TrendActivity extends AppCompatActivity {
    private final String mPath = MyApplication.HOST + "trendServlet";
    private MyApplication myApplication;
    private ViewPager mViewPager;
    private TrendViewPagerAdapter mViewPagerAdapter;
    private int uid;
    private List<TrendData> mTrendDataList = new ArrayList<>();
    private TextView mondayTextView, sundayTextView, walkTextView, distanceTextView, timeTextView, carlorieTextView;

    private RequestQueue mRequestQueue = NoHttp.newRequestQueue(1);
    private int mPage;
    //一周的数据
    private List<Double> datas = new ArrayList<>();
    private List<GraphicalView> mList = new ArrayList<>();
    //一周的开始时间
    private long startTime = TimeUtil.getWeekBegin(0);
    //七天的毫秒
    private long sevenDays = 7 * 24 * 60 * 60 * 1000;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

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
        mViewPager = (ViewPager) findViewById(R.id.vp_trend);
        mondayTextView = (TextView) findViewById(R.id.monday_trend);
        sundayTextView = (TextView) findViewById(R.id.sunday_trend);
        sundayTextView.setText(sdf.format(startTime));
        mondayTextView.setText(sdf.format(startTime + sevenDays));
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

    private void initAdapter() {
        for (int i = 0; i < mPage; i++) {
            mList.add(xychar(listToDouble(datas)));
        }
        mViewPagerAdapter = new TrendViewPagerAdapter(TrendActivity.this, mList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setCurrentItem(mPage - 1);
    }

    private OnResponseListener<String> mOnResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            Log.e("my", "trend.result=" + result);
            if (what == 1) {
                mPage = Integer.parseInt(result);
                datas.add(1.0);
                datas.add(2.0);
                datas.add(3.0);
                datas.add(4.0);
                datas.add(5.0);
                datas.add(6.0);
                datas.add(7.0);
                initAdapter();
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

    private void addListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("my", "position=" + position);
                sundayTextView.setText(sdf.format(startTime - (mPage - position - 1) * sevenDays));
                mondayTextView.setText(sdf.format(startTime - (mPage - position) * sevenDays));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public static double[] listToDouble(List<Double> list) {
        double[] doubleData = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            doubleData[i] = list.get(i);
        }
        return doubleData;
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
        int color = Color.argb(255, 255, 153, 0);
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
