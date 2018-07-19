package com.todoplanner.matthewwen.todoplanner.developer.developerActivities;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class DeveloperEventCreateActivity extends AppCompatActivity
    implements DateDialog.DatePopUp, TimeDialog.TimePopUP {

    //private static final String TAG = DeveloperEventCreateActivity.class.getSimpleName();

    private EditText nameET;
    private EditText noteET;
    private Switch stationS;

    private static final int START_TIME = 1;
    private static final int END_TIME = 2;

    //The Key
    private static final String date_picker_key = "DatePicker";
    private static final String time_picker_key = "TimePicker";

    private DateDialog dateDialog;
    private TimeDialog timeDialog;

    private int yearStart;
    private int monthStart;
    private int dayOfMonthStart;
    private int hourStart;
    private int minuteStart;

    private int yearEnd;
    private int monthEnd;
    private int dayOfMonthEnd;
    private int hourEnd;
    private int minuteEnd;

    //The Start/End Time
    private long startDate;
    private long endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_event_create);

        //Initialize all the views
        nameET = findViewById(R.id.developer_create_event_name_et);
        noteET = findViewById(R.id.developer_create_event_note_et);
        stationS = findViewById(R.id.developer_create_event_stationary_s);

        //set all the values
        if (yearStart == 0 || yearEnd == 0){
            Calendar temp = Calendar.getInstance();
            yearStart = temp.get(Calendar.YEAR);
            monthStart = temp.get(Calendar.MONTH);
            dayOfMonthStart = temp.get(Calendar.DAY_OF_MONTH);
            hourStart = temp.get(Calendar.HOUR_OF_DAY);
            minuteStart = temp.get(Calendar.MINUTE);
            yearEnd = yearStart;
            monthEnd = monthStart;
            dayOfMonthEnd = dayOfMonthStart;
            hourEnd = hourStart;
            minuteEnd = minuteStart;
            setDate(START_TIME, DataMethods.roundNearestMinute(temp.getTimeInMillis()));
            setDate(END_TIME, DataMethods.roundNearestMinute(temp.getTimeInMillis()));
        }


        FloatingActionButton fab = findViewById(R.id.developer_create_event_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
                DeveloperEventCreateActivity.super.onBackPressed();
            }
        });


        TextView startDateTV = findViewById(R.id.developer_create_event_start_date_tv);
        startDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog = new DateDialog(v, START_TIME,DeveloperEventCreateActivity.this,
                        yearStart, monthStart, dayOfMonthStart, hourStart, minuteStart);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dateDialog.show(ft, date_picker_key);
            }
        });

        TextView startTimeTV = findViewById(R.id.developer_create_event_start_time_tv);
        startTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDialog = new TimeDialog(v, START_TIME,DeveloperEventCreateActivity.this,
                        yearStart, monthStart, dayOfMonthStart, hourStart, minuteStart);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                timeDialog.show(ft, time_picker_key);
            }
        });

        TextView endDateTV = findViewById(R.id.developer_create_event_end_date_tv);
        endDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog = new DateDialog(v, END_TIME,DeveloperEventCreateActivity.this,
                        yearStart, monthStart, dayOfMonthStart, hourStart, minuteStart);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dateDialog.show(ft, date_picker_key);
            }
        });

        TextView endTimeTV = findViewById(R.id.developer_create_event_end_time_tv);
        endTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDialog = new TimeDialog(v, END_TIME,DeveloperEventCreateActivity.this,
                        yearEnd, monthEnd, dayOfMonthEnd, hourEnd, minuteEnd);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                timeDialog.show(ft, time_picker_key);
            }
        });

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        startDateTV.setText(dateFormat.format(new Date(startDate)));
        endDateTV.setText(dateFormat.format(new Date(endDate)));
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm aa");
        endTimeTV.setText(timeFormat.format(new Date(startDate)));
        startTimeTV.setText(timeFormat.format(new Date(endDate)));

        //The Dialog
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
    public void setTime(int type, long time) {
        switch (type){
            case START_TIME: startDate = time; break;
            case END_TIME: endDate = time; break;
        }
    }

    @Override
    public void setDate(int type, long time) {
        switch (type){
            case START_TIME: startDate = time; break;
            case END_TIME: endDate = time; break;
        }
    }


}
