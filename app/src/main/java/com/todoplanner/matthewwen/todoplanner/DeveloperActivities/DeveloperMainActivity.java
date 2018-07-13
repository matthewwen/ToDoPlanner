package com.todoplanner.matthewwen.todoplanner.developerActivities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.data.PreferenceUtils;
import com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase.DeveloperTaskActivity;
import com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase.developerEventActivities.DeveloperPastEventActivity;
import com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase.developerEventActivities.DeveloperPendingEventActivity;
import com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase.developerEventActivities.DeveloperTodayEventActivity;
import com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase.DeveloperNoteActivity;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.receivers.AlarmEventReminderReceiver;
import com.todoplanner.matthewwen.todoplanner.sync.UpdateDelayedEventJobService;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.TodayEventEntry;

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


    //Pending Intent act as ID.
    private static PendingIntent pendingIntentReminderNotification;

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
                null,
                "Wang meets Wen at Purdon't",
                "1pm to 5pm",
                "516 Northwestern Ave, West Lafayette, IN 47906",
                NotificationsUtils.EVENT_REMINDER_START);

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

    public void showTodayEventDatabase(View view) {
        Intent intent = new Intent(this, DeveloperTodayEventActivity.class);
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
        getContentResolver().delete(TodayEventEntry.EVENT_CONTENT_URI,
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
        DataMethods.createEvent(this, "Event 1", startingValue1, endValue1);
        DataMethods.createEvent(this, "Event 2", endValue1, endValue2);
        DataMethods.createEvent(this,"Event 3", endValue2, endValue3);
        DataMethods.createEvent(this,"Event 4", endValue3, endValue4);
        DataMethods.createEvent(this,"Event 5", endValue4, endValue5);

    }

    public void developerScenarioCalendarEvent(View view) {
        Log.v(TAG, "Event Created");
        //Turn off all services
        PreferenceUtils.setEventDeveloperJobService(this, false);
        PreferenceUtils.setReminderDeveloperJobService(this, false);

        //Delete all Events
        getContentResolver().delete(TodayEventEntry.EVENT_CONTENT_URI,
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
        DataMethods.createEvent(this,"Event 1", startingValue1, endValue1);
        DataMethods.createEvent(this,"Event 2", endValue1, endValue2);
        DataMethods.createEvent(this,"Event 3", endValue2, endValue3);
        DataMethods.createEvent(this,"Event 4", endValue3, endValue4);
        DataMethods.createEvent(this,"Event 5", endValue4, endValue5);

        NotificationsUtils.setAlarmNextEvent(this);
    }

    public void showPendingEventDatabase(View view) {
        Intent intent = new Intent(this, DeveloperPendingEventActivity.class);
        startActivity(intent);
    }

    public void showPastEventDatabase(View view) {
        Intent intent = new Intent(this, DeveloperPastEventActivity.class);
        startActivity(intent);
    }
}
