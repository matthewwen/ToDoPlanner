package com.todoplanner.matthewwen.todoplanner.developer.developerActivities.developerEvent;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.popUpDialog.DateDialog;
import com.todoplanner.matthewwen.todoplanner.popUpDialog.TimeDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DeveloperEventCreateActivity extends AppCompatActivity
    implements DateDialog.DatePopUp, TimeDialog.TimePopUP {

    private static final String TAG = DeveloperEventCreateActivity.class.getSimpleName();

    private EditText nameET;
    private EditText noteET;
    private Switch stationS;

    private static final int START_TIME = 1;
    private static final int END_TIME = 2;

    //The Key
    private static final String DATE_PICKER_KEY = "DatePicker";
    private static final String TIME_PICKER_KEY = "TimePicker";

    private static final String RANGE_KEY = "range-key"; private long range;

    //The Start/End Time
    private static final String START_DATE_LONG = "start-date-long"; private long startDate;

    private TextView startDateTV;
    private TextView endDateTV;
    private TextView endTimeTV;
    private TextView startTimeTV;

    private Calendar startCal;
    private Calendar endCal;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(RANGE_KEY, range);

        outState.putLong(START_DATE_LONG, startDate);

        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        range = savedInstanceState.getLong(RANGE_KEY);

        startDate = savedInstanceState.getLong(START_DATE_LONG);
    }

    //initialize date dialog
    private void showDateDialog(int type, int year, int month, int dayofMonth){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = DateDialog.newInstance(type, year, month, dayofMonth);
        newFragment.show(ft, DATE_PICKER_KEY);
    }

    private void  showTimeDialog(int type, int hour, int minute){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        TimeDialog newFragment = TimeDialog.newInstance(type, hour, minute);
        newFragment.show(ft, TIME_PICKER_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_event_create);

        //Initialize all the views
        nameET = findViewById(R.id.developer_create_event_name_et);
        noteET = findViewById(R.id.developer_create_event_note_et);
        stationS = findViewById(R.id.developer_create_event_stationary_s);

        if (savedInstanceState == null){
            range = TimeUnit.HOURS.toMillis(1);
            startDate = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis());
        }else {
            Log.v(TAG, "Restore instance state");
            onRestoreInstanceState(savedInstanceState);
        }

        //set all the values
        startCal = Calendar.getInstance();
        startCal.setTimeInMillis(startDate);
        long timeEndCal = startCal.getTimeInMillis() + range;
        endCal = Calendar.getInstance();
        endCal.setTimeInMillis(timeEndCal);

        FloatingActionButton fab = findViewById(R.id.developer_create_event_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
                DeveloperEventCreateActivity.super.onBackPressed();
            }
        });

        startDateTV = findViewById(R.id.developer_create_event_start_date_tv);
        startDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int yearStart = startCal.get(Calendar.YEAR);
                int monthStart = startCal.get(Calendar.MONTH);
                int dayOfMonthStart = startCal.get(Calendar.DAY_OF_MONTH);
                showDateDialog(START_TIME, yearStart, monthStart, dayOfMonthStart);
            }
        });

        startTimeTV = findViewById(R.id.developer_create_event_start_time_tv);
        startTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hourStart = startCal.get(Calendar.HOUR_OF_DAY);
                int minuteStart = startCal.get(Calendar.MINUTE);
                showTimeDialog(START_TIME, hourStart, minuteStart);
            }
        });

        endDateTV = findViewById(R.id.developer_create_event_end_date_tv);
        endDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int yearEnd = endCal.get(Calendar.YEAR);
                int monthEnd = endCal.get(Calendar.MONTH);
                int dayOfMonthEnd = endCal.get(Calendar.DAY_OF_MONTH);
                showDateDialog(END_TIME, yearEnd, monthEnd, dayOfMonthEnd);
            }
        });

        endTimeTV = findViewById(R.id.developer_create_event_end_time_tv);
        endTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hourEnd = endCal.get(Calendar.HOUR_OF_DAY);
                int minuteEnd = endCal.get(Calendar.MINUTE);
                showTimeDialog(END_TIME, hourEnd, minuteEnd);
            }
        });

        updateViews();
    }

    private void updateViews(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        startDateTV.setText(dateFormat.format(new Date(startDate)));
        endDateTV.setText(dateFormat.format(new Date(startDate + range)));
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm aa");
        endTimeTV.setText(timeFormat.format(new Date(startDate + range)));
        startTimeTV.setText(timeFormat.format(new Date(startDate)));
    }

    private int getSwitchInteger(Switch temp){
        if (temp.isChecked()){
            return DataContract.TodayEventEntry.EVENT_STATIONARY;
        }
        return DataContract.TodayEventEntry.EVENT_NOT_STATIONARY;
    }

    private void onButtonClick(){
        int staticOrNah = getSwitchInteger(stationS);
        String name = nameET.getText().toString();
        String note = noteET.getText().toString();
        DataMethods.createEvent(this,
                name,
                note,
                DataMethods.roundNearestMinute(startDate),
                DataMethods.roundNearestMinute(startDate + range),
                staticOrNah);
    }

    @Override
    public void setTime(int type, int hour, int minute) {
        switch (type){
            case START_TIME:
                startDate = getTime(startCal.get(Calendar.YEAR),
                        startCal.get(Calendar.MONTH),
                        startCal.get(Calendar.DAY_OF_MONTH),
                        hour,
                        minute);
                startCal.setTimeInMillis(startDate);
                endCal.setTimeInMillis(startDate + range);
                break;
            case END_TIME:
                endCal.set(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DAY_OF_MONTH), hour, minute);
                range = endCal.getTimeInMillis() - startDate;
                break;
        }
        updateViews();
    }

    @Override
    public void setDate(int type, int year, int month, int dayOfMonth) {
        switch (type){
            case START_TIME:
                startCal.setTimeInMillis(startDate);
                startCal.set(year, month, dayOfMonth, startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE));
                startDate = startCal.getTimeInMillis(); endCal.setTimeInMillis(startDate + range);break;
            case END_TIME:
                endCal.setTimeInMillis(startDate + range);
                endCal.set(year, month, dayOfMonth, endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE));
                range = endCal.getTimeInMillis() - startDate; break;
        }

        updateViews();

    }

    private long getTime(int year, int month, int dayOfMonth, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, hour, minute);
        return DataMethods.roundNearestMinute(calendar.getTimeInMillis());
    }
}
