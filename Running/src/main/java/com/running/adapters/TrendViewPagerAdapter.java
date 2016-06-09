package com.running.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.running.android_main.R;
import com.running.beans.TrendData;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by C5-0 on 2016/5/28.
 */
public class TrendViewPagerAdapter extends PagerAdapter {
    List<TrendData> trendDatas;
    List<LinearLayout> linearLayouts;
    Context mContext;
    LayoutInflater mInflater;

    public TrendViewPagerAdapter(Context mContext, List<TrendData> trendDatas) {
        this.mContext = mContext;
        this.trendDatas = trendDatas;
        mInflater.from(mContext).inflate(R.layout.activity_trend,null);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

       linearLayouts=new ArrayList<>();
       for (int i = 0; i <trendDatas.size(); i++) {
            LinearLayout linearLayout=new LinearLayout(mContext);
            //添加柱状图
            linearLayout.addView(xychar(trendDatas.get(position).getValue()), new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayouts.add(linearLayout);
        }

        LinearLayout linearLayout=linearLayouts.get(position);
        container.addView(linearLayout);
        return linearLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //销毁缓存
        container.removeView(linearLayouts.get(position));
    }

    @Override
    public int getCount() {
        return trendDatas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }



    //柱状图
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

        GraphicalView mChartView = ChartFactory.getBarChartView(mContext,
                dataset, renderer, BarChart.Type.DEFAULT);
        return mChartView;
    }
}
