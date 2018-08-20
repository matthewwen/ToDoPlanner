package com.todoplanner.matthewwen.todoplanner.jobServices;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.alarmService.methods.SetAlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.data.PreferenceUtils;
import com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.adaption.DelayBehavior;
import com.todoplanner.matthewwen.todoplanner.jobServices.jobServiceClass.NotifyMoveEventJobService;
import com.todoplanner.matthewwen.todoplanner.jobServices.jobServiceClass.UpdateTodayDatabaseJobService;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JobServiceMethods {

    private static final String TAG = JobServiceMethods.class.getSimpleName();

    //ID and Tag for Job Services
    public static final int DELAY_AND_NOTIFY = 1;
    public static final int UPDATE_TODAY_DATABASE = 2;
    //ID and Tag for type of service.
    private static final int DELAY_EVERY_EVENT = 1;
    private static final int PROPORTION_DELAY = 3;
    //Bundle Keys
    private static final String EVENT_URI_BUNDLE_KEY = "event-uri-bundle-key";

    /**
     *All The Methods to Create the Job Service
     */
    //Delay current event every 15 minutes
    public static void automatedDelayEventJobService(Context context){
        ComponentName serviceName = new ComponentName(context.getPackageName(), NotifyMoveEventJobService.class.getName());
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        if (jobExist(DELAY_AND_NOTIFY, scheduler.getAllPendingJobs())){
            cancelEventJobService(context, DELAY_AND_NOTIFY);
        }
        JobInfo.Builder builder = new JobInfo.Builder(DELAY_AND_NOTIFY, serviceName)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .setPersisted(true);
        scheduler.schedule(builder.build());
        Log.v(TAG, "Job Service is created");
    }
    //Get all Events from Pending
    public static void automatedMoveToTodayJobService(Context context){
        ComponentName serviceName = new ComponentName(context.getPackageName(), UpdateTodayDatabaseJobService.class.getName());
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        JobInfo.Builder builder = new JobInfo.Builder(UPDATE_TODAY_DATABASE, serviceName)
                .setPeriodic(TimeUnit.HOURS.toMillis(6))
                .setPersisted(true);
        scheduler.schedule(builder.build());
    }

    /**
     *All the Utilities
     */
    //see if the job already exists
    private static boolean jobExist(int id, List<JobInfo> allJobs){
        for (int i = 0 ; i < allJobs.size(); i++){
            if (allJobs.get(i).getId() == id){
                return true;
            }
        }
        return false;
    }

    //cancel the interval 15 minutes job thing
    public static void cancelEventJobService(Context context, int id){
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        scheduler.cancel(id);
    }

    //get the type of operation to do
    public static int getType(ArrayList<Event> pendingEvents){
        for (Event temp: pendingEvents){
            if (temp.isStatic()){
                return PROPORTION_DELAY;
            }
        }
        return DELAY_EVERY_EVENT;
    }

    /**
     * Methods/Actions that the application does
     */
    //update current event to extend to current time
    public static void eventExtend(Context context, ArrayList<Event> pendingEvents, long current){
        if (pendingEvents == null){
            return;
        }
        //get the event in progress
        Event inProgress = DataMethods.getEventInProgress(pendingEvents);
        if (inProgress == null){
            return;
        }
        pendingEvents.remove(inProgress);
        //making the event extend by 15 minutes and show notification
        inProgress.setEventEnd(current);
        boolean showNotification = PreferenceUtils.getNotifyEndJobService(context);
        if (showNotification) {
            NotificationsUtils.displayCalendarNotification(context, inProgress, NotificationsUtils.EVENT_REMINDER_END);
        }
        DataMethods.updateTodayEvent(context, inProgress);
    }
    //update database
    public static void settingUrgentPendingToToday(Context context){
        long limit = TimeUnit.HOURS.toMillis(12) + Calendar.getInstance().getTimeInMillis();
        ArrayList<Event> allNewPending = DataMethods.getEssentialPendingEvents(context, limit);

        if (allNewPending == null){
            return;
        }

        for (Event temp: allNewPending){
            DataMethods.deletePendingEvent(context, temp);
            DataMethods.insertTodayEvent(context, temp);
        }

        //set next alarm service
        SetAlarmServiceMethods.setAlarmService(context);
    }
    //check to see if update database job service exists
    public static boolean hasUpdateDatabaseJobService(Context context){
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        return jobExist(UPDATE_TODAY_DATABASE, scheduler.getAllPendingJobs());
    }
    //automatically create job service, no notification need to be shown aka if it is shown and in progress. Make it the end and start job service
    public static void justCreateJobServiceDelay(Context context, Event tempProg, long currentTime){
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        if (jobExist(DELAY_AND_NOTIFY, scheduler.getAllPendingJobs())){
            return;
        }
        tempProg.setEventEnd(currentTime);
        DataMethods.updateTodayEvent(context, tempProg);
        cancelEventJobService(context, JobServiceMethods.DELAY_AND_NOTIFY);
        automatedDelayEventJobService(context);
    }

}
