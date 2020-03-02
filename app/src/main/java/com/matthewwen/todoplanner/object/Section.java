package com.matthewwen.todoplanner.object;

public class Section {
    public long id;
    public String name;
    long duedate;
    long complete;

    public Section(long id, String name, long duedate, long complete) {
        this.id = id;
        this.name = name;
        this.duedate = duedate;
        this.complete = complete;
    }

}
