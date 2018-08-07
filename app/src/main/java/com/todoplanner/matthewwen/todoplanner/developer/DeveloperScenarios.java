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

    public static void developerTwentyEventScenarioOne(Context context){
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

    }

    public static void developerForwardConnectedEventScenarioTwo(Context context){
        Log.v(TAG, "Forward Event Created");

        //Delete all the events
        context.getContentResolver().delete(DataContract.TodayEventEntry.EVENT_CONTENT_URI, null, null);

        ArrayList<Long> allTimes = new ArrayList<>();
        long startValue = DataMethods.roundNearestMinute(
                Calendar.getInstance().getTimeInMillis() + TimeUnit.MINUTES.toMillis(1));
        for (int i = 0; i < 21; i++){
            startValue += TimeUnit.MINUTES.toMillis(5);
            allTimes.add(startValue);
        }

        for (int i = 1; i <= 20; i++){
            DataMethods.createEvent(context,"Event " + i, "Tory and Abed in the Morning",
                    allTimes.get(i-1), allTimes.get(i), DataContract.TodayEventEntry.EVENT_NOT_STATIONARY);
        }

        SetAlarmServiceMethods.setAlarmService(context);
    }

    public static void developerJobServiceScenario(Context context){
        //Delete all the events
        context.getContentResolver().delete(DataContract.TodayEventEntry.EVENT_CONTENT_URI, null, null);

        //create events 1 hour long and 3 hours apart.
        ArrayList<Long> timeArray = new ArrayList<>();
        long current = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis());
        current += TimeUnit.HOURS.toMillis(1);
        for (int i = 0; i < 10; i++){
            timeArray.add(current);
            current += TimeUnit.HOURS.toMillis(3);
        }
        for (int i = 0; i < timeArray.size(); i++){
            DataMethods.createEvent(context,
                    "Event " + i,
                    "Purdue University",
                    timeArray.get(i),
                    timeArray.get(i) + TimeUnit.HOURS.toMillis(1),
                    DataContract.TodayEventEntry.EVENT_STATIONARY);
        }
    }

    public static void developerAllStaticScenario(Context context){
        //Delete all the events
        context.getContentResolver().delete(DataContract.TodayEventEntry.EVENT_CONTENT_URI, null, null);

        //events 2 minutes long, 1 minute apart.
        long start = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis())
                + TimeUnit.MINUTES.toMillis(1);
        for (int i = 0; i < 20; i++){
            DataMethods.createEvent(context,
                    "Event " + (i+1),
                    "Purdue University",
                    start,
                    start + TimeUnit.MINUTES.toMillis(2),
                    DataContract.TodayEventEntry.EVENT_STATIONARY);
            start += TimeUnit.MINUTES.toMillis(3);
        }

    }
}
