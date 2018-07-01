package com.todoplanner.matthewwen.todoplanner.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;

//Author Matthew Wen

public class TaskJobService extends com.firebase.jobdispatcher.JobService {


    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }
}
