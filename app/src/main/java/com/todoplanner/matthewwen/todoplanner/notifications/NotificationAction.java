package com.todoplanner.matthewwen.todoplanner.notifications;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.todoplanner.matthewwen.todoplanner.R;

public class NotificationAction {

    //The identifications for the map actions.
    private static final int NOTIFICATION_ACTION_OPEN_MAPS = 1;
    private static final int NOTIFICATION_ACTION_COMPLETED = 2;

    static NotificationCompat.Action openMaps(Context context){
        NotificationCompat.Action.Builder builder = new NotificationCompat.Action.Builder(
                NOTIFICATION_ACTION_OPEN_MAPS,
                context.getString(R.string.notification_action_maps),
                NotificationPendingIntent.openMapsPendingIntent(context)
        );
        return builder.build();
    }

    static NotificationCompat.Action completedReminder(Context context){
        NotificationCompat.Action.Builder builder = new NotificationCompat.Action.Builder(
                NOTIFICATION_ACTION_COMPLETED,
                context.getString(R.string.notification_action_completed),
                null);

        return builder.build();
    }
}