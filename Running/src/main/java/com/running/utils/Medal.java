package com.running.utils;

import com.running.android_main.R;

/**
 * Created by ZhangHang on 2016/6/17.
 */
public class Medal {
    public static int[] mHaveImage = {R.drawable.ic_medal_m_day1, R.drawable.ic_medal_m_run5,
            R.drawable.ic_medal_m_run10, R.drawable.ic_medal_m_run21,
            R.drawable.ic_medal_m_run42, R.drawable.ic_medal_m_run100};
    public static String[] mHaveString = {"开始运动\n(已获得)", "5公里\n(已获得)", "10公里\n(已获得)",
            "半程马拉松\n(已获得)", "全称马拉松\n(已获得)", "超级马拉松\n(已获得)"};

    public static int[] mNotHaveImage = {R.drawable.ic_medal_m_day1_p, R.drawable.ic_medal_m_run5_p,
            R.drawable.ic_medal_m_run10_p, R.drawable.ic_medal_m_run21_p,
            R.drawable.ic_medal_m_run42_p, R.drawable.ic_medal_m_run100_p};
    public static String[] mNotHaveString = {"开始运动\n(未获得)", "5公里\n(未获得)", "10公里\n(未获得)",
            "半程马拉松\n(未获得)", "全程马拉松\n(未获得)", "超级马拉松\n(未获得)"};

    public static String[] mDescription = {"开始一次跑步", "单次跑步距离达到5公里",
            "单次跑步距离达到10公里", "单次跑步距离达到21.1公里",
            "单次跑步距离达到42.2公里", "单次跑步距离达到100公里"};
}
