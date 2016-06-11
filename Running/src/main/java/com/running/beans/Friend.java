package com.running.beans;

import java.io.Serializable;

public class Friend implements Serializable {

	private String sortLetters;//显示数据拼音的首字母

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	private int meid;
	/**
	 * 用户id
	 */
	 private int friendid;
	/**
     * 用户userid(账号)
     */
    private String account;
    /**
     *备注
     */
    private String remark;
    /**
     *头像
     */
    private String portrait;
    /**
     * 
     */
    private String friendtime;
    /**
     *状态
     */
    private int status;
    /**
     *token值
     */
    private String token;

	public int getMeid() {
		return meid;
	}
	public void setMeid(int meid) {
		this.meid = meid;
	}
    
	public int getFriendid() {
		return friendid;
	}
	public void setFriendid(int friendid) {
		this.friendid = friendid;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getPortrait() {
		return portrait;
	}
	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}
	public String getFriendtime() {
		return friendtime;
	}
	public void setFriendtime(String friendtime) {
		this.friendtime = friendtime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	private int age;
	private String sex;
	private String address;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
