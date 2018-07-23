package com.todoplanner.matthewwen.todoplanner.jobServices.jobServiceClass;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.todoplanner.matthewwen.todoplanner.jobServices.JobServiceMethods;

public class UpdateTodayDatabaseJobService extends JobService{
    @Override
    public boolean onStartJob(final JobParameters params) {
        JobServiceMethods.settingUrgentPendingToToday(UpdateTodayDatabaseJobService.this);

        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
