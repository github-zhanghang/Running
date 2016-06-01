package com.running.beans;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class RaceData {
    private String picUrl;
    private String title;
    private String time;
    private String num;
    private String webUrl;
    public RaceData(String picUrl, String title, String time, String num, String webUrl) {
        this.picUrl = picUrl;
        this.title = title;
        this.time = time;
        this.num = num;
        this.webUrl = webUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
