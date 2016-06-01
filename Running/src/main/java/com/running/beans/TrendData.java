package com.running.beans;


import java.util.Date;

/**
 * Created by C5-0 on 2016/5/28.
 */
public class TrendData {

    private Date Monday;
    private int walkStep;
    private double distance;
    private int time;
    private double calorie;
    private double[] value;
    public TrendData(Date monday, int walkStep, double distance, int time, double calorie, double[] value) {
        this.Monday = monday;
        this.walkStep = walkStep;
        this.distance = distance;
        this.time = time;
        this.calorie = calorie;
        this.value = value;
    }

    public Date getMonday() {
        return Monday;
    }

    public void setMonday(Date monday) {
        Monday = monday;
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
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
