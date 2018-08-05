package com.todoplanner.matthewwen.todoplanner.objects;

import android.net.Uri;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;

public class Event implements Story {

    private int id;
    private String eventName;
    private long eventStart;
    private long eventEnd;
    private int taskId;
    private String note;
    private int inProgress;
    private int staticInt;
    private int alarmSet;

    //This is for the today view
    public Event(int id, String eventName, long eventStart, long eventEnd, int taskId, String note, int inProgress, int staticInt, int alarmSet){
        this.id = id; this.eventName = eventName; this.eventStart = eventStart; this.eventEnd = eventEnd; this.taskId = taskId;
        this.inProgress = inProgress; this.note = note; this.staticInt = staticInt; this.alarmSet = alarmSet;
    }

    //This is for pending
    public Event(int id, String eventName, long eventStart, long eventEnd, String note, int taskId,  int staticInt){
        this.id = id; this.eventName = eventName; this.eventStart = eventStart; this.eventEnd = eventEnd; this.note = note;
        this.taskId = taskId; this.staticInt = staticInt; alarmSet = DataContract.TodayEventEntry.ALARM_NOT_SET;
    }

    //This is for past
    public Event(int id, String name, long start, long end, String note, int taskId){
        this.id = id; this.eventName = name; this.eventStart = start; this.eventEnd = end; this.note = note; this.taskId = taskId;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getStoryType() {
        return DataContract.TodayEventEntry.TABLE_NAME;
    }

    public String getEventName() {
        return eventName;
    }

    public long getEventStart() {
        return eventStart;
    }

    public long getEventEnd() {
        return eventEnd;
    }

    public int getTaskId() {
        return taskId;
    }

    public int getTheProgress(){
        return inProgress;
    }

    public boolean getInProgress(){
        return inProgress == DataContract.TodayEventEntry.EVENT_IN_PROGRESS;
    }

    public boolean isStatic(){
        return staticInt == DataContract.TodayEventEntry.EVENT_STATIONARY;
    }

    public String getNote() {
        return note;
    }

    public int getStaticInt(){
        return staticInt;
    }

    public void setEventStart(long eventStart){
        this.eventStart = eventStart;
    }

    public void setEventEnd(long eventEnd){
        this.eventEnd = eventEnd;
    }

    public void setInProgress(){
        inProgress = DataContract.TodayEventEntry.EVENT_IN_PROGRESS;
    }

    public void setNotInProgress(){inProgress = DataContract.TodayEventEntry.EVENT_NOT_IN_PROGRESS;}

    public long getRange(){
        return eventEnd - eventStart;
    }

    public int getAlarmSet(){
        return alarmSet;
    }

    public void setAlarmSet(){
        alarmSet = DataContract.TodayEventEntry.ALARM_SET;
    }

    public boolean isAlarmSet(){
        return alarmSet == DataContract.TodayEventEntry.ALARM_SET;
    }
}
