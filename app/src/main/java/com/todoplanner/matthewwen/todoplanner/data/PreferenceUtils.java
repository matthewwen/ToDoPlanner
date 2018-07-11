package com.todoplanner.matthewwen.todoplanner.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.R;

public class PreferenceUtils {

    //The Key Values
    public static final String KEY_DEVELOPER_JOB_SERVICE =  "developer-job-service";
    public static final String KEY_DEVELOPER_EVENT_REMINDER = "developer-even-reminder";

    //The Tag
    private static final String TAG = PreferenceUtils.class.getSimpleName();

    /**
     * This is for the Job service that adds a event to your database.
     */
    synchronized public static void setEventDeveloperJobService(Context context, Boolean doIt){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_DEVELOPER_JOB_SERVICE, doIt);
        editor.apply();
    }

    public static boolean getEventDeveloperJobService(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(KEY_DEVELOPER_JOB_SERVICE, context.getResources().getBoolean(R.bool.developer_default_job_service));
    }

    synchronized public static void changeEventDeveloperJobService(Context context){
        boolean b = getEventDeveloperJobService(context);
        setEventDeveloperJobService(context, !b);
    }

    /**
     * This is for the reminder to show up
     */
    synchronized public static void setReminderDeveloperJobService(Context context, Boolean doIt){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_DEVELOPER_EVENT_REMINDER, doIt);
        editor.apply();
    }

    public static boolean getReminderDeveloperJobService(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(KEY_DEVELOPER_EVENT_REMINDER, context.getResources().getBoolean(R.bool.developer_default_event_reminder));
    }

    synchronized public static void changeReminderDeveloperJobService(Context context){
        boolean b = getReminderDeveloperJobService(context);
        Log.v(TAG, "We are in the Preference method: " + b + " -> convert to: " + !b);
        setReminderDeveloperJobService(context, !b);
    }



}
