package com.running.beans;

import java.io.Serializable;

/**
 * Created by ldd on 2016/5/30.
 * 二级评论bean
 */
public class SecondCommentBean implements Serializable {
    private int id;
    private int toId;
    private String name;
    private String toName;
    private int cId;
    private String content;
    private String time;

    public SecondCommentBean(int id, int toId, String name, String toName, String content, String
            time) {
        this.id = id;
        this.toId = toId;
        this.name = name;
        this.toName = toName;
        this.cId = 2333;
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

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
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
