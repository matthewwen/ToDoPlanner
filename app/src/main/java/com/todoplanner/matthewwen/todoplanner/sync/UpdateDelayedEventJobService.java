package com.todoplanner.matthewwen.todoplanner.sync;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.content.ContentValues;
import android.content.Context;

import android.app.job.JobService;
import android.os.AsyncTask;
import android.widget.Toast;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UpdateDelayedEventJobService extends JobService {

    private static final String EVENT_NAME = "Job Service";
    private static final String NOTE = "Job Service Works";
    private static final int TASK_ID = -1;

    private static final String TAG = UpdateDelayedEventJobService.class.getSimpleName();

    private static AsyncTask<Void, Void, Void> mAsyncTask;

    //add event to the database
    public void addEvent(Context context){
        long start = new Date().getTime();
        long end = TimeUnit.MINUTES.toMillis(15) + start;
        ContentValues values = new ContentValues();
        values.put(DataContract.EventEntry.COLUMN_EVENT_NAME, EVENT_NAME);
        values.put(DataContract.EventEntry.COLUMN_EVENT_START, start);
        values.put(DataContract.EventEntry.COLUMN_EVENT_END, end);
        values.put(DataContract.EventEntry.COLUMN_EVENT_NOTE, NOTE);
        values.put(DataContract.EventEntry.COLUMN_EVENT_TASK_ID, TASK_ID);

        context.getContentResolver().insert(DataContract.EventEntry.EVENT_CONTENT_URI, values);
    }


   @SuppressLint("StaticFieldLeak")
   @Override
    public boolean onStartJob(final JobParameters params) {
        mAsyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                addEvent(UpdateDelayedEventJobService.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                jobFinished(params, false);

            }
        };

        mAsyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (mAsyncTask != null) mAsyncTask.cancel(true);
        return true;
    }

}
