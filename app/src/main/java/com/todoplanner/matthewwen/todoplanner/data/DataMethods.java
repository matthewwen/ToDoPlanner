package com.todoplanner.matthewwen.todoplanner.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.data.DataContract.EventEntry;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;

public class DataMethods {

    private static final String TAG = DataMethods.class.getSimpleName();

    //reset everything until all the events are not in progress
    public static void noneInProgress(Context context){
        //Get the cursor for all the events
        Cursor cursor = context.getContentResolver().query(EventEntry.EVENT_CONTENT_URI,
                EventEntry.PROJECTION,
                null,
                null,
                EventEntry.COLUMN_EVENT_START);
        assert cursor != null;

        int idIndex = cursor.getColumnIndex(EventEntry._ID);
        int inProgIndex = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_IN_PROGRESS);

        cursor.moveToPosition(-1);

        while (cursor.moveToNext()){
            if (cursor.getInt(inProgIndex)
                    == EventEntry.EVENT_IN_PROGRESS){
                   int id = cursor.getInt(idIndex);
                   Uri uri = ContentUris.withAppendedId(EventEntry.EVENT_CONTENT_URI, id);
                   ContentValues values = getContentValues(context, uri);
                  values.put(EventEntry.COLUMN_EVENT_IN_PROGRESS, EventEntry.EVENT_NOT_IN_PROGRESS);
                  context.getContentResolver().update(uri, values, null, null);
            }
        }

        cursor.close();
    }

    //get all the content values for a particular uri
    public static ContentValues getContentValues(Context context, Uri uri){
        Cursor cursor = context.getContentResolver().query(uri,
                EventEntry.PROJECTION,
                null,
                null,
                null);

        assert cursor != null;
        int nameIndex = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_NAME);
        int startIndex = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_START);
        int endIndex = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_END);
        int noteIndex = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_NOTE);
        int taskIndex = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_TASK_ID);

        cursor.moveToPosition(0);
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventEntry.COLUMN_EVENT_NAME, cursor.getString(nameIndex));
        contentValues.put(EventEntry.COLUMN_EVENT_START, cursor.getLong(startIndex));
        contentValues.put(EventEntry.COLUMN_EVENT_END, cursor.getLong(endIndex));
        contentValues.put(EventEntry.COLUMN_EVENT_NOTE, cursor.getString(noteIndex));
        contentValues.put(EventEntry.COLUMN_EVENT_TASK_ID, cursor.getInt(taskIndex));
        cursor.close();

        return contentValues;
    }
}
