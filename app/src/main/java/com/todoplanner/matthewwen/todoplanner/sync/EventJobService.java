package com.todoplanner.matthewwen.todoplanner.sync;

import android.content.ContentValues;
import android.content.Context;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EventJobService extends JobService {

    private static final String EVENT_NAME = "Job Service";
    private static final String NOTE = "Job Service Works";
    private static final int TASK_ID = -1;

    @Override
    public boolean onStartJob(JobParameters job) {
        return addEvent(this);
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    //add event to the database
    public boolean addEvent(Context context){
        long start = new Date().getTime();
        long end = TimeUnit.MINUTES.toMillis(15) + start;
        ContentValues values = new ContentValues();
        values.put(DataContract.EventEntry.COLUMN_EVENT_NAME, EVENT_NAME);
        values.put(DataContract.EventEntry.COLUMN_EVENT_START, start);
        values.put(DataContract.EventEntry.COLUMN_EVENT_END, end);
        values.put(DataContract.EventEntry.COLUMN_EVENT_NOTE, NOTE);
        values.put(DataContract.EventEntry.COLUMN_EVENT_TASK_ID, TASK_ID);

        context.getContentResolver().insert(DataContract.EventEntry.EVENT_CONTENT_URI, values);

        return true;
    }
}
