package com.todoplanner.matthewwen.todoplanner.popUpDialog;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;


import com.todoplanner.matthewwen.todoplanner.data.DataMethods;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = DateDialog.class.getSimpleName();

    private TextView dateText;
    private int type;
    private DatePopUp mainView;

    private int hour;
    private int minute;

    private int year;
    private int month;
    private int day;

    public DateDialog(View view, int type, DatePopUp mainView, int year, int month, int day, int hour, int minute){
        dateText = (TextView) view;
        this.mainView = mainView;
        this.type = type;
        this.hour = hour;
        this.minute = minute;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        long time = DataMethods.roundNearestMinute(calendar.getTimeInMillis());
        String text = format.format(new Date(time));
        dateText.setText(text);
        mainView.setDate(type, time);
    }

    public interface DatePopUp{
        void setDate(int type, long time);
    }
}
