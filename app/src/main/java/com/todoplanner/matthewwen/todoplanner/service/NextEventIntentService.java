package com.todoplanner.matthewwen.todoplanner.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationAction;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;

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
        Uri uri = Uri.parse(intent.getAction());

        if (!(NotificationsUtils.compareTime(
                new Date().getTime(),
                DataMethods.getEndTime(this, uri)
        ))){
            DataMethods.updateData(this, uri);
        }

        DataMethods.changeToPastEvent(this, uri); //Last thing that happens
        NotificationsUtils.setAlarmNextEvent(this);
    }
}
