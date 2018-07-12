package com.todoplanner.matthewwen.todoplanner.receivers.events;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationPendingIntent;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmEventCalendarReminderReceiver extends BroadcastReceiver{

    //This is for displaying the range
    private static final SimpleDateFormat GET_ADVANCE_FORMAT = new SimpleDateFormat("h:mm", Locale.ENGLISH);
    private static final SimpleDateFormat GET_SIMPLE_FORMAT = new SimpleDateFormat("h", Locale.ENGLISH);
    private static final SimpleDateFormat GET_MINUTE = new SimpleDateFormat("m", Locale.ENGLISH);
    private static final SimpleDateFormat GET_AM_PM = new SimpleDateFormat("a", Locale.ENGLISH);

    private static final String TAG = AlarmEventCalendarReminderReceiver.class.getSimpleName();

    //This is a reference to Simple format for comparing times
    public static final SimpleDateFormat GET_COMPARE_FORMAT = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get the Uri and the type
        String action = intent.getAction();
        Uri uri = Uri.parse(action);
        String type = intent.getStringExtra(context.getString(R.string.notification_event_start_stop_key));

        //Get the Cursor
        Cursor cursor = context.getContentResolver().query(uri,
                DataContract.EventEntry.PROJECTION,
                null,
                null,
                DataContract.EventEntry.COLUMN_EVENT_START);
        assert cursor != null;
        cursor.moveToPosition(-1);
        cursor.moveToNext();

        //get all the values
        int titleIndex = cursor.getColumnIndex(DataContract.EventEntry.COLUMN_EVENT_NAME);
        int longStartIndex = cursor.getColumnIndex(DataContract.EventEntry.COLUMN_EVENT_START);
        int longEndIndex = cursor.getColumnIndex(DataContract.EventEntry.COLUMN_EVENT_END);

        String title = cursor.getString(titleIndex);
        long longStart = cursor.getLong(longStartIndex);
        long longEnd = cursor.getLong(longEndIndex);
        Date dateStart = new Date(longStart);
        Date dateEnd = new Date(longEnd);

        String range = "";
        if (GET_MINUTE.format(dateStart).equals("0") &&
                GET_MINUTE.format(dateEnd).equals("0")){
            range = GET_SIMPLE_FORMAT.format(dateStart) +
                    " - " + GET_SIMPLE_FORMAT.format(dateEnd) + " " +
                    GET_AM_PM.format(dateEnd);
        }else {
            range = GET_ADVANCE_FORMAT.format(dateStart) +
                    " - " + GET_ADVANCE_FORMAT.format(dateEnd) + " " +
                    GET_AM_PM.format(dateEnd);
        }

        cursor.close();

        //If notification on start, make it in progress
        if (type.equals(NotificationsUtils.EVENT_REMINDER_START)){
            DataMethods.noneInProgress(context);
            ContentValues values = DataMethods.getContentValues(context, uri);
            values.put(DataContract.EventEntry.COLUMN_EVENT_IN_PROGRESS,
                    DataContract.EventEntry.EVENT_IN_PROGRESS);
            context.getContentResolver().update(uri, values, null, null);
        }

        NotificationsUtils.displayCalendarNotification(context, uri, title, range, "", type);
    }
}
