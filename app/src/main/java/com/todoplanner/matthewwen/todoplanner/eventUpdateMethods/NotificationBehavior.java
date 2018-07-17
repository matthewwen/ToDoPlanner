package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationBehavior {

    public static void setUpNextEvent(Intent intent, Context context){
        final Uri uri = Uri.parse(intent.getAction());
        ArrayList<Event> allEvents = DataMethods.getAllTodayEvents(context); //First one is the finished one, Second one is the next event.

        if (allEvents.size() > 1 && allEvents.get(1).getEventStart() < Calendar.getInstance().getTimeInMillis()){
            if (!(NotificationsUtils.compareTime(
                    Calendar.getInstance().getTimeInMillis(),
                    DataMethods.getEndTime(context, uri)
            ))){
                EventChangeBehavior.moveEverythingBack(context, allEvents, true); // it should already set the next alarm
            }else{
                NotificationsUtils.displayCalendarNotificationStart(context, allEvents.get(1));
                allEvents.get(1).setInProgress();
                DataMethods.updateTodayEvent(context, allEvents.get(1));
                NotificationsUtils.setAlarmNextEventEnd(context, allEvents.get(1));
                DataMethods.changeToPastEvent(context, uri);
            }
        }else {
            allEvents.get(1).setInProgress();
            DataMethods.updateTodayEvent(context, allEvents.get(1));
            DataMethods.changeToPastEvent(context, uri);
            NotificationsUtils.setAlarmNextEvent(context);
        }
    }


}
