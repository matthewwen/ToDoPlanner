package com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase.developerEventActivities;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.adapter.eventAdapter.DeveloperEventPastAdapter;
import com.todoplanner.matthewwen.todoplanner.loaders.eventLoaders.EventPastLoader;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class DeveloperPastEventActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<ArrayList<Event>>{

    private DeveloperEventPastAdapter mAdapter;

    //The id for the loader
    private static final int LOADER_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_past_event);

        //Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAdapter = new DeveloperEventPastAdapter(this, new ArrayList<Event>());
        RecyclerView recyclerView = findViewById(R.id.developer_event_past_rv);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: super.onBackPressed(); return true;
        }
        return false;
    }

    @Override
    public Loader<ArrayList<Event>> onCreateLoader(int id, Bundle args) {
        return new EventPastLoader(this);
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
