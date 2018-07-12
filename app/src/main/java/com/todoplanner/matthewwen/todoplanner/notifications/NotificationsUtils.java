package com.todoplanner.matthewwen.todoplanner.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.receivers.events.AlarmEventCalendarReminderReceiver;

import java.util.Date;

import static com.todoplanner.matthewwen.todoplanner.notifications.NotificationPendingIntent.calendarPendingIntent;
import static com.todoplanner.matthewwen.todoplanner.notifications.NotificationPendingIntent.openReminderPendingIntent;

public class NotificationsUtils {

    private static final String TAG = NotificationsUtils.class.getSimpleName();

    //The different type of notification for Calendar Events
    public static final String EVENT_REMINDER_START = "Start Event: ";
    public static final String EVENT_REMINDER_END = "End Event: ";

    //The identifications for the Notifications
    private static int CALENDAR_NOTIFICATION = 2000;
    private static int REMINDER_NOTIFICATION = 2100;
    private static int WEATHER_NOTIFICATION = 2200;

    //The identifications for the Notification Channel Calendar
    private static final String CALENDAR_NOTIFICATION_CHANNEL = "calendar-notification-channel";
    private static final String REMINDER_NOTIFICATION_CHANNEL = "reminder-notification-channel";
    private static final String WEATHER_NOTIFICATION_CHANNEL = "weather-notification-channel";

    //Comparing times from one event to the next
    public static boolean compareTime(long date1, long date2){
        return AlarmEventCalendarReminderReceiver.GET_COMPARE_FORMAT.format(new Date(date1))
                .equals(
                        AlarmEventCalendarReminderReceiver.GET_COMPARE_FORMAT.format(new Date(date2))
                );
    }


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
                .setContentIntent(calendarPendingIntent(context));

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

        }

        //for devices with sdk less than oreo
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notify.setPriority(Notification.PRIORITY_HIGH);
        }

        manager.notify(CALENDAR_NOTIFICATION, notify.build());

        if (CALENDAR_NOTIFICATION == 2099){
            CALENDAR_NOTIFICATION = 2000;
        }else {
            CALENDAR_NOTIFICATION++;
        }
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

    /**
     *
     * @param context of the activity
     * @return Bitmap for the notification
     */
    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();

        return BitmapFactory.decodeResource(res, R.mipmap.ic_launcher_round);
    }


}