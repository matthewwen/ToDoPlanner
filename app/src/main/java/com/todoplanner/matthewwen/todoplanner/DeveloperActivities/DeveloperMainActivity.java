package com.todoplanner.matthewwen.todoplanner.developerActivities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.adapters.DeveloperEventAdapter;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Task;

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

    public void showTaskDatabase(View view) {
        Intent intent = new Intent(this, DeveloperTaskActivity.class);
        startActivity(intent);
    }

    public void showNotesDatabase(View view) {
        Intent intent = new Intent(this, DeveloperNoteActivity.class);
        startActivity(intent);
    }

    public void developerReminderCalendar(View view) {
        NotificationsUtils.displayCalendarNotification(this,
                "Wang meets Wen at Purdon't",
                "1pm to 5pm",
                "516 Northwestern Ave, West Lafayette, IN 47906");

    }

    public void developerReminderTask(View view) {
        NotificationsUtils.displayReminderNotification(this,
        "(8-21-18) Math Homework",
        "Make sure to turn in this assignment to the teacher");
    }

    public void developerWeatherUpdate(View view) {
        NotificationsUtils.displayWeatherNotification(this,
                "Sunny",
                "It will be Sunny all day long");
    }

    public void showEventDatabase(View view) {
        Intent intent = new Intent(this, DeveloperEventActivity.class);
        startActivity(intent);
    }
}
