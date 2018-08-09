package com.todoplanner.matthewwen.todoplanner.jobServices.jobServiceClass;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.todoplanner.matthewwen.todoplanner.data.PreferenceUtils;
import com.todoplanner.matthewwen.todoplanner.jobServices.JobServiceMethods;

public class NotifyMoveEventJobService extends JobService{

    @Override
    public boolean onStartJob(JobParameters params) {
        boolean showNotification = PreferenceUtils.getNotifyEndJobService(this);
        JobServiceMethods.eventExtend(this, showNotification);
        jobFinished(params, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
