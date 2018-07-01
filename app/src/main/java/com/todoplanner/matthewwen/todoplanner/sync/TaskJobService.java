package com.todoplanner.matthewwen.todoplanner.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;

//Author Matthew Wen

public class TaskJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
