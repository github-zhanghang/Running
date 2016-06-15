package com.running.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ldd on 2016/5/29.
 * ImgItem动态bean
 */
public class DynamicImgBean implements Serializable {
    private int dId;
    private int dUId;
    private String name;
    private String headPhoto;
    private List<String> imgList;
    private String content;
    private String time;
    private String location;
    private int praiseCount;
    private int commentCount;
    private int praiseStatus;

    public DynamicImgBean(int dUId, List<String> imgList, String content, String time, String
            location) {
        this.dUId = dUId;
        this.imgList = imgList;
        this.content = content;
        this.time = time;
        this.location = location;
    }

    public DynamicImgBean(int dId, String name, String headPhoto, List<String> imgList, String
            content, String time, String location, int praiseCount, int commentCount, int dUId,int
            praiseStatus) {
        this.dId = dId;
        this.name = name;
        this.headPhoto = headPhoto;
        this.imgList = imgList;
        this.content = content;
        this.time = time;
        this.location = location;
        this.praiseCount = praiseCount;
        this.commentCount = commentCount;
        this.dUId = dUId;
        this.praiseStatus = praiseStatus;
    }

    public int getdId() {
        return dId;
    }

    public void setdId(int dId) {
        this.dId = dId;
    }

    public int getdUId() {
        return dUId;
    }

    public void setdUId(int dUId) {
        this.dUId = dUId;
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
