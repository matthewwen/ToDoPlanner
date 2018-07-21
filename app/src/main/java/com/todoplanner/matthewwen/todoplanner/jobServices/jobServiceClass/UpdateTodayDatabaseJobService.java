package com.todoplanner.matthewwen.todoplanner.jobServices.jobServiceClass;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

import com.todoplanner.matthewwen.todoplanner.jobServices.JobServiceMethods;

public class UpdateTodayDatabaseJobService extends JobService{

    private AsyncTask<Void, Void, Void> mAsync;

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters params) {
        mAsync = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                JobServiceMethods.settingUrgentPendingToToday(UpdateTodayDatabaseJobService.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(params, false);
                super.onPostExecute(aVoid);
            }
        };
        mAsync.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mAsync.cancel(true);
        return false;
    }
}
