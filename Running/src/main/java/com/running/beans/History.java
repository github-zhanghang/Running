package com.running.beans;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by C5-0 on 2016/6/6.
 */
public class History implements Serializable{

    private int rid;
    private int ruid;
    private double rundistance;
    private long runtime;
    private double runspeed;
    private int calories;
    private int stepcount;
    private long runstarttime;
    private String target;
    private boolean complete;
    private boolean isExist;

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }



    public History(int rid, int ruid, double rundistance, long runtime, double runspeed, int calories, int stepcount,long runstarttime, String target, boolean complete) {

        this.rid = rid;
        this.ruid = ruid;
        this.rundistance = rundistance;
        this.runtime = runtime;
        this.runspeed = runspeed;
        this.calories = calories;
        this.stepcount = stepcount;
        this.runstarttime = runstarttime;
        this.target = target;
        this.complete = complete;
        this.isExist = true;
    }

    public History() {

    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getRuid() {
        return ruid;
    }

    public void setRuid(int ruid) {
        this.ruid = ruid;
    }

    public double getRundistance() {
        return rundistance;
    }

    public void setRundistance(double rundistance) {
        this.rundistance = rundistance;
    }

    public long getRuntime() {
        return runtime;
    }

    public void setRuntime(long runtime) {
        this.runtime = runtime;
    }

    public double getRunspeed() {
        return runspeed;
    }

    public void setRunspeed(double runspeed) {
        this.runspeed = runspeed;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getStepcount() {
        return stepcount;
    }

    public void setStepcount(int stepcount) {
        this.stepcount = stepcount;
    }

    public long getRunstarttime() {
        return runstarttime;
    }

    public void setRunstarttime(long runstarttime) {
        this.runstarttime = runstarttime;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
