package com.todoplanner.matthewwen.todoplanner.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class NotesJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
