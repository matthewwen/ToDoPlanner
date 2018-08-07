package com.todoplanner.matthewwen.todoplanner.alarmService.methods;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.CommonBehavior;
import com.todoplanner.matthewwen.todoplanner.jobServices.JobServiceMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class SetupAlarmServiceMethods {

    private static final String TAG = SetupAlarmServiceMethods.class.getSimpleName();

    public static void setupStartEvent(Context context, Event event){
        Log.v(TAG, "Now Displaying Notification for Nonstatic Events");
        //make event in progress
        event.setInProgress();
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

    public static void setUpStaticEvent(Context context, Event event){
        //display notification
        String type = NotificationsUtils.EVENT_REMINDER_START;
        NotificationsUtils.displayCalendarNotification(context, event, type);
        //cancel any job service
        CommonBehavior.cancelJobService(context);
        //make the event in progress
        event.setInProgress();
        DataMethods.updateTodayEvent(context, event);
        //change all the events
        ArrayList<Event> allEvents = DataMethods.getNecessaryTodayEvents(context);
        ArrayList<Event> changeEvents = new ArrayList<>();
        boolean found = false;
        for (int i = 0; i < allEvents.size() && !found; i++){
            if (allEvents.get(i).getID() == event.getID()){
                found  = true;
            }else {
                changeEvents.add(allEvents.get(i));
            }
        }
        for (int i = 0; i < changeEvents.size(); i++){
            Uri theUri = ContentUris.withAppendedId(
                    DataContract.TodayEventEntry.EVENT_CONTENT_URI, changeEvents.get(i).getID()
            );
            DataMethods.changeToPastEvent(context, theUri);
        }

        //remove the gunk from the list
        for (int i = 0; i < changeEvents.size(); i++){
            allEvents.remove(0);
        }

        //get the last event
        Event lastEvent = allEvents.get(allEvents.size() - 1);
        if (lastEvent.isStatic()){
            Log.v(TAG,"The last event is static: " + lastEvent.getID());
            SetAlarmServiceMethods.setStaticAlarmService(context, lastEvent);
        }

        Log.v(TAG, "Event size: " + allEvents.size());

        //set up alarm end service
        if (allEvents.size() == 2){
            if (!(allEvents.get(0).getEventEnd() == allEvents.get(1).getEventStart())) {
                Log.v(TAG, "End Event is created");
                SetAlarmServiceMethods.setEndAlarmService(context, event);
            }
        }

    }

    public static void setUpEndEvent(Context context, Event event){
        Log.v(TAG, "Show Notification for the end");
        //display the notification
        String type = NotificationsUtils.EVENT_REMINDER_END;
        NotificationsUtils.displayCalendarNotification(context, event, type);
        //create job service
        JobServiceMethods.cancelEventJobService(context, JobServiceMethods.DELAY_AND_NOTIFY);
        JobServiceMethods.automatedDelayEventJobService(context);
    }
}
