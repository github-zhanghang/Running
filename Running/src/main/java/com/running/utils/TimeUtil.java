package com.running.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by ZhangHang on 2016/6/12.
 */
public class TimeUtil {
    public static long getWeekBegin(int position) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime() - position * 7 * 24 * 60 * 60 * 1000;
    }
}
