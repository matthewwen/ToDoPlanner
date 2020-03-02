package com.matthewwen.todoplanner.object;

public class section {
    long id;
    public String name;
    long duedate;
    long complete;

    public section(long id, String name, long duedate, long complete) {
        this.id = id;
        this.name = name;
        this.duedate = duedate;
        this.complete = complete;
    }

}
