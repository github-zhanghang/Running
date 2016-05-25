package com.running.android_main;

import android.app.Activity;
import android.app.Application;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

/**
 * Created by ZhangHang on 2016/5/21.
 */
public class MyApplication extends Application {
    private String mCity = "苏州";
    private String mAccount = "run_1212121";

    private List<Activity> mActivityList;
    public static String sourceUserId;
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        mActivityList = new ArrayList<>();

        /**
         * 初始化融云
         */
        RongIM.init(this);
        sourceUserId = "当前用户的账号(userId)";
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    public void finish() {
        for (int i = 0; i < mActivityList.size(); i++) {
            Activity activity = mActivityList.get(i);
            if (activity != null) {
                activity.finish();
            }
        }
    }
}
