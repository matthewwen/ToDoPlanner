package com.todoplanner.matthewwen.todoplanner.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class EventLoader extends AsyncTaskLoader<ArrayList<Event>> {


    public EventLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Event> loadInBackground() {
        Cursor cursor = getContext().getContentResolver().query(DataContract.EventEntry.EVENT_CONTENT_URI,
                DataContract.EventEntry.PROJECTION,
                null,
                null,
                DataContract.EventEntry.COLUMN_EVENT_START);

        if (cursor == null) return null;

        ArrayList<Event> allEvents = new ArrayList<>();
        cursor.moveToPosition(-1);

        //Column index
        int idIndex = cursor.getColumnIndex(DataContract.EventEntry._ID);
        int nameIndex = cursor.getColumnIndex(DataContract.EventEntry.COLUMN_EVENT_NAME);
        int startIndex = cursor.getColumnIndex(DataContract.EventEntry.COLUMN_EVENT_START);
        int endIndex = cursor.getColumnIndex(DataContract.EventEntry.COLUMN_EVENT_END);
        int taskIdIndex = cursor.getColumnIndex(DataContract.EventEntry.COLUMN_EVENT_TASK_ID);
        int inProgIndex = cursor.getColumnIndex(DataContract.EventEntry.COLUMN_EVENT_IN_PROGRESS);

        while (cursor.moveToNext()){
            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            long start = cursor.getLong(startIndex);
            long end = cursor.getLong(endIndex);
            int taskId = cursor.getInt(taskIdIndex);
            int inProg = cursor.getInt(inProgIndex);

            Event temp = new Event(id, name, start, end, taskId, inProg);
            allEvents.add(temp);
        }

        return allEvents;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
