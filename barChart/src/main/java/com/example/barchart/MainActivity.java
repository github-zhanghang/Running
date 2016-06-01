package com.example.barchart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	LinearLayout li1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//得到资源
		li1 = (LinearLayout) findViewById(R.id.li1);
		//初始化柱状图
		initData();
	}

	private void initData() {

		//柱状图序列的名字
		String title="跑步距离";
        //存放柱状图的值
		//String[] value_X=new String[]{"一","二","三","四","五","六","日"};
		int [] value_X=new int[]{0,1,2,3,4,5,6,7};
		double[] value_Y = new double[] { 10 ,3.8, 6.3,5.7, 8.8, 7.5 ,2.1};
		//设置柱状图的范围
		double[] range=new double[]{0,7, 0, 10};
		//为li1添加柱状图
		li1.addView(xychar(title,value_X, value_Y,range),
			new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
	}

	public GraphicalView xychar(String title, int []xLable, double []yLable, double[] range) {

		//渲染,创建你需要的图表最下面的图层
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		//创建你需要在图层上显示的具体内容的图层
		XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
		// 将要绘制的点添加到坐标绘制中
		renderer.addSeriesRenderer(xyRenderer);
		//创建数据层
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		//创建具体的数据层
		XYSeries series = new XYSeries(title);

		for (int j=0;j<yLable.length;j++) {
			series.add(xLable[j+1],yLable[j]);
		}
		dataset.addSeries(series);
		// 设置颜色
		int color=Color.argb(255,255,153,0);
		xyRenderer.setColor(color);
		// 设置 X 轴不显示数字（改用我们手动添加的文字标签）
		//renderer.setXLabels(0);
		//设置x轴标签数
		renderer.setXLabels(xLable.length);
		//设置x轴和y轴的标签对齐方式
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
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
		renderer.setPanEnabled(false,false);
		//设置图例的字体大小
		renderer.setLegendTextSize(0);
		//设置x轴和y轴的最大最小值
		renderer.setRange(range);
		//设置标签倾斜度
		renderer.setXLabelsAngle(-45f);
		renderer.setMarginsColor(0x00888888);

		/*for (int i = 0; i <xLable.length ; i++) {
			//这边是自定义自己的标签,显示自己想要的X轴的标签,需要注意的是需要setXLabels(0)放在标签重叠(就是自定义的标签与图表默认的标签)
			renderer.addXTextLabel(i,xLable[i].toString());
		}*/
		for (int i = 0; i < renderer.getSeriesRendererCount(); i++) {
			SimpleSeriesRenderer ssr = renderer.getSeriesRendererAt(i);
			ssr.setChartValuesTextAlign(Align.RIGHT);
			ssr.setChartValuesTextSize(30);
			ssr.setDisplayChartValues(true);// 在柱子顶端显示数值
		}

		GraphicalView mChartView = ChartFactory.getBarChartView(getApplicationContext(),
				dataset, renderer, Type.DEFAULT);
		return mChartView;

	}
}
