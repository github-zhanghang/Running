package com.running.beans;

/**
 * Created by ZhangHang on 2016/5/26.
 */
public class MedalItemInfo {
    private int imgId;
    private String description;

    public MedalItemInfo(int imgId, String description) {
        this.imgId = imgId;
        this.description = description;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
