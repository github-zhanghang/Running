package com.running.beans;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ldd on 2016/5/29.
 * 链接动态bean
 */
public class DynamicLinkBean implements Serializable {
    private int headImg;
    private String name;
    private String time;
    private String content;
    private Map<String,Object> mLink;
    private int praiseCount;
    private int commentCount;
    private String type;

    public DynamicLinkBean(int headImg, String name, String time, String content, Map<String,
            Object> link, int praiseCount, int commentCount, String type) {
        this.headImg = headImg;
        this.name = name;
        this.time = time;
        this.content = content;
        mLink = link;
        this.praiseCount = praiseCount;
        this.commentCount = commentCount;
        this.type = type;
    }

    public int getHeadImg() {
        return headImg;
    }

    public void setHeadImg(int headImg) {
        this.headImg = headImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getLink() {
        return mLink;
    }

    public void setLink(Map<String, Object> link) {
        mLink = link;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
