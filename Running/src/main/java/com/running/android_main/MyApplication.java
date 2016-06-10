package com.running.android_main;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.running.beans.UserInfo;
import com.running.event.RongCloudEvent;
import com.running.message.ContactNotificationMessageProvider;
import com.yolanda.nohttp.NoHttp;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;

/**
 * Created by ZhangHang on 2016/5/21.
 */
public class MyApplication extends Application {
    //public static final String HOST = "http://192.168.56.1:8080/Running/";
    public static final String HOST = "http://123.206.203.86:8080/Running/";
    //用户信息
    public UserInfo mUserInfo;
    //总距离
    public String mDistance;
    public List<Activity> mActivityList;
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
        //NoHttp
        NoHttp.init(this);
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    public void finish() {
        for (int i = 0; i < mActivityList.size(); i++) {
            Log.e("my", "size" + mActivityList.size());
            Activity activity = mActivityList.get(i);
            if (activity != null) {
                activity.finish();
            }
        }
    }
}
