package com.running.android_main;


import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.running.adapters.TrendViewPagerAdapter;
import com.running.beans.TrendData;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TrendActivity extends AppCompatActivity {

    TextView dateTextView,walkTextView,distanceTextView,timeTextView,carlorieTextView;
    List<TrendData> trendDatas;
    List<LinearLayout> linearLayouts;
    ViewPager mViewPager;
    TrendViewPagerAdapter mViewPagerAdapter;
    /** ViewPager缓存页面数目;当前页面的相邻N各页面都会被缓存 */
    int cachePagers = 1;
    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);


        initData();
        initView();
        mViewPagerAdapter=new TrendViewPagerAdapter(TrendActivity.this, trendDatas,linearLayouts);
        mViewPager.setOffscreenPageLimit(cachePagers);// 设置缓存页面，当前页面的相邻N各页面都会被缓存
        mViewPager.setAdapter(mViewPagerAdapter);
        initListener();
    }

    private void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(TrendActivity.this,"huadong"+(position+1),Toast.LENGTH_SHORT).show();
               // dateTextView.setText(trendDatas.get(position).getMonday().toString()+"");


                dateTextView.setText(trendDatas.get(position).getMonday().toString());
                walkTextView.setText(trendDatas.get(position).getWalkStep()+"");
                distanceTextView.setText(trendDatas.get(position).getDistance()+"");
                timeTextView.setText(trendDatas.get(position).getTime()+"");
                carlorieTextView.setText(trendDatas.get(position).getCalorie()+"");
                mViewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.trend_magin));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        mViewPager= (ViewPager) findViewById(R.id.vp_trend);
        dateTextView= (TextView) findViewById(R.id.date_trend);
        walkTextView= (TextView) findViewById(R.id.walk_trend);
        distanceTextView= (TextView) findViewById(R.id.distance_trend);
        timeTextView= (TextView) findViewById(R.id.time_trend);
        carlorieTextView= (TextView) findViewById(R.id.calorie_trend);
    }
    private void initData()  {
        //初始化数据
        trendDatas =new ArrayList<>();
        TrendData t1= null;
        TrendData t2=null;
        TrendData t3=null;
        try {
            t1 = new TrendData(dateFormat.parse("2016-05-15"),3900,36.6,45,4500.3,new double[]{ 2.1 ,3.8, 5.3,2.7, 6.8, 5.5 ,3.9});
            t2=new TrendData(dateFormat.parse("2016-06-22"),5400,27.4,23,3570.1,new double[]{ 4.1 ,6.8, 2.3,1.7, 6.8, 4.5 ,2.2});
            t3=new TrendData(dateFormat.parse("2016-06-29"),2340,49.6,64,1341.9,new double[]{ 5.1 ,1.8, 6.3,2.7, 3.8, 1.5 ,3.2});
        } catch (ParseException e) {
            e.printStackTrace();
        }

        trendDatas.add(t1);
        trendDatas.add(t2);
        trendDatas.add(t3);

        linearLayouts=new ArrayList<>();
        for (int i = 0; i < trendDatas.size(); i++) {
            LinearLayout linearLayout=new LinearLayout(this);
            //添加柱状图
            linearLayout.addView(xychar(trendDatas.get(i).getValue()), new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayouts.add(linearLayout);
        }
    }

    public GraphicalView xychar(double []yLable) {

        //存放柱状图的值
        int [] xLable=new int[]{0,1,2,3,4,5,6,7};
        //设置柱状图的范围
        double[] range=new double[]{0.5,7.5, 0, 10};

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

        for (int j=0;j<yLable.length;j++) {
            series.add(xLable[j+1],yLable[j]);
        }
        dataset.addSeries(series);
        // 设置颜色
        int color= Color.argb(255,255,153,0);
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
        renderer.setPanEnabled(false,false );
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

        GraphicalView mChartView = ChartFactory.getBarChartView(getApplicationContext(),
                dataset, renderer, BarChart.Type.DEFAULT);
        return mChartView;
    }

}
