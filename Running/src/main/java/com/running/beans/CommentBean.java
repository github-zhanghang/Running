package com.running.beans;

import java.io.Serializable;
import java.util.List;

/**
 * 评论bean
 * Created by ldd on 2016/6/6.
 */
public class CommentBean implements Serializable {
    private int fCId;
    private int fDId;
    private int fUId;
    private String fUImg;
    private String fName;
    private String fContent;
    private String fTime;
    private List<SecondCommentBean> list;

    public CommentBean(int fDId, int fUId, String fUImg, String fName, String fContent, String
            fTime, List<SecondCommentBean> list) {
        this.fDId = fDId;
        this.fUId = fUId;
        this.fUImg = fUImg;
        this.fName = fName;
        this.fContent = fContent;
        this.fTime = fTime;
        this.list = list;
    }

    public CommentBean(int fDId, int fUId, String fUImg, String fName, String fContent, String
            fTime) {
        this.fDId = fDId;
        this.fUId = fUId;
        this.fUImg = fUImg;
        this.fName = fName;
        this.fContent = fContent;
        this.fTime = fTime;
    }

    public CommentBean(int fCId, int fDId, int fUId, String fUImg, String fName, String fContent,
                       String fTime, List<SecondCommentBean> list) {
        super();
        this.fCId = fCId;
        this.fDId = fDId;
        this.fUId = fUId;
        this.fUImg = fUImg;
        this.fName = fName;
        this.fContent = fContent;
        this.fTime = fTime;
        this.list = list;
    }

    public int getfCId() {
        return fCId;
    }

    public void setfCId(int fCId) {
        this.fCId = fCId;
    }

    public int getfDId() {
        return fDId;
    }

    public void setfDId(int fDId) {
        this.fDId = fDId;
    }

    public int getfUId() {
        return fUId;
    }

    public void setfUId(int fUId) {
        this.fUId = fUId;
    }

    public String getfUImg() {
        return fUImg;
    }

    public void setfUImg(String fUImg) {
        this.fUImg = fUImg;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getfContent() {
        return fContent;
    }

    public void setfContent(String fContent) {
        this.fContent = fContent;
    }

    public String getfTime() {
        return fTime;
    }

    public void setfTime(String fTime) {
        this.fTime = fTime;
    }

    public List<SecondCommentBean> getList() {
        return list;
    }

    public void setList(List<SecondCommentBean> list) {
        this.list = list;
    }
}
