package com.todoplanner.matthewwen.todoplanner.objects;

import com.todoplanner.matthewwen.todoplanner.data.DataMethods;

import java.sql.Time;

public class TimeRange {

    private double range;
    private long id;

    public TimeRange(long range){
        id = -1;
        this.range = (double) range;
    }

    public TimeRange(int id, long range){
        this.id = id;
        this.range = range;
    }

    public void setNewRange(double m){
        range = range * m;
    }

    public long getNewRange(){
        return DataMethods.roundNearestMinute((long) range);
    }

    public boolean isEvent(){
        return id != -1;
    }
}
