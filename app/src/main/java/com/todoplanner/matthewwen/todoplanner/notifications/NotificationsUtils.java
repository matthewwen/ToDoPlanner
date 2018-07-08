package com.todoplanner.matthewwen.todoplanner.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.MainActivity;
import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.developerActivities.DeveloperMainActivity;

public class NotificationsUtils {

    private static final String TAG = NotificationsUtils.class.getSimpleName();

    //The identifications for the Notification Calendar
    private static final int CALENDAR_NOTIFICATION = 1;

    //The identifications for the Notification Channel Calendar
    private static final String CALENDAR_NOTIFICATION_CHANNEL = "calendar-notification-channel";

    //The identifications for each pending intent
    private static final int CALENDAR_PENDING_INTENT_ID = 2;

    public static void displayCalendarNotification(Context context, String title, String range, String location){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE) ;
        assert manager != null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getString(R.string.notification_channel_calendar_name);
            String description = context.getString(R.string.notification_channel_calendar_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =  new NotificationChannel(CALENDAR_NOTIFICATION_CHANNEL, name, importance);
            channel.setDescription(description);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notify = new NotificationCompat.Builder(context, CALENDAR_NOTIFICATION_CHANNEL)
                .setContentTitle(title)
                .setContentText(range)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        range + "\n" + location
                ))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(largeIcon(context))
                .setContentIntent(calendarPendingIntent(context));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notify.setPriority(Notification.PRIORITY_HIGH);
        }

        manager.notify(CALENDAR_NOTIFICATION, notify.build());
        Log.v(TAG, "Notification came through");
    }

    private static Bitmap largeIcon(Context context){
        Resources res = context.getResources();

        return BitmapFactory.decodeResource(res, R.mipmap.ic_launcher_round);
    }

    private static PendingIntent calendarPendingIntent(Context context){
        Intent intent = new Intent(context, DeveloperMainActivity.class);

        return PendingIntent.getActivity(context,CALENDAR_PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }
}