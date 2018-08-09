package com.todoplanner.matthewwen.todoplanner.developer;


import android.content.Context;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.alarmService.methods.SetAlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DeveloperScenarios {

    private static final String TAG = DeveloperScenarios.class.getSimpleName();

    public static void developerAllStaticScenario(Context context){
        //Delete all the events
        context.getContentResolver().delete(DataContract.TodayEventEntry.EVENT_CONTENT_URI, null, null);

        //events 2 minutes long, 1 minute apart.
        long start = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis())
                + TimeUnit.MINUTES.toMillis(1);
        for (int i = 0; i < 2; i++){
            DataMethods.createEvent(context,
                    "Event " + (i+1),
                    "Purdue University",
                    start,
                    start + TimeUnit.MINUTES.toMillis(1),
                    DataContract.TodayEventEntry.EVENT_STATIONARY);
            start += TimeUnit.MINUTES.toMillis(2);
        }

    }
}
