package com.running.beans;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class GoodsData {
    private int pic;
    private String goodname;
    private  String price;
    private String weburl;

    public GoodsData(int pic, String goodname, String price, String weburl) {
        this.pic = pic;
        this.goodname = goodname;
        this.price = price;
        this.weburl = weburl;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public String getGoodname() {
        return goodname;
    }

    public void setGoodname(String goodname) {
        this.goodname = goodname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }
}
