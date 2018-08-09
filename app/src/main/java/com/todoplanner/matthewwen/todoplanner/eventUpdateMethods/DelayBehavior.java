package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods;

import android.content.Context;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;

public class DelayBehavior {

    private static final int DELAY_EVERY_EVENT = 1;
    private static final int PROPORTION_DELAY = 3;

    private static final String TAG = DelayBehavior.class.getSimpleName();

    public static void delayAllEvents(Context context, Event inProgress, ArrayList<Event> pendingEvents,
                                      boolean showNotification){
        long current = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis());


        int type = getType(pendingEvents);

        //making the event extend by 15 minutes
        inProgress.setEventEnd(current);
        if (showNotification) {
            NotificationsUtils.displayCalendarNotification(context, inProgress, NotificationsUtils.EVENT_REMINDER_END);
        }

        inProgress.setEventEnd(current);
        DataMethods.updateTodayEvent(context, inProgress);

        switch (type){
            case DELAY_EVERY_EVENT: EventChangeBehavior.moveEverythingBack(context, pendingEvents); break;
            case PROPORTION_DELAY: break;
        }

    }

    private static int getType(ArrayList<Event> pendingEvents){
        Log.v(TAG, "The size of pendingEvents: " + pendingEvents.size());
        if (pendingEvents.size() == 0){
            return DELAY_EVERY_EVENT;
        }
        boolean lastStatic = pendingEvents.get(pendingEvents.size() - 1).isStatic();

        if (lastStatic){
            if (pendingEvents.size() == 1){
                return -1;
            }
            return PROPORTION_DELAY;
        }else {
            return DELAY_EVERY_EVENT;
        }
    }
}
