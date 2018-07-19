package com.todoplanner.matthewwen.todoplanner.developer;


import android.content.Context;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.alarmService.AlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.data.PreferenceUtils;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DeveloperScenarios {

    private static final String TAG = DeveloperScenarios.class.getSimpleName();

    public static void developerScenarioOne(Context context){
        Log.v(TAG, "Event Created");

        //Delete all Events
        context.getContentResolver().delete(DataContract.TodayEventEntry.EVENT_CONTENT_URI,
                null, null);

        ArrayList<Long> allTimes = new ArrayList<>();
        long startingValue1 =  new Date().getTime();
        for (int i = 0; i < 21; i++){
            startingValue1 +=  TimeUnit.MINUTES.toMillis(2);
            allTimes.add(startingValue1);
        }

        for (int i = 1; i <= 20; i++){
            DataMethods.createEvent(context,"Event " + i, "Tory and Abed are in Lava",
                    allTimes.get(i-1), allTimes.get(i), DataContract.TodayEventEntry.EVENT_NOT_STATIONARY);
        }

        AlarmServiceMethods.setAlarmNextEvent(context);
    }
}
