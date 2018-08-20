package com.todoplanner.matthewwen.todoplanner.developer;


import android.content.Context;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.alarmService.methods.SetAlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DeveloperScenarios {

    private static final String TAG = DeveloperScenarios.class.getSimpleName();

    public static void developerUpdateOnView(Context context){
        //Delete all the events
        context.getContentResolver().delete(DataContract.TodayEventEntry.EVENT_CONTENT_URI, null, null);

        //create 3 events, 2 minutes long, one in progress.
        long start  = DataMethods.getCurrentTime(context) - TimeUnit.MINUTES.toMillis(3);
        for (int i = 0; i < 3; i++){
            DataMethods.createEvent(context,
                    "Event " + (i+1),
                    "Junior Credit",
                    start,
                    start + TimeUnit.MINUTES.toMillis(2),
                    DataContract.TodayEventEntry.EVENT_NOT_STATIONARY);
            start += TimeUnit.MINUTES.toMillis(3);
        }
    }
}
