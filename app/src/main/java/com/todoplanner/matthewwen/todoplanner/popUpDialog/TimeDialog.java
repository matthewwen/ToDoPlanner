package com.todoplanner.matthewwen.todoplanner.popUpDialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SuppressLint("ValidFragment")
public class TimeDialog extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener{

    private TextView textView;
    private int type;
    private TimePopUP popUP;

    private int year;
    private int month;
    private int dayOfYear;
    private int hour;
    private int minute;

    public TimeDialog(View view, int type, TimePopUP popUP, int year, int month, int dayOfYear,
                      int hour, int minute){
        textView = (TextView) view;
        this.type = type;
        this.popUP = popUP;
        this.year = year;
        this.month = month;
        this.dayOfYear = dayOfYear;
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), this,
                hour, minute, false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        hour = hourOfDay;
        this.minute = minute;
        calendar.set(year, month, dayOfYear, hourOfDay, minute);
        long time = calendar.getTimeInMillis();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm aa");
        String timeText = timeFormat.format(new Date(time));
        textView.setText(timeText);
        popUP.setTime(type, time);
    }

    public interface TimePopUP{
        void setTime(int type, long time);
    }
}
