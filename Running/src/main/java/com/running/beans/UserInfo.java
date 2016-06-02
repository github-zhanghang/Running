package com.running.beans;

/**
 * Created by ZhangHang on 2016/6/1.
 */
public class UserInfo {
    private String mCode;// 状态码
    private String mAccount;
    private String mTelephone;
    private String mNickName;
    private String mImageUrl;
    private String mSingnature;
    private int mAge;
    private String mSex;
    private int mHeight;
    private int mWeight;
    private String mAddress;
    private double mLatitude;
    private double mLongitude;
    private String mRongToken;

    public UserInfo(String mCode, String mAccount, String mTelephone, String mNickName,
                    String mImageUrl, String mSingnature, int mAge, String mSex,
                    int mHeight, int mWeight, String mAddress, double mLatitude,
                    double mLongitude, String mRongToken) {
        super();
        this.mCode = mCode;
        this.mAccount = mAccount;
        this.mTelephone = mTelephone;
        this.mNickName = mNickName;
        this.mImageUrl = mImageUrl;
        this.mSingnature = mSingnature;
        this.mAge = mAge;
        this.mSex = mSex;
        this.mHeight = mHeight;
        this.mWeight = mWeight;
        this.mAddress = mAddress;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mRongToken = mRongToken;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String account) {
        mAccount = account;
    }

    public String getTelephone() {
        return mTelephone;
    }

    public void setTelephone(String telephone) {
        mTelephone = telephone;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getSingnature() {
        return mSingnature;
    }

    public void setSingnature(String singnature) {
        mSingnature = singnature;
    }

    public int getAge() {
        return mAge;
    }

    public void setAge(int age) {
        mAge = age;
    }

    public String getSex() {
        return mSex;
    }

    public void setSex(String sex) {
        mSex = sex;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public int getWeight() {
        return mWeight;
    }

    public void setWeight(int weight) {
        mWeight = weight;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getRongToken() {
        return mRongToken;
    }

    public void setRongToken(String rongToken) {
        mRongToken = rongToken;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "mCode=" + mCode +
                ", mAccount='" + mAccount + '\'' +
                ", mTelephone='" + mTelephone + '\'' +
                ", mNickName='" + mNickName + '\'' +
                ", mImageUrl='" + mImageUrl + '\'' +
                ", mSingnature='" + mSingnature + '\'' +
                ", mAge=" + mAge +
                ", mSex='" + mSex + '\'' +
                ", mHeight=" + mHeight +
                ", mWeight=" + mWeight +
                ", mAddress='" + mAddress + '\'' +
                ", mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mRongToken='" + mRongToken + '\'' +
                '}';
    }
}
