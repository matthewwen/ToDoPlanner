package com.matthewwen.todoplanner.object;

import java.util.ArrayList;

public class Section {
    public long id;
    public String name;
    public long duedate;
    public long complete;

    public ArrayList<TodoTasks> allTask;

    public Section(long id, String name, long duedate, long complete) {
        this.id = id;
        this.name = name;
        this.duedate = duedate;
        this.complete = complete;
        this.allTask = null;
    }

}
