package com.running.beans;

public class RankList {
	private String mNickName;
	private String mImageUrl;
	private String mDistance;
	public RankList(String mNickName,
			String mImageUrl, String mDistance) {
		super();
		this.mNickName = mNickName;
		this.mImageUrl = mImageUrl;
		this.mDistance = mDistance;
	}
	public String getmNickName() {
		return mNickName;
	}
	public void setmNickName(String mNickName) {
		this.mNickName = mNickName;
	}
	public String getmImageUrl() {
		return mImageUrl;
	}
	public void setmImageUrl(String mImageUrl) {
		this.mImageUrl = mImageUrl;
	}
	public String getmDistance() {
		return mDistance;
	}
	public void setmDistance(String mDistance) {
		this.mDistance = mDistance;
	}

	@Override
	public String toString() {
		return "RankList{" +
				"mNickName='" + mNickName + '\'' +
				", mImageUrl='" + mImageUrl + '\'' +
				", mDistance='" + mDistance + '\'' +
				'}';
	}
}
