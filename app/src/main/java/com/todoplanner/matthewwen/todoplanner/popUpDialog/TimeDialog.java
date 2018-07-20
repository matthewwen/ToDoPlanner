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


    //key values
    private static final String HOUR_KEY = "hour-key";
    private static final String MINUTE_KEY = "minute-key";
    private static final String TYPE_KEY = "type-key";

    public static TimeDialog newInstance(int type, int hour, int minute){
        TimeDialog dialog = new TimeDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(HOUR_KEY, hour);
        bundle.putInt(MINUTE_KEY, minute);
        bundle.putInt(TYPE_KEY, type);
        dialog.setArguments(bundle);
        return dialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour = getArguments().getInt(HOUR_KEY);
        int minute = getArguments().getInt(MINUTE_KEY);

        return new TimePickerDialog(getActivity(), this,
                hour, minute, false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int type = getArguments().getInt(TYPE_KEY);

        Bundle bundle = new Bundle();
        bundle.putInt(HOUR_KEY, hourOfDay);
        bundle.putInt(MINUTE_KEY, minute);
        setArguments(bundle);

        TimePopUP popUp = (TimePopUP) getActivity();
        popUp.setTime(type, hourOfDay, minute);
    }

    public interface TimePopUP{
        void setTime(int type, int hour, int minute);
    }

}
