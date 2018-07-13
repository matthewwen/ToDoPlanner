package com.todoplanner.matthewwen.todoplanner.loaders.eventLoaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class EventPastLoader extends AsyncTaskLoader<ArrayList<Event>> {

    public EventPastLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Event> loadInBackground() {
        Cursor cursor = getContext().getContentResolver().query(DataContract.PastEventEntry.EVENT_CONTENT_URI,
                DataContract.PastEventEntry.PROJECTION,
                null,
                null,
                DataContract.PastEventEntry.COLUMN_EVENT_START);
        assert cursor != null;
        cursor.moveToPosition(-1);
        ArrayList<Event> allEvents = new ArrayList<>();
        while (cursor.moveToNext()){
            int id = cursor.getInt(DataContract.PastEventEntry.COLUMN_EVENT_ID_FULL_INDEX);
            String name = cursor.getString(DataContract.PastEventEntry.COLUMN_EVENT_NAME_FULL_INDEX);
            long start = cursor.getLong(DataContract.PastEventEntry.COLUMN_EVENT_START_FULL_INDEX);
            long end = cursor.getLong(DataContract.PastEventEntry.COLUMN_EVENT_END_FULL_INDEX);
            String note = cursor.getString(DataContract.PastEventEntry.COLUMN_EVENT_NOTE_FULL_INDEX);
            int taskId = cursor.getInt(DataContract.PastEventEntry.COLUMN_EVENT_TASK_ID_FULL_INDEX);
            Event temp = new Event(id,name, start, end, note, taskId);
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
