package cn.orechou.cs.sleeping.Entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Zheli Liu on 14/10/2016.
 */
// all basic function of get\set
public class SleepRecord implements Serializable{
    private ArrayList<SleepNode> mSleepNodes = new ArrayList<>();
    private Boolean isGoodSleeping = true;
    private double latitude;
    private double longitude;
    private float sleepEfficiency;

    public SleepRecord(ArrayList<SleepNode> sleepNodes, Boolean isGoodSleeping, double latitude, double longitude) {
        mSleepNodes = sleepNodes;
        this.isGoodSleeping = isGoodSleeping;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public ArrayList<SleepNode> getSleepNodes() {
        return mSleepNodes;
    }

    public void setSleepNodes(ArrayList<SleepNode> sleepNodes) {
        mSleepNodes = sleepNodes;
    }

    public Boolean getGoodSleeping() {
        return isGoodSleeping;
    }

    public void setGoodSleeping(Boolean goodSleeping) {
        isGoodSleeping = goodSleeping;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getSleepEfficiency() {
        return sleepEfficiency;
    }

    public void setSleepEfficiency(float sleepEfficiency) {
        this.sleepEfficiency = sleepEfficiency;
    }
}
