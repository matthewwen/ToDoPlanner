package com.todoplanner.matthewwen.todoplanner.alarmService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.text.SimpleDateFormat;
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
    private static PendingIntent pendingIntentEndEvent;

    //The ID Tag for receivers
    private static final int DEVELOPER_REMINDER_EVENT_START = 3;
    private static final int DEVELOPER_REMINDER_EVENT_END = 5;

    //This is a reference to Simple format for comparing times
    private static final SimpleDateFormat GET_COMPARE_FORMAT = new SimpleDateFormat("h:mm a", Locale.ENGLISH);


    //cancel any schedule alarms from showing up.
    public static void cancelReminderAlarmService(Context context){
        Log.v(TAG,"Reminder Alarm Service canceled");
        if (pendingIntentReminderNotification != null){
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            assert manager != null;
            manager.cancel(pendingIntentReminderNotification);
        }
    }

    //Comparing times from one event to the next
    private static boolean compareTime(long date1, long date2){
        return GET_COMPARE_FORMAT.format(new Date(date1))
                .equals(
                        GET_COMPARE_FORMAT.format(new Date(date2))
                );
    }

    //Set Up the Next Alarm (and check if next event is stationary) did not open cursor yet.
    public static void setAlarmNextEvent(Context context){
        //cancel notification
        NotificationsUtils.cancelNotification(context, NotificationsUtils.CALENDAR_NOTIFICATION);

        //Getting the cursor
        Cursor cursor = context.getContentResolver().query(DataContract.TodayEventEntry.EVENT_CONTENT_URI,
                DataContract.TodayEventEntry.PROJECTION_DATE,
                DataContract.TodayEventEntry.COLUMN_EVENT_END + " > ?",
                new String[]{Long.toString(Calendar.getInstance().getTime().getTime())},
                DataContract.TodayEventEntry.COLUMN_EVENT_START);
        assert cursor != null;

        //get the first event
        int idIndex = cursor.getColumnIndex(DataContract.TodayEventEntry._ID);
        int startIndex = cursor.getColumnIndex(DataContract.TodayEventEntry.COLUMN_EVENT_START);
        int endIndex = cursor.getColumnIndex(DataContract.TodayEventEntry.COLUMN_EVENT_END);

        int id;
        long start;
        long end;
        Uri uri;
        boolean haveEnd = true;

        if (cursor.moveToPosition(0)){
            start = cursor.getLong(startIndex);
            end = cursor.getLong(endIndex);
            id = cursor.getInt(idIndex);
            uri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, id);
        }else{
            Log.v(TAG, "No Event Founded");
            return;
        }

        //make sure the next event is not stationary
        if (cursor.moveToPosition(1)){
            if (compareTime(end, cursor.getLong(DataContract.TodayEventEntry.COLUMN_EVENT_START_FULL_INDEX))) {
                haveEnd = !(cursor.getInt(DataContract.TodayEventEntry.COLUMN_EVENT_STATIONARY_FULL_INDEX)
                        == DataContract.TodayEventEntry.EVENT_STATIONARY);
            }
        }

        //Make sure the start does not happen before
        if (start < Calendar.getInstance().getTime().getTime()){
            start = Calendar.getInstance().getTime().getTime() + 2000;
        }

        //Setting up all the alarms
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert manager != null;
        //delete the current alarms set
        if (pendingIntentStartEvent != null) {manager.cancel(pendingIntentStartEvent);}
        if (pendingIntentEndEvent != null) {manager.cancel(pendingIntentEndEvent);}
        //Make intents
        Intent intentStart = new Intent(context, CalendarReminderReceiver.class);
        Intent intentEnd = new Intent(context, CalendarReminderReceiver.class);
        //set uri as the action
        intentStart.setAction(uri.toString());
        intentEnd.setAction(uri.toString());
        //putting in the type.
        intentStart.putExtra(context.getString(R.string.notification_event_start_end_event_key),
                NotificationsUtils.EVENT_REMINDER_START);
        intentEnd.putExtra(context.getString(R.string.notification_event_start_end_event_key),
                NotificationsUtils.EVENT_REMINDER_END);
        //Creating pending
        pendingIntentStartEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_EVENT_START, intentStart, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntentEndEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_EVENT_END, intentEnd, PendingIntent.FLAG_UPDATE_CURRENT);
        //Add it to the alarm service
        manager.set(AlarmManager.RTC_WAKEUP, start, pendingIntentStartEvent);
        if (haveEnd) {manager.set(AlarmManager.RTC_WAKEUP, end, pendingIntentEndEvent);}
        cursor.close();
    }

    //Set Up the End Alarm. The Event is the current event you try to end
    public static void setAlarmNextEventEnd(Context context, Event theEvent){
        //Get alarm service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Cancel Bother
        assert alarmManager != null;
        if (pendingIntentStartEvent != null) {alarmManager.cancel(pendingIntentStartEvent);}
        if (pendingIntentEndEvent != null) {alarmManager.cancel(pendingIntentEndEvent);}
        Intent intentEnd = new Intent(context, CalendarReminderReceiver.class);
        Uri theUri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, theEvent.getID());
        intentEnd.setAction(theUri.toString());
        intentEnd.putExtra(context.getString(R.string.notification_event_start_end_event_key),
                NotificationsUtils.EVENT_REMINDER_END);
        pendingIntentEndEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_EVENT_END, intentEnd, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, theEvent.getEventEnd(), pendingIntentEndEvent);
    }

    public static void setAlarmNextEvent(Context context, Event event, boolean nextStatic){
        Uri uri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI,
                event.getID());
        //Setting up all the alarms
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert manager != null;
        //delete the current alarms set
        if (pendingIntentStartEvent != null) {manager.cancel(pendingIntentStartEvent);}
        if (pendingIntentEndEvent != null) {manager.cancel(pendingIntentEndEvent);}
        //Make intents
        Intent intentStart = new Intent(context, CalendarReminderReceiver.class);
        Intent intentEnd = new Intent(context, CalendarReminderReceiver.class);
        //set uri as the action
        intentStart.setAction(uri.toString());
        intentEnd.setAction(uri.toString());
        //putting in the type.
        intentStart.putExtra(context.getString(R.string.notification_event_start_end_event_key),
                NotificationsUtils.EVENT_REMINDER_START);
        intentEnd.putExtra(context.getString(R.string.notification_event_start_end_event_key),
                NotificationsUtils.EVENT_REMINDER_END);
        //Creating pending
        pendingIntentStartEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_EVENT_START, intentStart, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntentEndEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_EVENT_END, intentEnd, PendingIntent.FLAG_UPDATE_CURRENT);
        //Add it to the alarm service
        manager.set(AlarmManager.RTC_WAKEUP, event.getEventStart(), pendingIntentStartEvent);
        if (!nextStatic) {manager.set(AlarmManager.RTC_WAKEUP, event.getEventEnd(), pendingIntentEndEvent);}
    }


}
