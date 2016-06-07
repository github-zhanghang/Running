package com.running.beans;

/**
 * Created by Ezio on 2016/6/4.
 */
public class NearUserInfo extends UserInfo {
    private double distance ;

    public NearUserInfo(String mCode, int mUid, String mAccount, String mTelephone, String mNickName, String mImageUrl, String mSignature, String mBirthday, int mAge, String mSex, int mHeight, int mWeight, String mAddress, double mLatitude, double mLongitude, String mRongToken, double distance) {
        super(mCode, mUid, mAccount, mTelephone, mNickName, mImageUrl, mSignature, mBirthday, mAge, mSex, mHeight, mWeight, mAddress, mLatitude, mLongitude, mRongToken);
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
