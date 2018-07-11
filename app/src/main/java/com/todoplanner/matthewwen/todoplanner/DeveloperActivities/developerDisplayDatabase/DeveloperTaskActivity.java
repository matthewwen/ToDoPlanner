package com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.todoplanner.matthewwen.todoplanner.adapters.adapters.DeveloperTaskAdapter;
import com.todoplanner.matthewwen.todoplanner.loaders.TaskLoader;
import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.objects.Task;

import java.util.ArrayList;
import java.util.Date;

public class DeveloperTaskActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Task>> {

    private DeveloperTaskAdapter mAdapter;

    private static final int LOADER_ID = 0;

    private static final String TAG = DeveloperTaskActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_task);

        RecyclerView mRecyclerView = findViewById(R.id.developer_task_rv);
        mAdapter = new DeveloperTaskAdapter(this, new ArrayList<Task>());

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

       getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.developer_add_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home: super.onBackPressed(); return true;
            case R.id.developer_menu_add_task: return insertDummyData();
            case R.id.developer_menu_refresh_task: getLoaderManager().restartLoader(LOADER_ID, null, this); return true;
            case R.id.developer_menu_clear_task: getContentResolver().delete(DataContract.TaskEntry.TASK_CONTENT_URI, null, null); getLoaderManager().restartLoader(LOADER_ID, null, this); return true;
        }
        return false;
    }

    public boolean insertDummyData(){
        ContentValues values = new ContentValues();
        values.put(DataContract.TaskEntry.COLUMN_TASK_NAME, "Task Name Duh " + mAdapter.getItemCount());
        values.put(DataContract.TaskEntry.COLUMN_TASK_DUE_DATE, new Date().getTime());
        values.put(DataContract.TaskEntry.COLUMN_TASK_HAVE_SUB_TASK, 1);
        values.put(DataContract.TaskEntry.COLUMN_TASK_PARENT_TASK, -1);

        getContentResolver().insert(DataContract.TaskEntry.TASK_CONTENT_URI, values);

        getLoaderManager().restartLoader(LOADER_ID, null, this);

        return true;
    }

    @Override
    public Loader<ArrayList<Task>> onCreateLoader(int id, Bundle args) {
        return new TaskLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Task>> loader, ArrayList<Task> data) {
        mAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Task>> loader) {
        mAdapter.clear();
    }

}
