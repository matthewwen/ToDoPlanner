package com.todoplanner.matthewwen.todoplanner.jobServices.jobServiceClass;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;

import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.data.PreferenceUtils;
import com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.adaption.DelayBehavior;
import com.todoplanner.matthewwen.todoplanner.jobServices.JobServiceMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class NotifyMoveEventJobService extends JobService{

    @Override
    public boolean onStartJob(final JobParameters params) {
        //get time and then list of arrays.
        final long current = DataMethods.getCurrentTime(this);
        final ArrayList<Event> allEvents = DataMethods.getNecessaryTodayEvents(this);
        JobServiceMethods.eventExtend(this, allEvents, current);
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void , Void> mAsyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... Void) {
                Context context = NotifyMoveEventJobService.this;
                DelayBehavior.delayAllEvents(context, allEvents, current);
                JobServiceMethods.eventExtend(context, allEvents, current);
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
        return false;
    }
}
