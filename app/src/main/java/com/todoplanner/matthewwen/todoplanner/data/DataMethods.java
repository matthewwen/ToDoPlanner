package com.todoplanner.matthewwen.todoplanner.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.alarmService.AlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.PendingEventEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.TodayEventEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.PastEventEntry;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;

import java.util.concurrent.TimeUnit;

public class DataMethods {

    //Simple Time format to get all the events needed for current day
    private static final long TWELVE_HOURS = TimeUnit.HOURS.toMillis(12);

    private static final String TAG = DataMethods.class.getSimpleName();

    //reset everything until all the events are not in progress
    public static void noneInProgress(Context context){
        ArrayList<Event> allTodayEvents = getAllTodayEvents(context);
        for (int i = 0; i < allTodayEvents.size(); i++){
            allTodayEvents.get(i).setNotInProgress();
            updateTodayEvent(context, allTodayEvents.get(i));
        }
    }

    //This Rounds the time to the nearest minute
    public static long roundNearestMinute(long time){
        long remainder = time % TimeUnit.MINUTES.toMillis(1);
        return time - remainder;
    }

    //Make the event in progress
    public static void updateTodayEvent(Context context, Event event){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TodayEventEntry.COLUMN_EVENT_NAME, event.getEventName());
        contentValues.put(TodayEventEntry.COLUMN_EVENT_START, event.getEventStart());
        contentValues.put(TodayEventEntry.COLUMN_EVENT_END, event.getEventEnd());
        contentValues.put(TodayEventEntry.COLUMN_EVENT_NOTE, event.getNote());
        contentValues.put(TodayEventEntry.COLUMN_EVENT_TASK_ID, event.getTaskId());
        contentValues.put(TodayEventEntry.COLUMN_EVENT_IN_PROGRESS, event.getTheProgress());
        contentValues.put(TodayEventEntry.COLUMN_EVENT_STATIONARY, event.getStaticInt());
        Uri uri = ContentUris.withAppendedId(TodayEventEntry.EVENT_CONTENT_URI, event.getID());
        context.getContentResolver().update(uri, contentValues, null, null);
    }

    //Get all the events
    public static ArrayList<Event> getAllTodayEvents(Context context){
        Cursor cursor = context.getContentResolver().query(DataContract.TodayEventEntry.EVENT_CONTENT_URI,
                DataContract.TodayEventEntry.PROJECTION,
                null,
                null,
                DataContract.TodayEventEntry.COLUMN_EVENT_START);

        assert cursor != null;

        ArrayList<Event> allEvents = new ArrayList<>();
        cursor.moveToPosition(-1);

        while (cursor.moveToNext()){
            int id = cursor.getInt(DataContract.TodayEventEntry.COLUMN_EVENT_ID_FULL_INDEX);
            String name = cursor.getString(DataContract.TodayEventEntry.COLUMN_EVENT_NAME_FULL_INDEX);
            long start = cursor.getLong(DataContract.TodayEventEntry.COLUMN_EVENT_START_FULL_INDEX);
            long end = cursor.getLong(DataContract.TodayEventEntry.COLUMN_EVENT_END_FULL_INDEX);
            int taskId = cursor.getInt(DataContract.TodayEventEntry.COLUMN_EVENT_TASK_ID_FULL_INDEX);
            String note = cursor.getString(DataContract.TodayEventEntry.COLUMN_EVENT_NOTE_FULL_INDEX);
            int inProg = cursor.getInt(DataContract.TodayEventEntry.COLUMN_EVENT_IN_PROGRESS_FULL_INDEX);
            int station = cursor.getInt(DataContract.TodayEventEntry.COLUMN_EVENT_STATIONARY_FULL_INDEX);

            Event temp = new Event(id, name, start, end, taskId, note, inProg, station);
            allEvents.add(temp);
        }

        cursor.close();

        return allEvents;
    }

    //adding an event
    public static void createEvent(Context context, String name, String note,
                                   long startValue, long endValue, int staticType){
        ContentValues values = new ContentValues();
        long limit = Calendar.getInstance().getTimeInMillis() + TWELVE_HOURS;
        startValue = roundNearestMinute(startValue);
        endValue = roundNearestMinute(endValue);

        if (startValue > limit){
            values.put(PendingEventEntry.COLUMN_EVENT_NAME, name);
            values.put(PendingEventEntry.COLUMN_EVENT_START, startValue);
            values.put(PendingEventEntry.COLUMN_EVENT_END, endValue);
            values.put(PendingEventEntry.COLUMN_EVENT_NOTE, note);
            values.put(PendingEventEntry.COLUMN_EVENT_TASK_ID, PendingEventEntry.NO_TASK_ID);
            values.put(PendingEventEntry.COLUMN_EVENT_STATIONARY, staticType);
            Log.v(TAG, "Data inserted into the pending database");
            context.getContentResolver().insert(PendingEventEntry.EVENT_CONTENT_URI, values);
        }else {
            values.put(TodayEventEntry.COLUMN_EVENT_NAME, name);
            values.put(TodayEventEntry.COLUMN_EVENT_START, startValue);
            values.put(TodayEventEntry.COLUMN_EVENT_END, endValue);
            values.put(TodayEventEntry.COLUMN_EVENT_NOTE, note);
            values.put(TodayEventEntry.COLUMN_EVENT_TASK_ID, TodayEventEntry.NO_TASK_ID);
            values.put(TodayEventEntry.COLUMN_EVENT_STATIONARY, staticType);
            context.getContentResolver().insert(TodayEventEntry.EVENT_CONTENT_URI, values);
            Log.v(TAG, "Data inserted into the today database");
            AlarmServiceMethods.setAlarmNextEvent(context);
        }
    }

    //create past event (Last Thing that happens before starting the next event)
    public static void changeToPastEvent(Context context, Uri oldUri) {
        ContentValues oldValues = getTodayEventContentValues(context, oldUri);
        context.getContentResolver().delete(oldUri, null, null);
        ContentValues values = new ContentValues();
        values.put(PastEventEntry.COLUMN_EVENT_NAME, oldValues.getAsString(TodayEventEntry.COLUMN_EVENT_NAME));
        values.put(PastEventEntry.COLUMN_EVENT_START, oldValues.getAsLong(TodayEventEntry.COLUMN_EVENT_START));
        values.put(PastEventEntry.COLUMN_EVENT_END, roundNearestMinute(Calendar.getInstance().getTimeInMillis())); //When this method ends is the proper end time.
        values.put(PastEventEntry.COLUMN_EVENT_NOTE, oldValues.getAsString(TodayEventEntry.COLUMN_EVENT_NOTE));
        values.put(PastEventEntry.COLUMN_EVENT_TASK_ID, oldValues.getAsString(TodayEventEntry.COLUMN_EVENT_TASK_ID));
        context.getContentResolver().insert(PastEventEntry.EVENT_CONTENT_URI, values);
    }

    //Get the Event based off of uri
    public static Event getTodayEvent(Context context, Uri uri){
        Cursor cursor = context.getContentResolver().query(uri, TodayEventEntry.PROJECTION, null, null, null);
        assert cursor != null;
        if (cursor.moveToPosition(0)){
            int id = cursor.getInt(TodayEventEntry.COLUMN_EVENT_ID_FULL_INDEX);
            String name = cursor.getString(TodayEventEntry.COLUMN_EVENT_NAME_FULL_INDEX);
            long start = cursor.getLong(TodayEventEntry.COLUMN_EVENT_START_FULL_INDEX);
            long end = cursor.getLong(TodayEventEntry.COLUMN_EVENT_END_FULL_INDEX);
            int taskId = cursor.getInt(TodayEventEntry.COLUMN_EVENT_TASK_ID_FULL_INDEX);
            String note = cursor.getString(TodayEventEntry.COLUMN_EVENT_NOTE_FULL_INDEX);
            int inProg = cursor.getInt(TodayEventEntry.COLUMN_EVENT_IN_PROGRESS_FULL_INDEX);
            int station = cursor.getInt(TodayEventEntry.COLUMN_EVENT_STATIONARY_FULL_INDEX);
            cursor.close();
            return new Event(id, name, start, end, taskId, note, inProg, station);
        }
        return null;
    }

    //get all the content values for a particular uri
    private static ContentValues getTodayEventContentValues(Context context, Uri uri){
        Cursor cursor = context.getContentResolver().query(uri,
                TodayEventEntry.PROJECTION,
                null,
                null,
                null);

        assert cursor != null;
        int nameIndex = cursor.getColumnIndex(TodayEventEntry.COLUMN_EVENT_NAME);
        int startIndex = cursor.getColumnIndex(TodayEventEntry.COLUMN_EVENT_START);
        int endIndex = cursor.getColumnIndex(TodayEventEntry.COLUMN_EVENT_END);
        int noteIndex = cursor.getColumnIndex(TodayEventEntry.COLUMN_EVENT_NOTE);
        int taskIndex = cursor.getColumnIndex(TodayEventEntry.COLUMN_EVENT_TASK_ID);

        cursor.moveToPosition(0);
        ContentValues contentValues = new ContentValues();
        contentValues.put(TodayEventEntry.COLUMN_EVENT_NAME, cursor.getString(nameIndex));
        contentValues.put(TodayEventEntry.COLUMN_EVENT_START, cursor.getLong(startIndex));
        contentValues.put(TodayEventEntry.COLUMN_EVENT_END, cursor.getLong(endIndex));
        contentValues.put(TodayEventEntry.COLUMN_EVENT_NOTE, cursor.getString(noteIndex));
        contentValues.put(TodayEventEntry.COLUMN_EVENT_TASK_ID, cursor.getInt(taskIndex));
        cursor.close();

        return contentValues;
    }

    //get all the Pending Events with a start time above a certain time
    public static ArrayList<Event> getEssentialPendingEvents(Context context, long limit){
        Cursor cursor = context.getContentResolver().query(PendingEventEntry.EVENT_CONTENT_URI,
                PendingEventEntry.PROJECTION,
                PendingEventEntry.COLUMN_EVENT_START +"<=?",
                new String[]{String.valueOf(limit)},
                PendingEventEntry.COLUMN_EVENT_START);
        if (cursor == null || cursor.getCount() == 0){
            return null;
        }
        ArrayList<Event> arrayList = new ArrayList<>();
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()){
            int id = cursor.getInt(PendingEventEntry.COLUMN_EVENT_ID_FULL_INDEX);
            String name = cursor.getString(PendingEventEntry.COLUMN_EVENT_NAME_FULL_INDEX);
            long start = cursor.getLong(PendingEventEntry.COLUMN_EVENT_START_FULL_INDEX);
            long end = cursor.getLong(PendingEventEntry.COLUMN_EVENT_END_FULL_INDEX);
            int taskid = cursor.getInt(PendingEventEntry.COLUMN_EVENT_TASK_ID_FULL_INDEX);
            String note = cursor.getString(PendingEventEntry.COLUMN_EVENT_NOTE_FULL_INDEX);
            int station = cursor.getInt(PendingEventEntry.COLUMN_EVENT_STATIONARY_FULL_INDEX);
            Event temp = new Event(id, name, start, end, note, taskid, station);
            arrayList.add(temp);
        }
        cursor.close();
        return arrayList;
    }

    //delete an event from the pending database
    public static void deletePendingEvent(Context context, Event event){
        int id = event.getID();
        Uri uri = ContentUris.withAppendedId(PendingEventEntry.EVENT_CONTENT_URI, id);
        context.getContentResolver().delete(uri,
                null,
                null);
    }

    //insert event into Today Event Table
    public static void insertTodayEvent(Context context, Event event){
        String name = event.getEventName();
        long startValue = event.getEventStart();
        long endValue = event.getEventEnd();
        String note = event.getNote();
        //int taskId = event.getTaskId();
        int staticType = event.getStaticInt();

        createEvent(context, name, note, startValue, endValue, staticType);
    }


}

