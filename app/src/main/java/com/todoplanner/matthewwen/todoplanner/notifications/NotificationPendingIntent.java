package com.todoplanner.matthewwen.todoplanner.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.todoplanner.matthewwen.todoplanner.developerActivities.DeveloperMainActivity;
import com.todoplanner.matthewwen.todoplanner.service.NextEventIntentService;

public class NotificationPendingIntent {

    //The identifications for each pending intent
    private static final int CALENDAR_PENDING_INTENT_ID = 1000;
    private static final int MAPS_PENDING_INTENT_ID = 1001;
    private static final int OPEN_REMINDER_INTENT_ID = 1002;
    private static final int NEXT_EVENT_INTENT_ID = 1003;

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

        return PendingIntent.getActivity(context, OPEN_REMINDER_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static PendingIntent nextEventPendingIntent(Context context, Uri uri){
        Intent intent = new Intent(context, NextEventIntentService.class);
        intent.setAction(uri.toString());
        return PendingIntent.getService(context, NEXT_EVENT_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
