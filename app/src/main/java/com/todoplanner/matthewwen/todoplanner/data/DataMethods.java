package com.todoplanner.matthewwen.todoplanner.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
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
        //Get the cursor for all the events
        Cursor cursor = context.getContentResolver().query(TodayEventEntry.EVENT_CONTENT_URI,
                TodayEventEntry.PROJECTION,
                null,
                null,
                TodayEventEntry.COLUMN_EVENT_START);
        assert cursor != null;

        int idIndex = cursor.getColumnIndex(TodayEventEntry._ID);
        int inProgIndex = cursor.getColumnIndex(TodayEventEntry.COLUMN_EVENT_IN_PROGRESS);

        cursor.moveToPosition(-1);

        while (cursor.moveToNext()){
            if (cursor.getInt(inProgIndex)
                    == TodayEventEntry.EVENT_IN_PROGRESS){
                   int id = cursor.getInt(idIndex);
                   Uri uri = ContentUris.withAppendedId(TodayEventEntry.EVENT_CONTENT_URI, id);
                   ContentValues values = getTodayEventContentValues(context, uri);
                  values.put(TodayEventEntry.COLUMN_EVENT_IN_PROGRESS, TodayEventEntry.EVENT_NOT_IN_PROGRESS);
                  context.getContentResolver().update(uri, values, null, null);
            }
        }

        cursor.close();
    }

    private static long roundNearestMinute(long time){
        long remainder = time % TimeUnit.MINUTES.toMillis(1);
        return time - remainder;
    }

    //get all the content values for a particular uri
    public static ContentValues getTodayEventContentValues(Context context, Uri uri){
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

    //move back everything so everything is updated (Uri is current uri)
    public static void updateDataDelay(Context context, Uri uri, ArrayList<Event> allEvents,
                                       boolean showNotification){
        if (moveEverythingBack(context, uri, allEvents, showNotification)){
            Log.v(TAG, "Moving Everything back was a success");
        }else {
            Log.v(TAG, "Moving Everything back was not a success");
        }
    }

    //Move all the events a certain point (Uri is the just completed event)
    private static boolean moveEverythingBack(final Context context, final Uri uri, ArrayList<Event> allEvents,
                                              boolean showNotification){
        //Getting Everything set up
        if (allEvents == null || allEvents.size() == 0){
            return false;
        }
        Event finished  = allEvents.remove(0); //first one is useless.
        long oldEndValue = finished.getEventEnd();
        long current = roundNearestMinute(Calendar.getInstance().getTimeInMillis());
        long difference = current - oldEndValue;
        long ending = oldEndValue;
        //Updating to see what would happen. If return false, then there is an error.
        boolean conti = true;
        for (int i = 0; i < allEvents.size() && conti; i++){
            Event temp = allEvents.get(i);
            if (temp.getStaticInt() == TodayEventEntry.EVENT_STATIONARY){
                if (ending > temp.getEventStart()){
                    return false;
                }
                conti = false;
                while (allEvents.size() > i){
                    allEvents.remove(i);
                }
            }else {
                if (temp.getEventStart() < ending + difference) {
                    temp.setEventStart(temp.getEventStart() + difference);
                    ending = temp.getEventEnd() + difference;
                    temp.setEventEnd(temp.getEventEnd() + difference);
                }else {
                    conti = false;
                    while (allEvents.size() > i){
                        allEvents.remove(i);
                    }
                }
            }
        }
        //If no error, apply to the database.
        if (allEvents.size() == 0) return false;
        Event nextEvent = allEvents.remove(0);
        if (nextEvent.getEventStart() <= Calendar.getInstance().getTime().getTime()) {
            if (showNotification)
                NotificationsUtils.displayCalendarNotificationStart(context, nextEvent);
            nextEvent.setInProgress();
            if (allEvents.size() == 0 || !allEvents.get(0).isStatic()){
                Log.v(TAG, "Created the next alarm service Ending");
                NotificationsUtils.setAlarmNextEventEnd(context, nextEvent);
            }else {
                Log.v(TAG, "We though it was static");
            }
        }

        updateEventInDatabase(context, nextEvent);

        changeToPastEvent(context, uri);

        for (Event temp: allEvents){
            updateEventInDatabase(context, temp);
        }

        return true;
    }

    //move all the events a certain point forward
    public static boolean moveEverythingForward(Context context, Uri uri, ArrayList<Event> allEvents,
                                                boolean showNotification){
        if (allEvents == null || allEvents.size() < 2){
            return false;
        }

        long current = roundNearestMinute(Calendar.getInstance().getTimeInMillis());
        long oldEnd = allEvents.remove(0).getEventEnd();
        long difference = oldEnd - current;

        for (int i = 0; i < allEvents.size(); i++){
            Event temp = allEvents.get(i);
            if (temp.isStatic()){
                while (allEvents.size() > i){
                    allEvents.remove(i);
                }
                return false;
            }else {
                temp.setEventStart(temp.getEventStart()- difference);
                temp.setEventEnd(temp.getEventEnd() - difference);
            }
        }

        if (showNotification){
            NotificationsUtils.displayCalendarNotificationStart(context, allEvents.get(1));
        }

        changeToPastEvent(context, uri);

        for(Event temp: allEvents){
            updateEventInDatabase(context, temp);
        }

        return true;
    }

    //Updating an event into the database.
    private static void updateEventInDatabase(Context context, Event event){
        Uri uri = ContentUris.withAppendedId(TodayEventEntry.EVENT_CONTENT_URI, event.getID());
        ContentValues values = new ContentValues();
        values.put(TodayEventEntry.COLUMN_EVENT_NAME, event.getEventName());
        values.put(TodayEventEntry.COLUMN_EVENT_START, event.getEventStart());
        values.put(TodayEventEntry.COLUMN_EVENT_END, event.getEventEnd());
        values.put(TodayEventEntry.COLUMN_EVENT_NOTE, event.getNote());
        values.put(TodayEventEntry.COLUMN_EVENT_TASK_ID, event.getTaskId());
        values.put(TodayEventEntry.COLUMN_EVENT_IN_PROGRESS, event.getTheProgress());
        values.put(TodayEventEntry.COLUMN_EVENT_STATIONARY, event.getStaticInt());
        context.getContentResolver().update(uri, values, null, null);
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

    //get the ending time of an event using the uri
    public static long getEndTime(Context context, Uri uri){
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{TodayEventEntry._ID, TodayEventEntry.COLUMN_EVENT_END},
                null,
                null,
                null);
        assert cursor != null;

        long value;
        if (cursor.moveToPosition(0)){
            value = cursor.getLong(cursor.getColumnIndex(TodayEventEntry.COLUMN_EVENT_END));
        }else {
            Log.v(TAG, "There was an error");
            value = Long.parseLong("0");
        }
        cursor.close();
        return value;
    }

    //get how long the next alarm is (Uri is the current uri)
    public static long differenceOfNextEvent(Context context, Uri uri){
        ContentValues old = getTodayEventContentValues(context, uri);
        long endTime = old.getAsLong(TodayEventEntry.COLUMN_EVENT_END);
        Cursor cursor = context.getContentResolver().query(TodayEventEntry.EVENT_CONTENT_URI,
                new String[]{TodayEventEntry._ID, TodayEventEntry.COLUMN_EVENT_START, TodayEventEntry.COLUMN_EVENT_END},
                TodayEventEntry.COLUMN_EVENT_START + ">=?",
                new String[]{Long.toString(endTime)},
                TodayEventEntry.COLUMN_EVENT_START);
        assert cursor != null;
        long diff;
        if (cursor.moveToPosition(0)){
            diff = cursor.getLong(cursor.getColumnIndex(TodayEventEntry.COLUMN_EVENT_END)) -
                    cursor.getLong(cursor.getColumnIndex(TodayEventEntry.COLUMN_EVENT_START));
        }else {
            Log.v(TAG, "Error getting difference or it does not exist");
            diff = TimeUnit.MINUTES.toMillis(6);
        }

        cursor.close();

        return diff;
    }

    //adding an event
    public static void createEvent(Context context, String name, long startValue, long endValue){
        ContentValues values = new ContentValues();
        long limit = Calendar.getInstance().getTime().getTime() + TWELVE_HOURS;
        startValue = roundNearestMinute(startValue);
        endValue = roundNearestMinute(endValue);

        if (startValue > limit){
            values.put(PendingEventEntry.COLUMN_EVENT_NAME, name);
            values.put(PendingEventEntry.COLUMN_EVENT_START, startValue);
            values.put(PendingEventEntry.COLUMN_EVENT_END, endValue);
            values.put(PendingEventEntry.COLUMN_EVENT_NOTE, "THE FLOOR IS LAVA ~ SAID TROY AND ABED");
            values.put(PendingEventEntry.COLUMN_EVENT_TASK_ID, PendingEventEntry.NO_TASK_ID);
            values.put(PendingEventEntry.COLUMN_EVENT_STATIONARY, PendingEventEntry.COLUMN_EVENT_STATIONARY);
            context.getContentResolver().insert(PendingEventEntry.EVENT_CONTENT_URI, values);
        }else {
            values.put(TodayEventEntry.COLUMN_EVENT_NAME, name);
            values.put(TodayEventEntry.COLUMN_EVENT_START, startValue);
            values.put(TodayEventEntry.COLUMN_EVENT_END, endValue);
            values.put(TodayEventEntry.COLUMN_EVENT_NOTE, "THE FLOOR IS LAVA ~ SAID TROY AND ABED");
            values.put(TodayEventEntry.COLUMN_EVENT_TASK_ID, TodayEventEntry.NO_TASK_ID);
            values.put(TodayEventEntry.COLUMN_EVENT_STATIONARY, TodayEventEntry.COLUMN_EVENT_STATIONARY);
            context.getContentResolver().insert(TodayEventEntry.EVENT_CONTENT_URI, values);
            Log.v(TAG, "Data inserted into the today database");
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
        cursor.moveToPosition(0);
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
}

