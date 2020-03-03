package com.matthewwen.todoplanner.object;

public class TodoTasks {
    long section;
    long complete;
    long duedate;
    public String name;
    public long id;

    public TodoTasks(long id, String name, long duedate, long complete, long section) {
        this.id = id;
        this.name = name;
        this.duedate = duedate;
        this.complete = complete;
        this.section = section;
    }
}
