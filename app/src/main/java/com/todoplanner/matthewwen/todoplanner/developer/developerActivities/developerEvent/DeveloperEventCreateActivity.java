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

    private static final String YEAR_START_KEY = "year-start-key"; private int yearStart;
    private static final String MONTH_START_KEY = "month-start-key"; private int monthStart;
    private static final String DAY_OF_MONTH_START_KEY = "day-of-month-start-key"; private int dayOfMonthStart;
    private static final String HOUR_START_KEY = "hour-start-key" ; private int hourStart;
    private static final String MINUTE_START_KEY = "minute-start-key"; private int minuteStart;

    private static final String YEAR_END_KEY = "year-end-key"; private int yearEnd;
    private static final String MONTH_END_KEY = "month-end-key"; private int monthEnd;
    private static final String DAY_OF_MONTH_END_KEY = "day-of-month-end-key"; private int dayOfMonthEnd;
    private static final String HOUR_END_KEY = "hour-end-key"; private int hourEnd;
    private static final String MINUTE_END_KEY = "minute-end-key"; private int minuteEnd;

    //The Start/End Time
    private static final String START_DATE_LONG = "start-date-long"; private long startDate;
    private static final String END_DATE_LONG = "end-date-long"; private long endDate;

    private TextView startDateTV;
    private TextView endDateTV;
    private TextView endTimeTV;
    private TextView startTimeTV;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(YEAR_START_KEY, yearStart);
        outState.putInt(MONTH_START_KEY, monthStart);
        outState.putInt(DAY_OF_MONTH_START_KEY, dayOfMonthStart);
        outState.putInt(HOUR_START_KEY, hourStart);
        outState.putInt(MINUTE_START_KEY, minuteStart);

        outState.putInt(YEAR_END_KEY, yearEnd);
        outState.putInt(MONTH_END_KEY, monthEnd);
        outState.putInt(DAY_OF_MONTH_END_KEY, dayOfMonthEnd);
        outState.putInt(HOUR_END_KEY, hourEnd);
        outState.putInt(MINUTE_END_KEY, minuteEnd);

        outState.putLong(START_DATE_LONG, startDate);
        outState.putLong(END_DATE_LONG, endDate);

        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        yearStart = savedInstanceState.getInt(YEAR_START_KEY);
        monthStart = savedInstanceState.getInt(MONTH_START_KEY);
        dayOfMonthStart = savedInstanceState.getInt(DAY_OF_MONTH_START_KEY);
        hourStart = savedInstanceState.getInt(HOUR_START_KEY);
        minuteStart = savedInstanceState.getInt(MINUTE_START_KEY);

        yearEnd = savedInstanceState.getInt(YEAR_END_KEY);
        monthEnd = savedInstanceState.getInt(MONTH_END_KEY);
        dayOfMonthEnd = savedInstanceState.getInt(DAY_OF_MONTH_END_KEY);
        hourEnd = savedInstanceState.getInt(HOUR_END_KEY);
        minuteEnd = savedInstanceState.getInt(MINUTE_END_KEY);

        startDate = savedInstanceState.getLong(START_DATE_LONG);
        endDate = savedInstanceState.getLong(END_DATE_LONG);
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

        //set all the values
        if (savedInstanceState == null){
            Calendar temp = Calendar.getInstance();
            yearStart = temp.get(Calendar.YEAR);
            monthStart = temp.get(Calendar.MONTH);
            dayOfMonthStart = temp.get(Calendar.DAY_OF_MONTH);
            hourStart = temp.get(Calendar.HOUR_OF_DAY);
            minuteStart = temp.get(Calendar.MINUTE);
            Calendar endTemp = Calendar.getInstance();
            endTemp.setTime(new Date(temp.getTimeInMillis() + TimeUnit.HOURS.toMillis(1)));
            yearEnd = endTemp.get(Calendar.YEAR);
            monthEnd = endTemp.get(Calendar.MONTH);
            dayOfMonthEnd = endTemp.get(Calendar.DAY_OF_MONTH);
            hourEnd = endTemp.get(Calendar.HOUR_OF_DAY);
            minuteEnd = endTemp.get(Calendar.MINUTE);
            startDate = getTime(yearStart, monthStart, dayOfMonthStart, hourStart, minuteStart);
            endDate = getTime(yearEnd, monthEnd, dayOfMonthEnd, hourEnd, minuteEnd);
        }else {
            Log.v(TAG, "Restore instance state");
            onRestoreInstanceState(savedInstanceState);
        }

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
                showDateDialog(START_TIME, yearStart, monthStart, dayOfMonthStart);
            }
        });

        startTimeTV = findViewById(R.id.developer_create_event_start_time_tv);
        startTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(START_TIME, hourStart, minuteStart);
            }
        });

        endDateTV = findViewById(R.id.developer_create_event_end_date_tv);
        endDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(END_TIME, yearEnd, monthEnd, dayOfMonthEnd);
            }
        });

        endTimeTV = findViewById(R.id.developer_create_event_end_time_tv);
        endTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(END_TIME, hourEnd, minuteEnd);
            }
        });

        updateViews();
    }

    private void updateViews(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        startDateTV.setText(dateFormat.format(new Date(startDate)));
        endDateTV.setText(dateFormat.format(new Date(endDate)));
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm aa");
        endTimeTV.setText(timeFormat.format(new Date(endDate)));
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
                startDate,
                endDate,
                staticOrNah);
    }

    @Override
    public void setTime(int type, int hour, int minute) {
        switch (type){
            case START_TIME:
                hourStart = hour; minuteStart = minute;
                startDate = getTime(yearStart, monthStart, dayOfMonthStart, hour, minute); break;
            case END_TIME:
                this.hourEnd = hour; this.minuteEnd = minute;
                endDate = getTime(yearEnd, monthEnd, dayOfMonthEnd, hourEnd, minute); break;
        }
        updateViews();
    }

    @Override
    public void setDate(int type, int year, int month, int dayOfMonth) {
        switch (type){
            case START_TIME:
                this.yearStart = year; this.monthStart = month; this.dayOfMonthStart = dayOfMonth;
                startDate = getTime(year, month, dayOfMonth, hourStart, minuteStart); break;
            case END_TIME:
                this.yearEnd = year; this.monthEnd = month; this.dayOfMonthEnd = dayOfMonth;
                endDate = getTime(year, month, dayOfMonth, hourStart, minuteStart); break;
        }
        updateViews();

    }

    private long getTime(int year, int month, int dayOfMonth, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, hour, minute);
        return DataMethods.roundNearestMinute(calendar.getTimeInMillis());
    }
}
