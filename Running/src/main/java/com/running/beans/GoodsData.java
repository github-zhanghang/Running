package com.running.beans;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class GoodsData {
    private String name;
    private double price;
    private String img;
    private String html;

    public GoodsData(String name, double price, String img, String html) {
        this.name = name;
        this.price = price;
        this.img = img;
        this.html = html;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
