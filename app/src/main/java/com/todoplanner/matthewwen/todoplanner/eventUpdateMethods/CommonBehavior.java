package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods;

import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class CommonBehavior {

    public static ArrayList<Event> getEvents(Context context){
        ArrayList<Event> allEvents = DataMethods.getAllTodayEvents(context);
        boolean foundStatic = false; int position = -1;
        for (int i = 1; i < allEvents.size() && !foundStatic; i++){
            if (allEvents.get(i).isStatic()){
                foundStatic = true;
                position = i;
            }
        }
        if (position != -1) {
            while (allEvents.size() > position + 1) {
                allEvents.remove(position + 1);
            }
        }
        //Delete the finished event if it is the last event throughout the day
        if (allEvents.size() <= 1){
            if (allEvents.size() == 1){
                Uri uri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI,
                        allEvents.get(0).getID());
                DataMethods.changeToPastEvent(context, uri);
            }
            return null;
        }
        return allEvents;
    }

    private static void cancelAnyCurrentNotification(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.cancel(NotificationsUtils.CALENDAR_NOTIFICATION);

    }

    public static void changeNothing(Context context, Event nextEvent, Event finished){
        cancelAnyCurrentNotification(context);
        NotificationsUtils.displayCalendarNotificationStart(context, nextEvent);
        Uri uri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, finished.getID());
        DataMethods.changeToPastEvent(context, uri);
        NotificationsUtils.setAlarmNextEventEnd(context, nextEvent);
        nextEvent.setInProgress();
        DataMethods.updateTodayEvent(context, nextEvent);
    }

    public static void delayEveryEvent(Context context, ArrayList<Event> allEvents,
                                        Event finished){
        cancelAnyCurrentNotification(context);

        //Put the event in the Past
        Uri oldEventUri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI,
                finished.getID());
        DataMethods.changeToPastEvent(context, oldEventUri);

        //Setting the next one to be in progress.
        Event nextEvent = allEvents.get(0);
        nextEvent.setInProgress();
        DataMethods.updateTodayEvent(context, allEvents.get(0));

        //Adding Change into database.
        EventChangeBehavior.moveEverythingBack(context, allEvents, true);
    }

    public static void forwardEveryEvent(Context context,
                                          ArrayList<Event> allEvents, Event finished){
        cancelAnyCurrentNotification(context);
        //Set the Event in Progress
        Event nextEvent = allEvents.get(0);
        nextEvent.setInProgress();
        DataMethods.updateTodayEvent(context, nextEvent);
        //Change to past events
        Uri oldUri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, finished.getID());
        DataMethods.changeToPastEvent(context, oldUri);
        //Adding change to the database
        EventChangeBehavior.moveEverythingForward(context, allEvents, true);
    }

    private static long getDifference(Event eventOne, Event eventTwo){
        return eventOne.getEventEnd() - eventTwo.getEventStart();
    }

    public static long getAmountBufferTime(ArrayList<Event> allEvents){
        long sum = 0;
        for (int i = 0; i < allEvents.size() - 1; i++){
            sum += getDifference(allEvents.get(i), allEvents.get(i+1));
        }
        return sum;
    }

    public static void setNextAlarmService(Context context, ArrayList<Event> allEvents){
        if (allEvents.get(0).isStatic()){
            return;
        }

        if (allEvents.size() < 2){
            return;
        }

        Event nextEvent = allEvents.get(0);
        Event nextNextEvent = allEvents.get(1);

        boolean value;
        boolean nextIsStatic = allEvents.get(1).isStatic();
        boolean theyLineUp = DataMethods.roundNearestMinute(nextEvent.getEventEnd())
                == DataMethods.roundNearestMinute(nextNextEvent.getEventStart());
        value = nextIsStatic && theyLineUp;


        NotificationsUtils.setAlarmNextEvent(context, nextEvent, value);

    }
}
