package com.todoplanner.matthewwen.todoplanner.alarmService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.alarmService.methods.SetAlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.alarmService.methods.SetupAlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.CommonBehavior;
import com.todoplanner.matthewwen.todoplanner.jobServices.JobServiceMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class CalendarReminderReceiver extends BroadcastReceiver{



    private static final String TAG = CalendarReminderReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get the Uri and the type
        String action = intent.getAction();
        Uri uri = Uri.parse(action);
        int type = intent.getIntExtra(context.getString(R.string.notification_type_setup_key), -1);
        Event event = DataMethods.getTodayEvent(context, uri);
        assert event != null;
        switch (type){
            case SetAlarmServiceMethods.DEVELOPER_REMINDER_EVENT_START:
                SetupAlarmServiceMethods.setupStartEvent(context, event); break;
            case SetAlarmServiceMethods.DEVELOPER_REMINDER_STATIC_START:
                SetupAlarmServiceMethods.setUpStaticEvent(context, event); break;
            case SetAlarmServiceMethods.DEVELOPER_REMINDER_EVENT_END:
                SetupAlarmServiceMethods.setUpEndEvent(context, event); break;
        }
    }


}
