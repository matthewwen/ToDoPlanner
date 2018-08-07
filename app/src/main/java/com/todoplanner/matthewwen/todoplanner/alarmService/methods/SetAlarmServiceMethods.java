package com.todoplanner.matthewwen.todoplanner.alarmService.methods;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.alarmService.CalendarReminderReceiver;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SetAlarmServiceMethods {

    private static final String TAG = SetAlarmServiceMethods.class.getSimpleName();

    //The ID identified by pending intents
    private static PendingIntent pendingIntentStartEvent;
    private static PendingIntent pendingIntentStaticStartEvent;
    private static PendingIntent pendingIntentEndEvent;

    //The ID Tag for receivers
    public static final int DEVELOPER_REMINDER_EVENT_START = 3;
    public static final int DEVELOPER_REMINDER_STATIC_START = 4;
    public static final int DEVELOPER_REMINDER_EVENT_END = 5;

    //This is a reference to Simple format for comparing times
    private static final SimpleDateFormat GET_COMPARE_FORMAT = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    //tell device that the alarm is set
    private static void setAlarmIsSet(Context context, Event event){
        event.setAlarmSet();
        DataMethods.updateTodayEvent(context, event);
    }

    //change all the events to not set
    private static void setAllAlarmNotSet(Context context, ArrayList<Event> allEvents){
        for (int i = 0; i < allEvents.size(); i++){
            Event temp = allEvents.get(0);
            temp.setAlarmNotSet();
            DataMethods.updateTodayEvent(context, temp);
        }
    }

    public static void setAlarmService(Context context){
        //get all the events
        ArrayList<Event> allEvents = DataMethods.getNecessaryTodayEvents(context);
        //check if none, then cancel all
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert manager != null;
        if (pendingIntentStartEvent != null) manager.cancel(pendingIntentStartEvent);
        if (pendingIntentStaticStartEvent != null) manager.cancel(pendingIntentStaticStartEvent);
        if (pendingIntentEndEvent != null) manager.cancel(pendingIntentEndEvent);
        if (allEvents.size() == 0){
            return;
        }
        //check if the first event is in progress
        if (allEvents.get(0).getInProgress()){
            allEvents.remove(0);
        }
        //end when no events are left
        if (allEvents.size() < 1){
            return;
        }
        //set all alarms to not set
        setAllAlarmNotSet(context, allEvents);
        //set the static event
        Event lastEvent = allEvents.get(allEvents.size() - 1);
        if (lastEvent.isStatic()){
            setStaticAlarmService(context, lastEvent);
        }
        //set the next event
        if (allEvents.size() < 1){
            return;
        }
        Event nextEvent = allEvents.get(0);
        if (nextEvent.isStatic()){
            return;
        }
        if (nextEvent.getEventStart() <= DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis())){
            SetupAlarmServiceMethods.setupStartEvent(context, nextEvent);
            return;
        }
        setStartAlarmService(context, nextEvent);

    }

    //Set Alarm Service for event that is static
    public static void setStaticAlarmService(Context context, Event eventStatic){
        if (eventStatic.isAlarmSet()){
            return;
        }
        //Setting up all the alarms
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert manager != null;
        //cancel any current alarm service
        if (pendingIntentStaticStartEvent != null){
            manager.cancel(pendingIntentStaticStartEvent);
        }
        //create alarm service
        Uri staticUri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI,
                eventStatic.getID());
        Intent staticStart = new Intent(context, CalendarReminderReceiver.class);
        staticStart.setAction(staticUri.toString());
        staticStart.putExtra(context.getString(R.string.notification_type_setup_key),
                DEVELOPER_REMINDER_STATIC_START);
        pendingIntentStaticStartEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_STATIC_START, staticStart, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, eventStatic.getEventStart(), pendingIntentStaticStartEvent);
        setAlarmIsSet(context, eventStatic);

    }

    //Set Alarm Service for event that is next (and not static)
    public static void setStartAlarmService(Context context, Event nextEvent){
        //Setting up all the alarms
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert manager != null;
        //cancel any current alarm service
        if (pendingIntentStartEvent != null){
            manager.cancel(pendingIntentStartEvent);
        }
        Uri eventUri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI,
                nextEvent.getID());
        Intent eventStart = new Intent(context, CalendarReminderReceiver.class);
        eventStart.setAction(eventUri.toString());
        eventStart.putExtra(context.getString(R.string.notification_type_setup_key),
                DEVELOPER_REMINDER_EVENT_START);
        pendingIntentStartEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_EVENT_START, eventStart, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, nextEvent.getEventStart(), pendingIntentStartEvent);

        setAlarmIsSet(context, nextEvent);
        Log.v(TAG, "Alarm Service is set: Event ID -> " + nextEvent.getID());
    }

    //Set Alarm Service for the end of the event
    public static void setEndAlarmService(Context context, Event event){
        Log.v(TAG, "Even End is set");
        //Setting up all the alarms
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert manager != null;
        //cancel any current alarm service
        if (pendingIntentEndEvent != null){
            manager.cancel(pendingIntentEndEvent);
        }
        Uri eventUri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI,
                event.getID());
        Intent eventEnd = new Intent(context, CalendarReminderReceiver.class);
        eventEnd.setAction(eventUri.toString());
        eventEnd.putExtra(context.getString(R.string.notification_type_setup_key),
                DEVELOPER_REMINDER_EVENT_END);
        pendingIntentEndEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_EVENT_END, eventEnd, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, event.getEventEnd(), pendingIntentEndEvent);
    }


}
