package com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase.developerEventActivities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.adapter.eventAdapter.DeveloperEventTodayAdapter;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.loaders.eventLoaders.EventTodayLoader;
import com.todoplanner.matthewwen.todoplanner.objects.Event;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.TodayEventEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DeveloperTodayEventActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Event>>{

    private static final String EVENT_NAME = "Mini Golf";
    private static final String NOTE = "Remember to be nice to Rahul even though " +
            "everytime you guys compare you guys height, you get angry!!";
    private static final int TASK_ID = -1;
    private static final String TAG = DeveloperTodayEventActivity.class.getSimpleName();


    private DeveloperEventTodayAdapter mAdapter;

    private static final int LOADER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_today_event);

        //Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAdapter = new DeveloperEventTodayAdapter(this, new ArrayList<Event>());

        RecyclerView recyclerView = findViewById(R.id.developer_event_today_rv);

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
            case R.id.developer_menu_refresh_event: getLoaderManager().restartLoader(LOADER_ID, null, this); return true;
            case R.id.developer_menu_clear_event:
                getContentResolver().delete(TodayEventEntry.EVENT_CONTENT_URI, null, null);
                getLoaderManager().restartLoader(LOADER_ID, null, this);
                return true;
        }
        return false;
    }

    //add event to the database
    public boolean addEvent(){
        long start = new Date().getTime();
        long end = TimeUnit.MINUTES.toMillis(15) + start;
        DataMethods.createEvent(this, EVENT_NAME, start, end);
        getLoaderManager().restartLoader(LOADER_ID, null, this);
        return true;
    }

    @Override
    public Loader<ArrayList<Event>> onCreateLoader(int id, Bundle args) {
        return new EventTodayLoader(this);
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
