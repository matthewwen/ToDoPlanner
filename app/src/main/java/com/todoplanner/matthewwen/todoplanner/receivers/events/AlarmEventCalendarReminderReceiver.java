package com.todoplanner.matthewwen.todoplanner.receivers.events;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmEventCalendarReminderReceiver extends BroadcastReceiver{



    private static final String TAG = AlarmEventCalendarReminderReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get the Uri and the type
        String action = intent.getAction();
        Uri uri = Uri.parse(action);
        String type = intent.getStringExtra(context.getString(R.string.notification_event_start_stop_key));

        //Get the Cursor
        Cursor cursor = context.getContentResolver().query(uri,
                DataContract.TodayEventEntry.PROJECTION,
                null,
                null,
                DataContract.TodayEventEntry.COLUMN_EVENT_START);
        assert cursor != null;
        cursor.moveToPosition(-1);
        cursor.moveToNext();

        //get all the values
        int titleIndex = cursor.getColumnIndex(DataContract.TodayEventEntry.COLUMN_EVENT_NAME);
        int longStartIndex = cursor.getColumnIndex(DataContract.TodayEventEntry.COLUMN_EVENT_START);
        int longEndIndex = cursor.getColumnIndex(DataContract.TodayEventEntry.COLUMN_EVENT_END);

        String title = cursor.getString(titleIndex);
        long longStart = cursor.getLong(longStartIndex);
        long longEnd = cursor.getLong(longEndIndex);
        Date dateStart = new Date(longStart);
        Date dateEnd = new Date(longEnd);

        String range = NotificationsUtils.displayRange(dateStart, dateEnd);

        cursor.close();

        //If notification on start, make it in progress
        if (type.equals(NotificationsUtils.EVENT_REMINDER_START)){
            DataMethods.noneInProgress(context);
            ContentValues values = DataMethods.getContentValues(context, uri);
            values.put(DataContract.TodayEventEntry.COLUMN_EVENT_IN_PROGRESS,
                    DataContract.TodayEventEntry.EVENT_IN_PROGRESS);
            context.getContentResolver().update(uri, values, null, null);
        }

        NotificationsUtils.displayCalendarNotification(context, uri, title, range, "", type);
    }


}
