package com.running.beans;

/**
 * Created by C5-0 on 2016/6/4.
 */
public class SumRecord {
    private int sumuid;
    private double sumrundistance;
    private long sumruntime;
    private int sumcalories;
    private long sumstep;
    private int sumcount;

    public SumRecord(int sumuid, double sumrundistance, long sumruntime, int sumcalories, long sumstep, int sumcount) {
        this.sumuid = sumuid;
        this.sumrundistance = sumrundistance;
        this.sumruntime = sumruntime;
        this.sumcalories = sumcalories;
        this.sumstep = sumstep;
        this.sumcount = sumcount;
    }

    public int getSumuid() {
        return sumuid;
    }

    public void setSumuid(int sumuid) {
        this.sumuid = sumuid;
    }

    public double getSumrundistance() {
        return sumrundistance;
    }

    public void setSumrundistance(double sumrundistance) {
        this.sumrundistance = sumrundistance;
    }

    public long getSumruntime() {
        return sumruntime;
    }

    public void setSumruntime(long sumruntime) {
        this.sumruntime = sumruntime;
    }

    public int getSumcalories() {
        return sumcalories;
    }

    public void setSumcalories(int sumcalories) {
        this.sumcalories = sumcalories;
    }

    public long getSumstep() {
        return sumstep;
    }

    public void setSumstep(long sumstep) {
        this.sumstep = sumstep;
    }

    public int getSumcount() {
        return sumcount;
    }

    public void setSumcount(int sumcount) {
        this.sumcount = sumcount;
    }
}
