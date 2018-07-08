package com.todoplanner.matthewwen.todoplanner.developerActivities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.adapters.DeveloperEventAdapter;
import com.todoplanner.matthewwen.todoplanner.loaders.EventLoader;
import com.todoplanner.matthewwen.todoplanner.objects.Event;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.EventEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DeveloperEventActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Event>>{

    private static final String EVENT_NAME = "Mini Golf";
    private static final String NOTE = "Remember to be nice to Rahul even though " +
            "everytime you guys compare you guys height, you get angry!!";
    private static final int TASK_ID = -1;

    private DeveloperEventAdapter mAdapter;

    private static final int LOADER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_event);

        //Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAdapter = new DeveloperEventAdapter(this, new ArrayList<Event>());

        RecyclerView recyclerView = findViewById(R.id.developer_event_rv);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.developer_add_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home: super.onBackPressed(); return true;
            case R.id.developer_menu_add_event: return addEvent();
            case R.id.developer_menu_clear_event:
                getContentResolver().delete(EventEntry.EVENT_CONTENT_URI, null, null);
                mAdapter.notifyDataSetChanged();
                return true;
        }
        return false;
    }

    //add event to the database
    public boolean addEvent(){
        long start = new Date().getTime();
        long end = TimeUnit.MINUTES.toMillis(15) + start;
        ContentValues values = new ContentValues();
        values.put(EventEntry.COLUMN_EVENT_NAME, EVENT_NAME);
        values.put(EventEntry.COLUMN_EVENT_START, start);
        values.put(EventEntry.COLUMN_EVENT_END, end);
        values.put(EventEntry.COLUMN_EVENT_NOTE, NOTE);
        values.put(EventEntry.COLUMN_EVENT_TASK_ID, TASK_ID);

        getContentResolver().insert(EventEntry.EVENT_CONTENT_URI, values);

        getLoaderManager().restartLoader(LOADER_ID, null, this);

        return true;
    }

    @Override
    public Loader<ArrayList<Event>> onCreateLoader(int id, Bundle args) {
        return new EventLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Event>> loader, ArrayList<Event> data) {
        mAdapter.setAllEvents(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Event>> loader) {
        mAdapter.clear();
    }
}
