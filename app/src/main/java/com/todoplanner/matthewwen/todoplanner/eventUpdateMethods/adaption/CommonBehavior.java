package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.adaption;

import android.app.NotificationManager;
import android.content.Context;

import com.todoplanner.matthewwen.todoplanner.alarmService.methods.SetAlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.EventChangeBehavior;
import com.todoplanner.matthewwen.todoplanner.jobServices.JobServiceMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;

public class CommonBehavior {

    public static void cancelAnyCurrentNotification(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.cancel(NotificationsUtils.CALENDAR_NOTIFICATION);

    }

    public static void changeNothingGoToNext(Context context, Event nextEvent){
        cancelAnyCurrentNotification(context);
        NotificationsUtils.displayCalendarNotification(context, nextEvent,  NotificationsUtils.EVENT_REMINDER_START);
        SetAlarmServiceMethods.setEndAlarmService(context, nextEvent);
        nextEvent.setInProgress();
        DataMethods.updateTodayEvent(context, nextEvent);
    }

    public static void delayEveryEvent(Context context, ArrayList<Event> allEvents){
        cancelAnyCurrentNotification(context);

        //Setting the next one to be in progress.
        Event nextEvent = allEvents.get(0);
        nextEvent.setInProgress();
        DataMethods.updateTodayEvent(context, allEvents.get(0));

        //Adding Change into database.
        long difference = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis())
                - allEvents.get(0).getEventStart();
        EventChangeBehavior.moveEverythingBack(context, allEvents, true, difference);
    }

    public static void forwardEveryEvent(Context context,
                                          ArrayList<Event> allEvents, long runOff){
        cancelAnyCurrentNotification(context);
        //Set the Event in Progress
        Event nextEvent = allEvents.get(0);
        nextEvent.setInProgress();
        DataMethods.updateTodayEvent(context, nextEvent);
        //Adding change to the database
        EventChangeBehavior.moveEverythingForward(context, allEvents, true, runOff);
    }

    public static void setNextAlarmService(Context context, ArrayList<Event> allEvents){
        if (allEvents.get(0).isStatic()){
            return;
        }

        if (allEvents.size() < 2){
            return;
        }

        Event nextEvent = allEvents.get(0);

        SetAlarmServiceMethods.setStartAlarmService(context, nextEvent);

    }

    public static void cancelJobService(Context context){
        //Cancel Job Service
        JobServiceMethods.cancelEventJobService(context, JobServiceMethods.DELAY_AND_NOTIFY);
    }

    //unsure about this method
    public static void nnEventNeedEndStatic(Context context, long currentTime, Event nEvent){
        cancelAnyCurrentNotification(context);
        if (DataMethods.roundNearestMinute(currentTime) == nEvent.getEventStart()){
            NotificationsUtils.displayCalendarNotification(context,
                    nEvent,
                    NotificationsUtils.EVENT_REMINDER_START);
        }else {
            SetAlarmServiceMethods.setStartAlarmService(context, nEvent);
        }
        SetAlarmServiceMethods.setStartAlarmService(context, nEvent);
    }

    //unsure about this method too
    public static void nnEventNoEndStatic(Context context, long currentTime, Event nEvent){
        cancelAnyCurrentNotification(context);
        if (DataMethods.roundNearestMinute(currentTime) == nEvent.getEventStart()){
            NotificationsUtils.displayCalendarNotification(context,
                    nEvent,
                    NotificationsUtils.EVENT_REMINDER_START);
        }else {
            SetAlarmServiceMethods.setStartAlarmService(context, nEvent);
        }
    }
}
