package cn.orechou.cs.sleeping.Entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Zheli Liu on 14/10/2016.
 */

public class SleepNode implements Serializable{
    private float coordinateValue;
    private Date saveDate;

    public SleepNode(float coordinateValue, Date saveDate) {
        this.coordinateValue = coordinateValue;
        this.saveDate = saveDate;
    }

    public float getCoordinateValue() {
        return coordinateValue;
    }

    public void setCoordinateValue(float coordinateValue) {
        this.coordinateValue = coordinateValue;
    }

    public Date getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(Date saveDate) {
        this.saveDate = saveDate;
    }
}
