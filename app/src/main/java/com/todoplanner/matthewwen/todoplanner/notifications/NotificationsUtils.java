package com.todoplanner.matthewwen.todoplanner.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.objects.Event;
import com.todoplanner.matthewwen.todoplanner.receivers.events.AlarmEventCalendarReminderReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.todoplanner.matthewwen.todoplanner.notifications.NotificationPendingIntent.calendarPendingIntent;
import static com.todoplanner.matthewwen.todoplanner.notifications.NotificationPendingIntent.openReminderPendingIntent;

public class NotificationsUtils {

    private static final String TAG = NotificationsUtils.class.getSimpleName();

    //The different type of notification for Calendar Events
    public static final String EVENT_REMINDER_START = "Start Event: ";
    public static final String EVENT_REMINDER_END = "End Event: ";

    //The identifications for the Notifications
    public static int CALENDAR_NOTIFICATION = 2000;
    private static int REMINDER_NOTIFICATION = 2100;
    private static int WEATHER_NOTIFICATION = 2200;

    //The identifications for the Notification Channel Calendar
    private static final String CALENDAR_NOTIFICATION_CHANNEL = "calendar-notification-channel";
    private static final String REMINDER_NOTIFICATION_CHANNEL = "reminder-notification-channel";
    private static final String WEATHER_NOTIFICATION_CHANNEL = "weather-notification-channel";

    //The ID identified by pending intents
    private static PendingIntent pendingIntentStartEvent;
    private static PendingIntent pendingIntentEndEvent;

    //The ID Tag for receivers
    private static final int DEVELOPER_REMINDER_EVENT_START = 3;
    private static final int DEVELOPER_REMINDER_EVENT_END = 5;

    //This is a reference to Simple format for comparing times
    private static final SimpleDateFormat GET_COMPARE_FORMAT = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    //This is for displaying the range
    private static final SimpleDateFormat GET_ADVANCE_FORMAT = new SimpleDateFormat("h:mm", Locale.ENGLISH);
    private static final SimpleDateFormat GET_SIMPLE_FORMAT = new SimpleDateFormat("h", Locale.ENGLISH);
    private static final SimpleDateFormat GET_MINUTE = new SimpleDateFormat("m", Locale.ENGLISH);
    private static final SimpleDateFormat GET_AM_PM = new SimpleDateFormat("a", Locale.ENGLISH);

    //Comparing times from one event to the next
    public static boolean compareTime(long date1, long date2){
        return GET_COMPARE_FORMAT.format(new Date(date1))
                .equals(
                        GET_COMPARE_FORMAT.format(new Date(date2))
                );
    }

    //For the Calendar
    public static void displayCalendarNotification(Context context, Uri uri, String title,
                                                   String range, String location, String typeOfEvent){

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE) ;
        assert manager != null;

        //creating notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getString(R.string.notification_channel_calendar_name);
            String description = context.getString(R.string.notification_channel_calendar_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =  new NotificationChannel(CALENDAR_NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            manager.createNotificationChannel(channel);
        }

        //Building the Notification
        NotificationCompat.Builder notify = new NotificationCompat.Builder(context, CALENDAR_NOTIFICATION_CHANNEL)
                .setContentTitle(typeOfEvent + title)
                .setContentText(range)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(largeIcon(context))
                .setContentIntent(calendarPendingIntent(context, uri));

        //Fix the location error
        if (location.equals("")){
            notify.setStyle(new NotificationCompat.BigTextStyle().bigText(
                    range
            ));
        }else{
            notify.setStyle(new NotificationCompat.BigTextStyle().bigText(
                    range + "\n" + location
            ));
            if (typeOfEvent.equals(EVENT_REMINDER_START)){
                notify.addAction(NotificationAction.openMaps(context));
            }
        }

        //If the calendar event is type end. Two actions need to be added
        if (typeOfEvent.equals(EVENT_REMINDER_END)){
            notify.addAction(NotificationAction.nextEvent(context, uri));
        }

        //for devices with sdk less than oreo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notify.setPriority(Notification.PRIORITY_HIGH);
        }

        manager.notify(CALENDAR_NOTIFICATION, notify.build());
   }

   //For the Calendar with different parameter
    public static void displayCalendarNotificationStart(Context context, Event event){
        Uri uri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, event.getID());
        String title = event.getEventName();
        String range = displayRange(new Date(event.getEventStart()), new Date(event.getEventEnd()));
        String location = "";
        displayCalendarNotification(context,
                uri,
                title,
                range,
                location,
                EVENT_REMINDER_START);
    }

    public static void displayReminderNotification(Context context, String taskName, String reminder){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getString(R.string.notification_channel_reminder_name);
            String description = context.getString(R.string.notification_channel_reminder_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(REMINDER_NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            manager.createNotificationChannel(channel);
       }

       NotificationCompat.Builder builder = new NotificationCompat.Builder(context, REMINDER_NOTIFICATION_CHANNEL)
               .setContentTitle("Reminder: " + taskName)
               .setContentText(reminder)
               .setAutoCancel(true)
               .setStyle(new NotificationCompat.BigTextStyle().bigText(
                       reminder
               ))
               .setSmallIcon(R.mipmap.ic_launcher_round)
               .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
               .setLargeIcon(largeIcon(context))
               .setContentIntent(openReminderPendingIntent(context))
               .addAction(NotificationAction.completedReminder(context));

       if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
           builder.setPriority(Notification.PRIORITY_HIGH);
       }

       manager.notify(REMINDER_NOTIFICATION, builder.build());
       if (REMINDER_NOTIFICATION == 2199){
           REMINDER_NOTIFICATION = 2100;
       }else {
           REMINDER_NOTIFICATION++;
       }

   }

    public static void displayWeatherNotification(Context context, String weather, String details){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getString(R.string.notification_channel_weather_name);
            String description = context.getString(R.string.notification_channel_weather_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(WEATHER_NOTIFICATION_CHANNEL,
                    name,
                    importance);
            channel.setDescription(description);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, WEATHER_NOTIFICATION_CHANNEL)
                .setContentTitle("Weather Update")
                .setContentText(weather)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        details
                ))
                .setLargeIcon(largeIcon(context))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setAutoCancel(true);

        manager.notify(WEATHER_NOTIFICATION, builder.build());
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();

        return BitmapFactory.decodeResource(res, R.mipmap.ic_launcher_round);
    }

    //Set Up the Next Alarm (and check if next event is stationary)
    public static void setAlarmNextEvent(Context context){
        //Cancel Notification (dismiss)
        NotificationManager notifyMangager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notifyMangager != null;
        notifyMangager.cancel(CALENDAR_NOTIFICATION);

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
        Intent intentStart = new Intent(context, AlarmEventCalendarReminderReceiver.class);
        Intent intentEnd = new Intent(context, AlarmEventCalendarReminderReceiver.class);
        //set uri as the action
        intentStart.setAction(uri.toString());
        intentEnd.setAction(uri.toString());
        //putting in the type.
        intentStart.putExtra(context.getString(R.string.notification_event_start_stop_key),
                NotificationsUtils.EVENT_REMINDER_START);
        intentEnd.putExtra(context.getString(R.string.notification_event_start_stop_key),
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
        Intent intentEnd = new Intent(context, AlarmEventCalendarReminderReceiver.class);
        Uri theUri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, theEvent.getID());
        intentEnd.setAction(theUri.toString());
        intentEnd.putExtra(context.getString(R.string.notification_event_start_stop_key),
                NotificationsUtils.EVENT_REMINDER_END);
        pendingIntentEndEvent = PendingIntent.getBroadcast(context,
                DEVELOPER_REMINDER_EVENT_END, intentEnd, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, theEvent.getEventEnd(), pendingIntentEndEvent);
    }

    public static String displayRange(Date dateStart, Date dateEnd){
        String range = "";
        if (GET_MINUTE.format(dateStart).equals("0") &&
                GET_MINUTE.format(dateEnd).equals("0")){
            range = GET_SIMPLE_FORMAT.format(dateStart) +
                    " - " + GET_SIMPLE_FORMAT.format(dateEnd) + " " +
                    GET_AM_PM.format(dateEnd);
        }else {
            range = GET_ADVANCE_FORMAT.format(dateStart) +
                    " - " + GET_ADVANCE_FORMAT.format(dateEnd) + " " +
                    GET_AM_PM.format(dateEnd);
        }

        return range;
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
        Intent intentStart = new Intent(context, AlarmEventCalendarReminderReceiver.class);
        Intent intentEnd = new Intent(context, AlarmEventCalendarReminderReceiver.class);
        //set uri as the action
        intentStart.setAction(uri.toString());
        intentEnd.setAction(uri.toString());
        //putting in the type.
        intentStart.putExtra(context.getString(R.string.notification_event_start_stop_key),
                NotificationsUtils.EVENT_REMINDER_START);
        intentEnd.putExtra(context.getString(R.string.notification_event_start_stop_key),
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