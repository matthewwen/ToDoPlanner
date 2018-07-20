package com.todoplanner.matthewwen.todoplanner.loaders.eventLoaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class EventPendingLoader extends AsyncTaskLoader<ArrayList<Event>> {

    public EventPendingLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Event> loadInBackground() {
        Cursor cursor = getContext().getContentResolver().query(DataContract.PendingEventEntry.EVENT_CONTENT_URI,
                DataContract.PendingEventEntry.PROJECTION,
                null,
                null,
                DataContract.PendingEventEntry.COLUMN_EVENT_START);
        assert cursor != null;
        cursor.moveToPosition(-1);
        ArrayList<Event> allEvents = new ArrayList<>();
        while (cursor.moveToNext()){
            int id = cursor.getInt(DataContract.PendingEventEntry.COLUMN_EVENT_ID_FULL_INDEX);
            String name = cursor.getString(DataContract.PendingEventEntry.COLUMN_EVENT_NAME_FULL_INDEX);
            long start = cursor.getLong(DataContract.PendingEventEntry.COLUMN_EVENT_START_FULL_INDEX);
            long end = cursor.getLong(DataContract.PendingEventEntry.COLUMN_EVENT_END_FULL_INDEX);
            String note = cursor.getString(DataContract.PendingEventEntry.COLUMN_EVENT_NOTE_FULL_INDEX);
            int taskId = cursor.getInt(DataContract.PendingEventEntry.COLUMN_EVENT_TASK_ID_FULL_INDEX);
            int station = cursor.getInt(DataContract.PendingEventEntry.COLUMN_EVENT_STATIONARY_FULL_INDEX);
            Event temp = new Event(id,name, start, end, note, taskId, station);
            allEvents.add(temp);
        }
        cursor.close();
        return allEvents;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
