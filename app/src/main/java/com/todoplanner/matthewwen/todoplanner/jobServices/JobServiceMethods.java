package com.todoplanner.matthewwen.todoplanner.jobServices;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class JobServiceMethods {

    private static final String TAG = JobServiceMethods.class.getSimpleName();

    //ID and Tag for Job Services
    private static final int DEVELOPER_JOB_SERVICE_TEST = 1;

    //see if the job already exists
    private static boolean jobExist(int id, List<JobInfo> allJobs){
        for (int i = 0 ; i < allJobs.size(); i++){
            if (allJobs.get(i).getId() == id){
                return true;
            }
        }
        return false;
    }

    //create the job service that changes the event every 15 minutes
    public static void createEventJobService(Context context){
//        ComponentName serviceName = new ComponentName(context.getPackageName(), EveryFifteenMinutesJobService.class.getName());
//        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        assert scheduler != null;
//        if (!JobServiceMethods.jobExist(DEVELOPER_JOB_SERVICE_TEST, scheduler.getAllPendingJobs())) {
//            JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(DEVELOPER_JOB_SERVICE_TEST, serviceName)
//                    .setPersisted(true)
//                    .setPeriodic(TimeUnit.MINUTES.toMillis(15));
//            scheduler.schedule(jobInfoBuilder.build());
//            Log.v(TAG, "Job was created");
//
//        }else{
//            Log.v(TAG, "Job already existed");
//        }
    }

    //cancel the interval 15 minutes job thing
    public static void cancelEventJobService(Context context){
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        scheduler.cancel(DEVELOPER_JOB_SERVICE_TEST);
    }

}
