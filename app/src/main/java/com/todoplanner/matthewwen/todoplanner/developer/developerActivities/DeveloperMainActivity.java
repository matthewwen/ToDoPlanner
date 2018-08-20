package com.todoplanner.matthewwen.todoplanner.developer.developerActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.developer.DeveloperScenarios;
import com.todoplanner.matthewwen.todoplanner.developer.developerActivities.developerDisplayDatabase.DeveloperTaskActivity;
import com.todoplanner.matthewwen.todoplanner.developer.developerActivities.developerDisplayDatabase.developerEventActivities.DeveloperPastEventActivity;
import com.todoplanner.matthewwen.todoplanner.developer.developerActivities.developerDisplayDatabase.developerEventActivities.DeveloperPendingEventActivity;
import com.todoplanner.matthewwen.todoplanner.developer.developerActivities.developerDisplayDatabase.developerEventActivities.DeveloperTodayEventActivity;
import com.todoplanner.matthewwen.todoplanner.developer.developerActivities.developerDisplayDatabase.DeveloperNoteActivity;

public class DeveloperMainActivity extends AppCompatActivity {

    private static final String TAG = DeveloperMainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home: super.onBackPressed(); return true;
        }
        return false;
    }

    //These are all the Databases
    public void showTaskDatabase(View view) {
        Intent intent = new Intent(this, DeveloperTaskActivity.class);
        startActivity(intent);
    }

    public void showNotesDatabase(View view) {
        Intent intent = new Intent(this, DeveloperNoteActivity.class);
        startActivity(intent);
    }

    public void showTodayEventDatabase(View view) {
        Intent intent = new Intent(this, DeveloperTodayEventActivity.class);
        startActivity(intent);
    }

    public void showPendingEventDatabase(View view) {
        Intent intent = new Intent(this, DeveloperPendingEventActivity.class);
        startActivity(intent);
    }

    public void showPastEventDatabase(View view) {
        Intent intent = new Intent(this, DeveloperPastEventActivity.class);
        startActivity(intent);
    }

    //These are all the scenarios
    public void developerScenarioAllStation(View view) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                DeveloperScenarios.developerAllStaticScenario(DeveloperMainActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(DeveloperMainActivity.this, "All Tasks are added to database", Toast.LENGTH_LONG).show();
            }
        };
        asyncTask.execute();
    }


    public void developerScenarioUpdateNow(View view) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                DeveloperScenarios.developerUpdateOnView(DeveloperMainActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(DeveloperMainActivity.this, "All Tasks are added to dattabase", Toast.LENGTH_LONG).show();
            }
        };
        asyncTask.execute();
    }
}
