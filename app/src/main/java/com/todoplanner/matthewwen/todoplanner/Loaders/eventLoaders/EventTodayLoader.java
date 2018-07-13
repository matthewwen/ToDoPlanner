package com.todoplanner.matthewwen.todoplanner.loaders.eventLoaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class EventTodayLoader extends AsyncTaskLoader<ArrayList<Event>> {

    private static final String TAG = EventTodayLoader.class.getSimpleName();

    public EventTodayLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Event> loadInBackground() {
        return DataMethods.getAllTodayEvents(getContext());
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
