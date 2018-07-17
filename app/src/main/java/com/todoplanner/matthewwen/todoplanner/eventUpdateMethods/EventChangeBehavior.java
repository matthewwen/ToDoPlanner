package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

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
        long endOff = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis());
        for (int i = 0; i < allEvents.size(); i++){
            Event temp = allEvents.get(i);
            long range = temp.getRange();
            if (endOff > temp.getEventStart()) {
                temp.setEventStart(endOff);
                temp.setEventEnd(endOff + range);
                endOff += range;
            }
        }

        Event nextEvent = allEvents.get(0);
        if (showNotification){
            NotificationsUtils.displayCalendarNotificationStart(context, nextEvent);
        }

        for (Event temp: allEvents){
            DataMethods.updateTodayEvent(context, temp);
        }
    }

    //move all the events a certain point forward
    public static void moveEverythingForward(Context context, ArrayList<Event> allEvents,
                                             boolean showNotification){
        long runOff = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis());
        for (int i = 0; i < allEvents.size(); i++){
            Event temp = allEvents.get(i);
            long range = temp.getRange();
            temp.setEventStart(runOff);
            temp.setEventEnd(runOff + range);
            runOff += range;
        }

        if (showNotification){
            NotificationsUtils.displayCalendarNotificationStart(context, allEvents.get(0));
        }

        for(Event temp: allEvents){
            DataMethods.updateTodayEvent(context, temp);
        }
    }


}
