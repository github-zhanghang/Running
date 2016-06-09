package com.running.beans;


import java.util.Date;

/**
 * Created by C5-0 on 2016/5/28.
 */
public class TrendData {

    private String Monday;
    private String Sunday;
    private int walkStep;
    private double distance;
    private long time;
    private double calorie;
    private double[] value={0,0,0,0,0,0,0};

    public TrendData() {

    }

    public TrendData(String monday, String sunday, int walkStep, double distance, long time, double calorie, double[] value) {
        Monday = monday;
        Sunday = sunday;
        this.walkStep = walkStep;
        this.distance = distance;
        this.time = time;
        this.calorie = calorie;
        this.value = value;
    }

    public String getMonday() {
        return Monday;
    }

    public void setMonday(String monday) {
        Monday = monday;
    }

    public String getSunday() {
        return Sunday;
    }

    public void setSunday(String sunday) {
        Sunday = sunday;
    }

    public int getWalkStep() {
        return walkStep;
    }

    public void setWalkStep(int walkStep) {
        this.walkStep = walkStep;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long  getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double[] getValue() {
        return value;
    }

    public void setValue(double[] value) {
        this.value = value;
    }
}
