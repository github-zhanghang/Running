package com.running.beans;

import java.text.DecimalFormat;

/**
 * Created by ZhangHang on 2016/5/25.
 */
public class RankItemInfo {
    private String position;
    private String imgUrl;
    private String nickName;
    private String distance;
    private DecimalFormat mDecimalFormat = new DecimalFormat("#####0.00");

    public RankItemInfo(String position, String nickName, String imgUrl, String distance) {
        this.distance = mDecimalFormat.format(Double.parseDouble(distance)) + "km";
        this.nickName = nickName;
        this.position = position;
        this.imgUrl = imgUrl;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
