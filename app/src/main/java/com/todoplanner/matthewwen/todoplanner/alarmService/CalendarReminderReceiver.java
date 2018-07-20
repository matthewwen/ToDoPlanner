package com.todoplanner.matthewwen.todoplanner.alarmService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

public class CalendarReminderReceiver extends BroadcastReceiver{



    private static final String TAG = CalendarReminderReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get the Uri and the type
        String action = intent.getAction();
        Uri uri = Uri.parse(action);
        String type = intent.getStringExtra(context.getString(R.string.notification_event_start_end_event_key));

        //get the event
        Event event = DataMethods.getTodayEvent(context, uri);

        if (event == null){
            return;
        }

        //make it in progress
        event.setInProgress();

        //put it in the database
        DataMethods.updateTodayEvent(context, event);

        //display notification
        NotificationsUtils.displayCalendarNotification(context, event, type);
    }


}
