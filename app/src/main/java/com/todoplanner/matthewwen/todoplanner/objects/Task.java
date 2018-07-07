package com.todoplanner.matthewwen.todoplanner.objects;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Task implements Story{

    private int id;
    private String taskName;
    private Date date;
    private int parent;


    //for the developer
    private long dateMil;
    private int subTask;

    //for the developer option
    public Task(int id, String taskName, long dateMil, int subTask, int parent){
        //for the user
        this.id = id;
        this.taskName = taskName;
        this.parent = parent;
        date = null;

        //for the developer
        this.dateMil = dateMil;
        this.subTask = subTask;

    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getStoryType() {
        return DataContract.TaskEntry.TABLE_NAME;
    }

    public String getTaskName() {
        return taskName;
    }

    public long getDevDueDate(){
        return dateMil;
    }

    public int devSubTask(){
        return subTask;
    }

    public int getParent(){
        return parent;
    }

    public String getDateOverview(){
        Date current = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);

        //check if the due date is today
        if (format.format(current).equals(format.format(date))){
            return "Today";
        }

        return "";
    }
}
