package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.adaption;

import android.content.Context;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.EventChangeBehavior;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;

public class DelayBehavior {

    private static final int DELAY_EVERY_EVENT = 1;
    private static final int PROPORTION_DELAY = 3;

    private static final String TAG = DelayBehavior.class.getSimpleName();

    public static void delayAllEvents(Context context, ArrayList<Event> pendingEvents, long current){
        int type = getType(pendingEvents);

        switch (type){
            case DELAY_EVERY_EVENT: setDelayEveryEvent(context, pendingEvents, current);
                 break;
            case PROPORTION_DELAY: setProportionDelay(context, pendingEvents, current);
                break;
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

    /**
     * The operations
     */
    private static void setDelayEveryEvent(Context context, ArrayList<Event> pendingEvents, long current){
        if (pendingEvents.size() == 0){
            return;
        }
        long difference = current - pendingEvents.get(0).getEventStart();
        EventChangeBehavior.moveEverythingBack(context, pendingEvents, difference);
    }

    private static void setProportionDelay(Context context, ArrayList<Event> pendingEvents, long current){
        if (pendingEvents.size() == 0){
            return;
        }
        if (pendingEvents.get(0).getInProgress()){
            pendingEvents.remove(0);
        }
        //checking any static events
        int pos = -1; boolean f = false;
        for (int i = 0; i < pendingEvents.size() && !f; i++){
            Event temp = pendingEvents.get(i);
            if (temp.isStatic()){
                pos = i;
                f = true;
            }
        }
        //do not include the event that is static
        Event endPoint = pendingEvents.remove(pos);
        while (pendingEvents.size() > pos){
            pendingEvents.remove(pos);
        }
        //let the magic happen
        EventChangeBehavior.moveProportion(context, pendingEvents,
                pendingEvents.get(0).getEventStart(),
                pendingEvents.get(pendingEvents.size() - 1).getEventEnd(),
                current,
                endPoint.getEventStart());
    }
}
