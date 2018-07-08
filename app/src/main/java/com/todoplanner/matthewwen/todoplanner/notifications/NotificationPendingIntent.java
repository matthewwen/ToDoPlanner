package com.todoplanner.matthewwen.todoplanner.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.todoplanner.matthewwen.todoplanner.developerActivities.DeveloperMainActivity;

class NotificationPendingIntent {

    //The identifications for each pending intent
    private static final int CALENDAR_PENDING_INTENT_ID = 1000;
    private static final int MAPS_PENDING_INTENT_ID = 1100;
    private static final int OPEN_REMINDER_INTENT_ID = 1200;

    static PendingIntent calendarPendingIntent(Context context){
        Intent intent = new Intent(context, DeveloperMainActivity.class);

        return PendingIntent.getActivity(context,CALENDAR_PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    static PendingIntent openMapsPendingIntent(Context context){
        Intent intent = new Intent(Intent.ACTION_VIEW);

        return PendingIntent.getActivity(context, MAPS_PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static PendingIntent openReminderPendingIntent(Context context){
        Intent intent = new Intent(context, DeveloperMainActivity.class);

        return PendingIntent.getService(context, OPEN_REMINDER_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
