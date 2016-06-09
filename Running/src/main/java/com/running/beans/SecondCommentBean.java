package com.running.beans;

import java.io.Serializable;

/**
 * Created by ldd on 2016/5/30.
 * 二级评论bean
 */
public class SecondCommentBean implements Serializable {
    private int sCId;
    private int sFCId;
    private int uId0;
    private String uName0;
    private int uId1;
    private String uName1;
    private String sContent;
    private String sTime;

    public SecondCommentBean(int sFCId, int uId0, String uName0, int uId1, String uName1, String
            sContent, String sTime) {
        this.sFCId = sFCId;
        this.uId0 = uId0;
        this.uName0 = uName0;
        this.uId1 = uId1;
        this.uName1 = uName1;
        this.sContent = sContent;
        this.sTime = sTime;
    }

    public SecondCommentBean(int sCId, int sFCId, int uId0, String uName0,
                             int uId1, String uName1, String sContent, String sTime) {
        super();
        this.sCId = sCId;
        this.sFCId = sFCId;
        this.uId0 = uId0;
        this.uName0 = uName0;
        this.uId1 = uId1;
        this.uName1 = uName1;
        this.sContent = sContent;
        this.sTime = sTime;
    }
    public int getsCId() {
        return sCId;
    }
    public void setsCId(int sCId) {
        this.sCId = sCId;
    }
    public int getsFCId() {
        return sFCId;
    }
    public void setsFCId(int sFCId) {
        this.sFCId = sFCId;
    }
    public int getuId0() {
        return uId0;
    }
    public void setuId0(int uId0) {
        this.uId0 = uId0;
    }
    public String getuName0() {
        return uName0;
    }
    public void setuName0(String uName0) {
        this.uName0 = uName0;
    }
    public int getuId1() {
        return uId1;
    }
    public void setuId1(int uId1) {
        this.uId1 = uId1;
    }
    public String getuName1() {
        return uName1;
    }
    public void setuName1(String uName1) {
        this.uName1 = uName1;
    }
    public String getsContent() {
        return sContent;
    }
    public void setsContent(String sContent) {
        this.sContent = sContent;
    }
    public String getsTime() {
        return sTime;
    }
    public void setsTime(String sTime) {
        this.sTime = sTime;
    }
}
