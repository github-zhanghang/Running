package com.running.android_main;

import android.app.Activity;
import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.running.event.RongCloudEvent;
import com.running.message.ContactNotificationMessageProvider;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

/**
 * Created by ZhangHang on 2016/5/21.
 */
public class MyApplication extends Application {
    private String mCity = "苏州";//城市
    private String mAccount = "";
    private int mWeight = 60;

    private List<Activity> mActivityList;
    public static String sourceUserId;

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityList = new ArrayList<>();
        //百度地图
        SDKInitializer.initialize(getApplicationContext());
        //初始化融云
        RongIM.init(this);
        sourceUserId = "当前用户的账号(userId)";
        //注册融云的监听事件
        RongCloudEvent.init(this);
        //注册定义的好友添加消息
        RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String account) {
        mAccount = account;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
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
