package com.running.beans;

import java.io.Serializable;

/**
 * Created by ldd on 2016/5/30.
 * 一级评论bean
 */
public class FirstCommentBean implements Serializable {
    private int id;
    private int toId;
    private String name;
    private String content;
    private String time;

    public FirstCommentBean(int id, int toId, String name, String content, String time) {
        this.id = id;
        this.toId = toId;
        this.name = name;
        this.content = content;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
