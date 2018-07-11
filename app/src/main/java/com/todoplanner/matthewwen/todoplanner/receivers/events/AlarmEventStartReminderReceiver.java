package com.todoplanner.matthewwen.todoplanner.receivers.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmEventStartReminderReceiver extends BroadcastReceiver{

    private static final SimpleDateFormat GET_ADVANCE_FORMAT = new SimpleDateFormat("h:mm", Locale.ENGLISH);
    private static final SimpleDateFormat GET_SIMPLE_FORMAT = new SimpleDateFormat("h", Locale.ENGLISH);
    private static final SimpleDateFormat GET_MINUTE = new SimpleDateFormat("m", Locale.ENGLISH);
    private static final SimpleDateFormat GET_AM_PM = new SimpleDateFormat("a", Locale.ENGLISH);

    private static final String TAG = AlarmEventStartReminderReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get the Uri
        String action = intent.getAction();
        Uri uri = Uri.parse(action);

        //Get the Cursor
        Cursor cursor = context.getContentResolver().query(uri,
                DataContract.EventEntry.PROJECTION,
                null,
                null,
                DataContract.EventEntry.COLUMN_EVENT_START);
        assert cursor != null;
        cursor.moveToPosition(-1);
        cursor.moveToNext();
        Log.v(TAG, "I Am Here");

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

        Log.v(TAG, "Notification should be displayed now!");

        NotificationsUtils.displayCalendarNotification(context, title, range, "");
    }
}
