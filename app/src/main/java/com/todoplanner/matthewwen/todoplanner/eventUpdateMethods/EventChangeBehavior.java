package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods;

import android.content.Context;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.alarmService.methods.SetAlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;
import com.todoplanner.matthewwen.todoplanner.objects.TimeRange;

import java.util.ArrayList;

public class EventChangeBehavior {


    private static final String TAG = EventChangeBehavior.class.getSimpleName();

    /**
     *Move to Next Event
     */
    //Move all the events a certain point. Going to next event.
    public static void moveEverythingBack(Context context, ArrayList<Event> allEvents,
                                           boolean showNotification, long difference){
        delayEverything(allEvents, difference);

        Event nextEvent = allEvents.get(0);
        if (showNotification){
            NotificationsUtils.displayCalendarNotification(context, nextEvent, NotificationsUtils.EVENT_REMINDER_START );
        }

        SetAlarmServiceMethods.setEndAlarmService(context, allEvents.get(0));

        saveInDatabase(context, allEvents);
    }

    //move all the events a certain point forward. Going to next event
    public static void moveEverythingForward(Context context, ArrayList<Event> allEvents,
                                             boolean showNotification, long runOff){
        forwardEverything(allEvents, runOff);

        if (showNotification){
            NotificationsUtils.displayCalendarNotification(context, allEvents.get(0), NotificationsUtils.EVENT_REMINDER_START);
        }

        SetAlarmServiceMethods.setEndAlarmService(context, allEvents.get(0));

        Log.v(TAG, "All Events are moved forward");

        saveInDatabase(context, allEvents);
    }

    /**
     * Utilities
     */
    private static void delayEverything(ArrayList<Event> allEvents, long difference){
        if (allEvents.size() == 0){
            return;
        }
        for (int i = 0; i < allEvents.size(); i++){
            Event temp = allEvents.get(i);
            temp.setEventStart(temp.getEventStart() + difference);
            temp.setEventEnd(temp.getEventEnd() + difference);
        }
    }

    private static void proportion(ArrayList<Event> allEvents, long oldStart, long oldEnd,
                                   long start, long end){
        if (allEvents.size() == 0){
            return;
        }

        if (allEvents.size() == 1){
            allEvents.get(0).setEventStart(start);
            allEvents.get(0).setEventEnd(end);
            return;
        }

        //2, 3, 4 events?
        ArrayList<TimeRange> allTimeRange = new ArrayList<>();
        long oRange = oldEnd - oldStart;
        long nRange = end - start;
        double m = (((double) nRange) / oRange);
        long endOff = start;

        //calculating new time ranges
        for (int i = 0 ; i < allEvents.size(); i++){
            TimeRange b = new TimeRange(allEvents.get(0).getEventStart() - oldStart);
            TimeRange c = new TimeRange(allEvents.get(0).getID(), allEvents.get(0).getRange());
            b.setNewRange(m);c.setNewRange(m);
            allTimeRange.add(b); allTimeRange.add(c);
        }

        //setting new time ranges.
        for (int i = 0; i < allTimeRange.size(); i++){
            TimeRange temp = allTimeRange.get(i);
            if (temp.isEvent()){
                int pos = (i - 1)/2;
                if (pos < 0){
                    return;
                }
                Event eventTemp = allEvents.get(pos);
                eventTemp.setEventStart(endOff);
                eventTemp.setEventEnd(endOff + temp.getNewRange());
            }
            endOff += temp.getNewRange();
        }
    }

    private static void forwardEverything(ArrayList<Event> allEvents, long runOff){
        for (int i = 0; i < allEvents.size(); i++){
            Event temp = allEvents.get(i);
            if (temp.isStatic()){
                return;
            }
            long range = temp.getRange();
            if (runOff < temp.getEventStart()) {
                temp.setEventStart(runOff);
                temp.setEventEnd(runOff + range);
                runOff += range;
            }
        }
    }

    private static void saveInDatabase(Context context, ArrayList<Event> allEvents){
        for (Event temp: allEvents){
            DataMethods.updateTodayEvent(context, temp);
        }
    }

    /**
     *Not Moving to Next Event
     */
    //Move all events a certain point. Not going to next event.
    public static void moveEverythingBack(Context context, ArrayList<Event> allEvents, long difference){
        delayEverything(allEvents, difference);

        saveInDatabase(context, allEvents);
    }

    //move all events a certain point forward. Not going to next event
    public static void moveEverythingForward(Context context, ArrayList<Event> allEvents, long runOff){
        forwardEverything(allEvents, runOff);
        saveInDatabase(context, allEvents);
    }

    //Move all events a certain point. Not going to next event
    public static void moveProportion(Context context, ArrayList<Event> allEvents, long oldStart, long oldEnd,
                                          long start, long end){
        proportion(allEvents, oldStart, oldEnd, start, end);
        saveInDatabase(context, allEvents);
    }



}
