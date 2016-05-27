package com.running.beans;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class RaceBannerData {
    private int pic;
    private String title;
    private String weburl;

    public RaceBannerData(int pic, String title, String weburl) {
        this.pic = pic;
        this.title = title;
        this.weburl = weburl;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }
}
