package com.todoplanner.matthewwen.todoplanner.developerActivities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.PreferenceUtils;
import com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase.DeveloperEventActivity;
import com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase.DeveloperNoteActivity;
import com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase.DeveloperTaskActivity;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;
import com.todoplanner.matthewwen.todoplanner.receivers.AlarmEventReminderReceiver;
import com.todoplanner.matthewwen.todoplanner.receivers.events.AlarmEventStartReminderReceiver;
import com.todoplanner.matthewwen.todoplanner.sync.UpdateDelayedEventJobService;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.EventEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeveloperMainActivity extends AppCompatActivity
implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = DeveloperMainActivity.class.getSimpleName();

    private Button mEventButton;
    private Button mReminderButton;

    //ID and Tag for Receivers
    private static final int DEVELOPER_REMINDER_EVENT_SERVICE = 2;
    private static final int DEVELOPER_REMINDER_EVENT_START = 3;
    private static final int DEVELOOPER_REMINDER_EVENT_END = 5;

    //Pending Intent act as ID.
    private static PendingIntent pendingIntentReminderNotification;
    private static PendingIntent pendingIntentStartEvent;
    private static PendingIntent pendingIntentEndEvent;

    //ID and Tag for Job Services
    private static final int DEVELOPER_JOB_SERVICE_TEST = 1;

    //The On and Off switch for the job service to add event
    private static final String ADD_EVENT_JOB_SERVICE_ON = "Schedule Job Service: On";
    private static final String ADD_EVENT_JOB_SERVICE_OFF = "Schedule Job Service: Off";

    //The On and off switch for the job service that reminds the user
    private static final String REMINDER_EVENT_JOB_SERVICE_ON = "Event Job Service: On";
    private static final String REMINDER_EVENT_JOB_SERVICE_OFF = "Event Job Service: Off";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mEventButton = findViewById(R.id.developer_job_service_b);
        mReminderButton = findViewById(R.id.developer_reminder_service_b);

        //on click listeners
        mEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.changeEventDeveloperJobService(DeveloperMainActivity.this);
            }
        });
        mReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.changeReminderDeveloperJobService(DeveloperMainActivity.this);
            }
        });


        //Display Button
        if (PreferenceUtils.getEventDeveloperJobService(this)){
            mEventButton.setText(ADD_EVENT_JOB_SERVICE_ON);
            createEventJobService();
        }else {
            mEventButton.setText(ADD_EVENT_JOB_SERVICE_OFF);
            cancelEventJobService();
        }
        if (PreferenceUtils.getReminderDeveloperJobService(this)){
            mReminderButton.setText(REMINDER_EVENT_JOB_SERVICE_ON);
            createReminderAlarmService();
        }else {
            mReminderButton.setText(REMINDER_EVENT_JOB_SERVICE_OFF);
            cancelReminderAlarmService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
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

    //create the job service that changes the event every 15 minutes
    private void createEventJobService(){
        ComponentName serviceName = new ComponentName(getPackageName(), UpdateDelayedEventJobService.class.getName());
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        if (!jobExist(DEVELOPER_JOB_SERVICE_TEST, scheduler.getAllPendingJobs())) {
            JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(DEVELOPER_JOB_SERVICE_TEST, serviceName)
                    .setPersisted(true)
                    .setPeriodic(TimeUnit.MINUTES.toMillis(15));
            scheduler.schedule(jobInfoBuilder.build());
            Log.v(TAG, "Job was created");

        }else{
            Log.v(TAG, "Job already existed");
        }
    }

    //see if the job already exists
    private boolean jobExist(int id, List<JobInfo> allJobs){
        for (int i = 0 ; i < allJobs.size(); i++){
            if (allJobs.get(i).getId() == id){
                return true;
            }
        }
        return false;
    }

    //cancel the interval 15 minutes job thing
    private void cancelEventJobService(){
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        scheduler.cancel(DEVELOPER_JOB_SERVICE_TEST);
    }

    //schedule an alarm at a certain time
    private void createReminderAlarmService(){
        Log.v(TAG,"Reminder Alarm Service Created");
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        assert manager != null;
        if (pendingIntentReminderNotification != null) manager.cancel(pendingIntentReminderNotification);
        Intent intent = new Intent(this, AlarmEventReminderReceiver.class);
        pendingIntentReminderNotification = PendingIntent.getBroadcast(this, DEVELOPER_REMINDER_EVENT_SERVICE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setRepeating(AlarmManager.RTC_WAKEUP,
                new Date().getTime() + TimeUnit.MINUTES.toMillis(1),
                TimeUnit.MINUTES.toMillis(2),
                pendingIntentReminderNotification);
    }

    //cancel any schedule alarms from showing up.
    private void cancelReminderAlarmService(){
        Log.v(TAG,"Reminder Alarm Service canceled");
        if (pendingIntentReminderNotification != null){
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            assert manager != null;
            manager.cancel(pendingIntentReminderNotification);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceUtils.KEY_DEVELOPER_JOB_SERVICE)) {
            boolean b = PreferenceUtils.getEventDeveloperJobService(this);
            if (b){
                mEventButton.setText(ADD_EVENT_JOB_SERVICE_ON);
                createEventJobService();
            }else {
                mEventButton.setText(ADD_EVENT_JOB_SERVICE_OFF);
                cancelEventJobService();
            }
        }else if (key.equals(PreferenceUtils.KEY_DEVELOPER_EVENT_REMINDER)){
            boolean b = PreferenceUtils.getReminderDeveloperJobService(this);
            if (b){
                mReminderButton.setText(REMINDER_EVENT_JOB_SERVICE_ON);
                createReminderAlarmService();
            }else {
                mReminderButton.setText(REMINDER_EVENT_JOB_SERVICE_OFF);
                cancelReminderAlarmService();
            }
        }
    }

    public void developerScenarioDelayingEvent(View view) {
        //Turn off all services
        PreferenceUtils.setEventDeveloperJobService(this, false);
        PreferenceUtils.setReminderDeveloperJobService(this, false);

        //Delete all Events
        getContentResolver().delete(DataContract.EventEntry.EVENT_CONTENT_URI,
                null, null);

        //Add content values inside of calendar
        //Event 1
        long startingValue1 =  new Date().getTime() + TimeUnit.MINUTES.toMillis(2);
        long endValue1 = startingValue1 + TimeUnit.MINUTES.toMillis(3);
        //Event 2
        long endValue2 = endValue1 + TimeUnit.MINUTES.toMillis(6);
        //Event 3
        long endValue3 = endValue2 + TimeUnit.MINUTES.toMillis(7);
        //Event 4
        long endValue4 = endValue3 + TimeUnit.MINUTES.toMillis(2);
        //Event 5
        long endValue5 = endValue4 + TimeUnit.MINUTES.toMillis(3);

        //Creating all the values
        createEvent("Event 1", startingValue1, endValue1);
        createEvent("Event 2", endValue1, endValue2);
        createEvent("Event 3", endValue2, endValue3);
        createEvent("Event 4", endValue3, endValue4);
        createEvent("Event 5", endValue4, endValue5);

    }

    public void developerScenarioCalendarEvent(View view) {
        Log.v(TAG, "Event Created");
        //Turn off all services
        PreferenceUtils.setEventDeveloperJobService(this, false);
        PreferenceUtils.setReminderDeveloperJobService(this, false);

        //Delete all Events
        getContentResolver().delete(DataContract.EventEntry.EVENT_CONTENT_URI,
                null, null);

        //Event 1
        long startingValue1 =  new Date().getTime() + TimeUnit.MINUTES.toMillis(1);
        long endValue1 = startingValue1 + TimeUnit.MINUTES.toMillis(1);
        //Event 2
        long endValue2 = endValue1 + TimeUnit.MINUTES.toMillis(1);
        //Event 3
        long endValue3 = endValue2 + TimeUnit.MINUTES.toMillis(1);
        //Event 4
        long endValue4 = endValue3 + TimeUnit.MINUTES.toMillis(1);
        //Event 5
        long endValue5 = endValue4 + TimeUnit.MINUTES.toMillis(1);

        //Creating all the values
        createEvent("Event 1", startingValue1, endValue1);
        createEvent("Event 2", endValue1, endValue2);
        createEvent("Event 3", endValue2, endValue3);
        createEvent("Event 4", endValue3, endValue4);
        createEvent("Event 5", endValue4, endValue5);

        //Getting the cursor
        Cursor cursor = getContentResolver().query(EventEntry.EVENT_CONTENT_URI,
                EventEntry.PROJECTION_DATE,
                EventEntry.COLUMN_EVENT_START + " > ?",
                new String[]{Long.toString(new Date().getTime())},
                EventEntry.COLUMN_EVENT_START);
        assert cursor != null;

        //getting all the events
        cursor.moveToPosition(-1);
        ArrayList<Uri> allUri = new ArrayList<>();
        ArrayList<Long> allStartTimes = new ArrayList<>();
        ArrayList<Long> allEndTimes = new ArrayList<>();
        int idIndex = cursor.getColumnIndex(EventEntry._ID);
        int startIndex = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_START);
        int endIndex = cursor.getColumnIndex(EventEntry.COLUMN_EVENT_END);
        while (cursor.moveToNext()){
            allStartTimes.add(cursor.getLong(startIndex));
            allEndTimes.add(cursor.getLong(endIndex));
            allUri.add(ContentUris.withAppendedId(EventEntry.EVENT_CONTENT_URI, cursor.getInt(idIndex)));
        }

        //Setting up all the alarms
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        for (int i = 0; i < allUri.size(); i++){
            Intent intent = new Intent(this, AlarmEventStartReminderReceiver.class);
            intent.setAction(allUri.get(i).toString());
            pendingIntentStartEvent = PendingIntent.getBroadcast(this, DEVELOPER_REMINDER_EVENT_START, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            assert manager != null;
            manager.set(AlarmManager.RTC_WAKEUP, allStartTimes.get(i), pendingIntentStartEvent);
        }


        cursor.close();

    }

    private void createEvent(String name, long startValue, long endValue){
        ContentValues values = new ContentValues();
        values.put(EventEntry.COLUMN_EVENT_NAME, name);
        values.put(EventEntry.COLUMN_EVENT_START, startValue);
        values.put(EventEntry.COLUMN_EVENT_END, endValue);
        values.put(EventEntry.COLUMN_EVENT_NOTE, "THE FLOOR IS LAVA ~ SAID TROY AND ABED");
        values.put(EventEntry.COLUMN_EVENT_TASK_ID, -1);
        values.put(EventEntry.COLUMN_EVENT_IN_PROGRESS, EventEntry.EVENT_NOT_IN_PROGRESS);

        getContentResolver().insert(EventEntry.EVENT_CONTENT_URI, values);
    }
}
