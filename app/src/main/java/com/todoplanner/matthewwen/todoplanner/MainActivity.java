package com.todoplanner.matthewwen.todoplanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.todoplanner.matthewwen.todoplanner.alarmService.methods.SetAlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.alarmService.methods.SetupAlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.developer.developerActivities.DeveloperMainActivity;
import com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.adaption.DelayBehavior;
import com.todoplanner.matthewwen.todoplanner.jobServices.JobServiceMethods;
import com.todoplanner.matthewwen.todoplanner.jobServices.jobServiceClass.UpdateTodayDatabaseJobService;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

//You could just implement LoaderManger.LoaderCallbacks<Cursor>
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    //use intent filters to determine if the device is connected to wifi or not
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        //Initial Job Services
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //job service
                if (!JobServiceMethods.hasUpdateDatabaseJobService(MainActivity.this)){
                    JobServiceMethods.settingUrgentPendingToToday(MainActivity.this);
                    JobServiceMethods.automatedMoveToTodayJobService(MainActivity.this);
                    Log.v(TAG, "Job Service Created");
                }else {
                    Log.v(TAG, "Job Service Already Exists");
                }
                //alarm service
                SetAlarmServiceMethods.setAlarmService(MainActivity.this);

            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Context context = MainActivity.this;
                Event event = DataMethods.getEventInProgress(context);
                long current = DataMethods.getCurrentTime(context);
                if (event == null){
                    return;
                }
                if (event.getEventEnd() < current){
                    //event.setEventEnd(current);
                    //DataMethods.updateTodayEvent(context, event);
                    ArrayList<Event> allEvents = DataMethods.getAllTodayEvents(context);
                    DelayBehavior.delayAllEvents(context, allEvents, current);
                }
            }
        });
        thread.run();
        thread2.run();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bottom navigation view
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.today_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.developer_menu_option: startActivity(new Intent(this, DeveloperMainActivity.class)); return true;
        }
        return false;
    }
}
