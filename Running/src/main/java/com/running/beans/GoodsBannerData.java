package com.running.beans;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class GoodsBannerData {
    private int pic;
    private String weburl;

    public GoodsBannerData(int pic, String weburl) {
        this.pic = pic;
        this.weburl = weburl;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }
}
