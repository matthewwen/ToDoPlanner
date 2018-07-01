package com.todoplanner.matthewwen.todoplanner.sync;


public class NotesJobService extends com.firebase.jobdispatcher.JobService {

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        return false;
    }
}
