package com.todoplanner.matthewwen.todoplanner.popUpDialog;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Configuration;
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

    //key values
    private static final String YEAR_KEY = "year_key";
    private static final String MONTH_KEY = "month_key";
    private static final String DAY_KEY = "day_key";
    private static final String TYPE_KEY = "type_key";

    public static DateDialog newInstance(int type, int year, int month, int day){
        DateDialog dateDialog = new DateDialog();
        Bundle args = new Bundle();
        args.putInt(YEAR_KEY, year);
        args.putInt(MONTH_KEY, month);
        args.putInt(DAY_KEY, day);
        args.putInt(TYPE_KEY, type);
        dateDialog.setArguments(args);
        return dateDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = getArguments().getInt(YEAR_KEY);
        int month = getArguments().getInt(MONTH_KEY);
        int day = getArguments().getInt(DAY_KEY);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //get values
        int type = getArguments().getInt(TYPE_KEY);

        //saved instance state
        Bundle bundle = new Bundle();
        bundle.putInt(YEAR_KEY, year);
        bundle.putInt(MONTH_KEY, month);
        bundle.putInt(DAY_KEY, dayOfMonth);
        bundle.putInt(TYPE_KEY, type);
        setArguments(bundle);


        DatePopUp popUp = (DatePopUp) getActivity();
        popUp.setDate(type, year, month, dayOfMonth);
    }


    public interface DatePopUp{
        void setDate(int type, int year, int month, int dayOfMonth);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dismiss();
    }
}
