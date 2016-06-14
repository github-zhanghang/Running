package com.running.eventandcontext;

import android.content.Context;
import android.preference.PreferenceManager;

import io.rong.imkit.RongIM;

/**
 * Created by Ezio on 2016/6/14.
 */
public class RongCloudContext {
    private static RongCloudContext mDemoContext;
    public Context mContext;
    private RongIM.LocationProvider.LocationCallback mLastLocationCallback;

    private RongCloudContext(Context context) {
        mContext = context;
        mDemoContext = this;
    }
    public static void init(Context context) {
        mDemoContext = new RongCloudContext(context);
    }

    public static RongCloudContext getInstance() {

        if (mDemoContext == null) {
            mDemoContext = new RongCloudContext();
        }
        return mDemoContext;
    }

    private RongCloudContext() {

    }


    public RongIM.LocationProvider.LocationCallback getLastLocationCallback() {
        return mLastLocationCallback;
    }

    public void setLastLocationCallback(RongIM.LocationProvider.LocationCallback lastLocationCallback) {
        this.mLastLocationCallback = lastLocationCallback;
    }
}
