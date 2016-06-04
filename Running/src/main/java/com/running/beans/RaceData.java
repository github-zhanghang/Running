package com.running.beans;


/**
 * Created by C5-0 on 2016/5/27.
 */
public class RaceData {
    private String name;
    private String time;
    private String img;
    private String html;
    private String location;
    public RaceData() {
        super();
    }
    public RaceData(String name, String time, String img, String html,
                String location) {
        super();
        this.name = name;
        this.time = time;
        this.img = img;
        this.html = html;
        this.location = location;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
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
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}
