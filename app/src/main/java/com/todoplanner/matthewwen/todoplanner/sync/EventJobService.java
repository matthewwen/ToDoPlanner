package com.todoplanner.matthewwen.todoplanner.sync;

import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.JobParameters;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

//Author Matthew Xiangyu Wen
//Date is 7-9-18

public class EventJobService extends JobService {

    private AsyncTask<Void, Void, Void> mAsyncTask;
    private static final String TAG = EventJobService.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters job) {
        mAsyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                NotificationsUtils.displayCalendarNotification(EventJobService.this,
                        "Event Job Service",
                        "1pm to 5pm",
                        "Matthew Home");
                Log.v(TAG, "Notification is created");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.v(TAG, "saying that the job is finished");
                jobFinished(job, false);
            }
        };
        mAsyncTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mAsyncTask != null)  mAsyncTask.cancel(true);
        return true;
    }
}
