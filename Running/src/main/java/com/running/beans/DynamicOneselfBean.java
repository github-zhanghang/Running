package com.running.beans;

import java.io.Serializable;
import java.util.List;

/**
 * 个人动态头部bean
 * Created by ldd on 2016/6/4.
 */
public class DynamicOneselfBean implements Serializable {
    private int uId;
    private String uSex;
    private int dId;
    private String name;
    private String headPhoto;
    private List<String> imgList;
    private String content;
    private String time;
    private String location;
    private int praiseCount;
    private int commentCount;
    private int praiseStatus;

    public DynamicOneselfBean(int uId, String uSex, int dId, String
            name, String headPhoto, List<String> imgList, String content, String time, String
            location, int praiseCount, int commentCount, int praiseStatus) {
        this.uId = uId;
        this.uSex = uSex;
        this.dId = dId;
        this.name = name;
        this.headPhoto = headPhoto;
        this.imgList = imgList;
        this.content = content;
        this.time = time;
        this.location = location;
        this.praiseCount = praiseCount;
        this.commentCount = commentCount;
        this.praiseStatus = praiseStatus;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public String getuSex() {
        return uSex;
    }

    public void setuSex(String uSex) {
        this.uSex = uSex;
    }

    public int getdId() {
        return dId;
    }

    public void setdId(int dId) {
        this.dId = dId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getPraiseStatus() {
        return praiseStatus;
    }

    public void setPraiseStatus(int praiseStatus) {
        this.praiseStatus = praiseStatus;
    }

}
