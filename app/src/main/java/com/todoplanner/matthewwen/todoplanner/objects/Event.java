package com.todoplanner.matthewwen.todoplanner.objects;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;

public class Event implements Story {

    private int id;
    private String eventName;
    private long eventStart;
    private long eventEnd;
    private int taskId;

    public Event(int id, String eventName, long eventStart, long eventEnd, int taskId){
        this.id = id; this.eventName = eventName; this.eventStart = eventStart; this.eventEnd = eventEnd; this.taskId = taskId;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getStoryType() {
        return DataContract.EventEntry.TABLE_NAME;
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
}
