package com.todoplanner.matthewwen.todoplanner.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationAction;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NextEventIntentService extends IntentService{

    private static final String name = NextEventIntentService.class.getSimpleName();
    private static final String TAG = name;

    public NextEventIntentService() {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        final Uri uri = Uri.parse(intent.getAction());
        ArrayList<Event> allEvents = DataMethods.getAllTodayEvents(this); //First one is the finished one, Second one is the next event.

        if (allEvents.size() > 1 && allEvents.get(1).getEventStart() < Calendar.getInstance().getTimeInMillis()){
            if (!(NotificationsUtils.compareTime(
                    Calendar.getInstance().getTimeInMillis(),
                    DataMethods.getEndTime(this, uri)
            ))){
                DataMethods.updateData(this, uri, allEvents, true); // it should already set the next alarm
            }else{
                Log.v(TAG, "Everything is perfect. Just show notification and started end service");
                NotificationsUtils.displayCalendarNotificationStart(this, allEvents.get(1));
                NotificationsUtils.setAlarmNextEventEnd(this, allEvents.get(1));
                DataMethods.changeToPastEvent(this, uri);
            }
        }else {
            DataMethods.changeToPastEvent(NextEventIntentService.this, uri);
            NotificationsUtils.setAlarmNextEvent(this);
        }


    }
}
