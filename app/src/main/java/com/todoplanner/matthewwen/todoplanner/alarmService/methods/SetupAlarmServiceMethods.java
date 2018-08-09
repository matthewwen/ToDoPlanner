package com.todoplanner.matthewwen.todoplanner.alarmService.methods;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.data.PreferenceUtils;
import com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.CommonBehavior;
import com.todoplanner.matthewwen.todoplanner.jobServices.JobServiceMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class SetupAlarmServiceMethods {

    private static final String TAG = SetupAlarmServiceMethods.class.getSimpleName();

    public static void setupStartEvent(Context context, Event event){
        //Update Preference Utils
        PreferenceUtils.resetNotifyEndJobService(context);
        //make event in progress
        event.setInProgress();
        event.setStartShown();
        DataMethods.updateTodayEvent(context, event);
        //display notification
        String type = NotificationsUtils.EVENT_REMINDER_START;
        NotificationsUtils.displayCalendarNotification(context, event, type);
        //get all the events
        ArrayList<Event> allEvents = DataMethods.getNecessaryTodayEvents(context);
        if (allEvents.get(0).getID() == event.getID()) {
            allEvents.remove(0);
        }
        //if nothing is left, do nothing
        if (allEvents.size() == 0){
            SetAlarmServiceMethods.setEndAlarmService(context, event);
            return;
        }
        //check the next event
        if (allEvents.get(0).isStatic()){
            if (event.getEventEnd() < allEvents.get(0).getEventStart()){
                //set up even end
                SetAlarmServiceMethods.setEndAlarmService(context, event);
            }
        }else {
            //set event end
            SetAlarmServiceMethods.setEndAlarmService(context, event);
        }
    }

    public static void setupStaticEvent(final Context context, final Event event){
        //display notification
        String type = NotificationsUtils.EVENT_REMINDER_START;
        NotificationsUtils.displayCalendarNotification(context, event, type);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //reset preference about job service
                PreferenceUtils.resetNotifyEndJobService(context);
                //cancel any job service
                CommonBehavior.cancelJobService(context);
                //make the event in progress
                event.setInProgress();
                event.setStartShown();
                DataMethods.updateTodayEvent(context, event);
                //change all the events
                ArrayList<Event> allEvents = DataMethods.getNecessaryTodayEvents(context);
                ArrayList<Integer> contentID = new ArrayList<>();
                //go through the list, get the pos of the event
                boolean found = false;
                while (!found){
                    if (allEvents.size() > 0){
                        if (allEvents.get(0).getID() == event.getID()){
                            found = true;
                        }else {
                            contentID.add(allEvents.remove(0).getID());
                        }
                    }else {
                        found = true;
                    }
                }
                //set up alarm end service
                if (allEvents.size() == 2){
                    if (!(allEvents.get(0).getEventEnd() == allEvents.get(1).getEventStart())) {
                        Log.v(TAG, "End Event is created");
                        SetAlarmServiceMethods.setEndAlarmService(context, event);
                    }
                }else{
                    SetAlarmServiceMethods.setEndAlarmService(context, event);
                }
                //get the last event
                Event lastEvent = allEvents.get(allEvents.size() - 1);
                if (lastEvent.isStatic()){
                    Log.v(TAG,"The last event is static: " + lastEvent.getID());
                    SetAlarmServiceMethods.setStaticAlarmService(context, lastEvent);
                }
                Log.v(TAG, "Event size: " + allEvents.size());
                //update database
                for (int i = 0; i < contentID.size(); i++){
                    int id = contentID.get(i);
                    Uri uri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, id);
                    DataMethods.changeToPastEvent(context, uri);
                }
            }
        });
        thread.start();

    }

    public static void setUpEndEvent(final Context context, final Event event){
        Log.v(TAG, "Show Notification for the end");
        //display the notification
        String type = NotificationsUtils.EVENT_REMINDER_END;
        NotificationsUtils.displayCalendarNotification(context, event, type);
        //update the database
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                event.setEndShown();
                DataMethods.updateTodayEvent(context, event);
            }
        });
        thread.start();
        //create job service
        JobServiceMethods.cancelEventJobService(context, JobServiceMethods.DELAY_AND_NOTIFY);
        JobServiceMethods.automatedDelayEventJobService(context);
    }
}
