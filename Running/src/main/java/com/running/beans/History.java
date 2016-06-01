package com.running.beans;

/**
 * Created by C5-0 on 2016/5/27.
 */
public class History {
    private String date;
    private String distance;
    private String time;

    public History(String date, String distance, String time) {
        this.date = date;
        this.distance = distance;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
