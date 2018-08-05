package com.todoplanner.matthewwen.todoplanner.alarmService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ProxyInfo;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmServiceMethods {

    private static final String TAG = AlarmServiceMethods.class.getSimpleName();

    //Pending Intent act as ID.
    private static PendingIntent pendingIntentReminderNotification;

    //ID and Tag for Receivers
    private static final int DEVELOPER_REMINDER_EVENT_SERVICE = 2;

    //The ID identified by pending intents
    private static PendingIntent pendingIntentStartEvent;
    private static PendingIntent pendingIntentStaticStartEvent;
    private static PendingIntent pendingIntentEndEvent;

    //The ID Tag for receivers
    private static final int DEVELOPER_REMINDER_EVENT_START = 3;
    private static final int DEVELOPER_REMINDER_STATIC_START = 4;
    private static final int DEVELOPER_REMINDER_EVENT_END = 5;

    //The ID Tag for different type of operation
    private static final int SET_BOTH = 1;
    private static final int SET_END = 2;
    private static final int SET_STATIC = 3;

    //This is a reference to Simple format for comparing times
    private static final SimpleDateFormat GET_COMPARE_FORMAT = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    //cancel any schedule alarms from showing up.
    public static void cancelReminderAlarmService(Context context) {
        Log.v(TAG, "Reminder Alarm Service canceled");
        if (pendingIntentReminderNotification != null) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            assert manager != null;
            manager.cancel(pendingIntentReminderNotification);
        }
    }

    //Comparing times from one event to the next
    private static boolean compareTime(long date1, long date2) {
        return GET_COMPARE_FORMAT.format(new Date(date1))
                .equals(
                        GET_COMPARE_FORMAT.format(new Date(date2))
                );
    }

    //Set Up the Next Alarm (and check if next event is stationary) did not open cursor yet.
    public static void setAlarmEventStart(Context context) {
        ArrayList<Event> upComingEvents = DataMethods.getAllTodayEvents(context);
        if (upComingEvents == null || upComingEvents.size() == 0){
            return;
        }

        if (upComingEvents.get(0).isStatic()){
            setStaticAlarmStartEvent(context, upComingEvents.get(0));
            Log.v(TAG, "This Alarm static is set: " + upComingEvents.get(0).getID());
        }else {
            setAlarmEventStart(context, upComingEvents.get(0));
            Log.v(TAG, "This Alarm is set: " + upComingEvents.get(0).getID());
        }
    }

    //Set Up the End Alarm. The Event is the current event you try to end
    public static void setAlarmEventEnd(Context context, Event theEvent) {
        //Get alarm service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Cancel Bother
        assert alarmManager != null;
        if (pendingIntentEndEvent != null) {
            alarmManager.cancel(pendingIntentEndEvent);
        }
        Intent intentEnd = new Intent(context, CalendarReminderReceiver.class);
        Uri theUri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, theEvent.getID());
        intentEnd.setAction(theUri.toString());
        intentEnd.putExtra(context.getString(R.string.notification_event_start_end_event_key),
                NotificationsUtils.EVENT_REMINDER_END);
        pendingIntentEndEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_EVENT_END, intentEnd, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, theEvent.getEventEnd(), pendingIntentEndEvent);
    }

    //Set up the Start Alarm. The Event is the current event you want to start
    public static void setAlarmEventStart(Context context, Event theEvent){
        //Get alarm service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Cancel Bother
        assert alarmManager != null;
        if (pendingIntentStartEvent != null) {
            alarmManager.cancel(pendingIntentStartEvent);
        }
        Intent intentEnd = new Intent(context, CalendarReminderReceiver.class);
        Uri theUri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, theEvent.getID());
        intentEnd.setAction(theUri.toString());
        intentEnd.putExtra(context.getString(R.string.notification_event_start_end_event_key),
                NotificationsUtils.EVENT_REMINDER_START);
        pendingIntentStartEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_EVENT_START, intentEnd, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, theEvent.getEventStart(), pendingIntentStartEvent);
        //Update in the database
        setAlarmIsSet(context, theEvent);
    }

    public static void setStaticAlarmStartEvent(Context context, Event event){
        //Setting up all the alarms
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert manager != null;
        //cancel alarm service
        if (pendingIntentStaticStartEvent != null){
            manager.cancel(pendingIntentStaticStartEvent);
        }
        Uri staticUri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI,
                event.getID());
        Intent staticStart = new Intent(context, CalendarReminderReceiver.class);
        staticStart.setAction(staticUri.toString());
        staticStart.putExtra(context.getString(R.string.notification_event_start_end_event_key),
                NotificationsUtils.EVENT_REMINDER_START);
        pendingIntentStaticStartEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_STATIC_START, staticStart, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, event.getEventStart(), pendingIntentStaticStartEvent);
        //Update in the database
        setAlarmIsSet(context, event);
    }

    public static void setAlarmIsSet(Context context, Event event){
        event.setAlarmSet();
        DataMethods.updateTodayEvent(context, event);
    }

}
