package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods;

import android.content.Context;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.alarmService.AlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;

public class EventChangeBehavior {


    private static final String TAG = EventChangeBehavior.class.getSimpleName();

    //Move all the events a certain point (Uri is the just completed event)
    public static void moveEverythingBack(Context context, ArrayList<Event> allEvents,
                                           boolean showNotification){
        delayEverything(allEvents);

        Event nextEvent = allEvents.get(0);
        if (showNotification){
            NotificationsUtils.displayCalendarNotification(context, nextEvent, NotificationsUtils.EVENT_REMINDER_START );
        }

        AlarmServiceMethods.setAlarmEventEnd(context, allEvents.get(0));

        saveInDatabase(context, allEvents);
    }

    private static void delayEverything(ArrayList<Event> allEvents){
        long difference = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis())
                - allEvents.get(0).getEventStart();
        for (int i = 0; i < allEvents.size(); i++){
            Event temp = allEvents.get(i);
            temp.setEventStart(temp.getEventStart() + difference);
            temp.setEventEnd(temp.getEventEnd() + difference);
        }
    }

    private static void saveInDatabase(Context context, ArrayList<Event> allEvents){
        for (Event temp: allEvents){
            DataMethods.updateTodayEvent(context, temp);
        }
    }

    public static void moveEverythingBack(Context context, ArrayList<Event> allEvents){
        delayEverything(allEvents);

        saveInDatabase(context, allEvents);
    }

    //move all the events a certain point forward
    public static void moveEverythingForward(Context context, ArrayList<Event> allEvents,
                                             boolean showNotification){
        long runOff = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis());
        for (int i = 0; i < allEvents.size(); i++){
            Event temp = allEvents.get(i);
            long range = temp.getRange();
            if (runOff < temp.getEventStart()) {
                temp.setEventStart(runOff);
                temp.setEventEnd(runOff + range);
                runOff += range;
            }
        }

        if (showNotification){
            NotificationsUtils.displayCalendarNotification(context, allEvents.get(0), NotificationsUtils.EVENT_REMINDER_START);
        }

        AlarmServiceMethods.setAlarmEventEnd(context, allEvents.get(0));

        Log.v(TAG, "All Events are moved forward");

        for(Event temp: allEvents){
            DataMethods.updateTodayEvent(context, temp);
        }
    }


}
