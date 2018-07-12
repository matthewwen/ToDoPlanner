package com.todoplanner.matthewwen.todoplanner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;

public class AlarmEventReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationsUtils.displayCalendarNotification(context,
                null,
                "Math Class",
                "1pm-5pm",
                "Home",
                NotificationsUtils.EVENT_REMINDER_START);

    }
}
