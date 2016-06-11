package com.running.android_main;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.running.beans.UserInfo;
import com.running.event.RongCloudEvent;
import com.running.message.ContactNotificationMessageProvider;
import com.running.utils.GlideImageLoader;
import com.running.utils.GlidePauseOnScrollListener;
import com.yolanda.nohttp.NoHttp;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import io.rong.imkit.RongIM;

/**
 * Created by ZhangHang on 2016/5/21.
 */
public class MyApplication extends Application {
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
        //初始化GalleryFinal
        initGalleryFinal();
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

    public void initGalleryFinal() {
        ThemeConfig themeConfig = new ThemeConfig.Builder().build();
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();
        CoreConfig coreConfig = new CoreConfig.Builder(this,new GlideImageLoader(), themeConfig)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(new GlidePauseOnScrollListener(false,true))
                .build();
        GalleryFinal.init(coreConfig);
    }
}
