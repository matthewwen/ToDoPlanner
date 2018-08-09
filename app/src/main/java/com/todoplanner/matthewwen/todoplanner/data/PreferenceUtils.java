package com.todoplanner.matthewwen.todoplanner.data;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class PreferenceUtils {

    //The Key Values
    public static final String SHOW_END_NOTIFICATION_TAG = "show-end-notify-tag";

    //The Tag
    //private static final String TAG = PreferenceUtils.class.getSimpleName();

    //whenever you are changing the value of a preference you have to do this
    //synchronized public static void method()
    synchronized public static void resetNotifyEndJobService(Context context){
        setShowNotifyEndJobService(context, true);
    }

    //set if the notification should be displayed or not
    synchronized public static void setShowNotifyEndJobService(Context context, boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHOW_END_NOTIFICATION_TAG, value);
        editor.apply();
    }

    //get what type of notify job service (display or not display notification)
    synchronized public static boolean getNotifyEndJobService(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SHOW_END_NOTIFICATION_TAG, true);
    }





}
