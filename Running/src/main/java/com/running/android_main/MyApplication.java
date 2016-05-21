package com.running.android_main;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangHang on 2016/5/21.
 */
public class MyApplication extends Application {
    private List<Activity> mActivityList;

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityList = new ArrayList<>();
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
