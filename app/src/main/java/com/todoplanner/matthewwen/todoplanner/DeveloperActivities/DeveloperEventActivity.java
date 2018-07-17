package com.todoplanner.matthewwen.todoplanner.developerActivities;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.TodayEventEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.InAppBehavior;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DeveloperEventActivity extends AppCompatActivity {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //from the table 'userTodayEvent'
    private static final int TABLE_EVENT_TODAY_ID = 301;

    //from the table 'userPendingEvent'
    private static final int TABLE_PENDING_EVENT_ID = 303;


    //from the table 'userPastEvent'
    private static final int TABLE_PAST_EVENT_ID = 305;

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //This is for the Today events
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + DataContract.TodayEventEntry.TABLE_NAME +"/#", TABLE_EVENT_TODAY_ID);

        //This is for the Past events
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + DataContract.PastEventEntry.TABLE_NAME +"/#", TABLE_PAST_EVENT_ID);

        //This is for the Pending events
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + DataContract.PendingEventEntry.TABLE_NAME +"/#", TABLE_PENDING_EVENT_ID);

        return uriMatcher;

    }

    private TextView idtv;
    private TextView nameTv;
    private TextView startTv;
    private TextView endTv;
    private TextView taskIDTv;
    private TextView noteTv;
    private TextView inProgTv;
    private TextView stationTv;
    private Button next;

    private static final String TAG  = DeveloperEventActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_event);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        idtv = findViewById(R.id.developer_event_id);
        nameTv = findViewById(R.id.developer_event_name);
        startTv = findViewById(R.id.developer_event_start);
        endTv = findViewById(R.id.developer_event_end);
        taskIDTv = findViewById(R.id.developer_event_task_id);
        noteTv = findViewById(R.id.developer_event_note);
        inProgTv = findViewById(R.id.developer_event_in_progress);
        stationTv = findViewById(R.id.developer_event_stationary);
        next = findViewById(R.id.developer_event_finished);

        Intent intent = getIntent();
        Uri uri;
        if (intent != null){
            uri = Uri.parse(intent.getAction());
            int id = sUriMatcher.match(uri);
            switch (id){
                case TABLE_EVENT_TODAY_ID: setUpToday(uri); break;
            }

        }

    }

    @SuppressLint("SetTextI18n")
    private void setUpToday(final Uri uri){
        Event values = DataMethods.getTodayEvent(this, uri);
        int id = values.getID();
        String name = values.getEventName();
        long start = values.getEventStart();
        final long end = values.getEventEnd();
        int taskId = values.getTaskId();
        String note = values.getNote();
        boolean inProg = values.getInProgress();
        int station = values.getStaticInt();

        idtv.setText("ID: " + Integer.toString(id));
        nameTv.setText("Name: " + name);
        startTv.setText("Start: " + new Date(start).toString());
        endTv.setText("End: " + new Date(end).toString());
        taskIDTv.setText("Task ID: " + Integer.toString(taskId));
        noteTv.setText("Note: " + note);
        inProgTv.setText("In Progress: " + Boolean.toString(inProg));
        stationTv.setText("Station: " + Integer.toString(station));

        if (inProg){
            next.setVisibility(View.VISIBLE);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InAppBehavior.userPressedFinished(DeveloperEventActivity.this);
                    DeveloperEventActivity.this.onBackPressed();
                }
            });
        }else {
            next.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int theMenuId = item.getItemId();
        switch (theMenuId){
            case android.R.id.home: super.onBackPressed(); return true;
        }
        return false;
    }
}
