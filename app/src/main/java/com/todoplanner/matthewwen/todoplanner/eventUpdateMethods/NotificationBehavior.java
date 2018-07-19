package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationBehavior {

    private static final int CHANGE_NOTHING = 0;
    private static final int DELAY_EVERY_EVENT = 1;
    private static final int PROPORTION_DELAY = 3;
    private static final int CREATE_NEXT_ALARM_SERVICE = 5;

    public static final String TAG = NotificationBehavior.class.getSimpleName();

    public static void setUpNextEvent(Intent intent, Context context){
        ArrayList<Event> allEvents = CommonBehavior.getEvents(context);  //First one is the finished one, Second one is the next event.
        if (allEvents == null){
            Log.v(TAG, "All Events are null");
            return;
        }
        Event finished = allEvents.remove(0);
        int type = getType(finished, allEvents);
        switch (type){
            case CHANGE_NOTHING: CommonBehavior.changeNothing(context, allEvents.get(0), finished);
                break;
            case DELAY_EVERY_EVENT: CommonBehavior.delayEveryEvent(context, allEvents, finished);
                break;
            case PROPORTION_DELAY:
                break;
            case CREATE_NEXT_ALARM_SERVICE: CommonBehavior.setNextAlarmService(context, allEvents);
                break;
        }

        Uri uri = Uri.parse(intent.getAction());
        DataMethods.changeToPastEvent(context, uri);
    }

    private static int getType(Event finished, ArrayList<Event> upComingEvents){
        long currentTime = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis());
        int arraySize = upComingEvents.size();
        Event nextEvent = upComingEvents.get(0);
        Event probablyStatic = upComingEvents.get(upComingEvents.size() - 1);
        if (finished.getEventEnd() == currentTime){
            if (finished.getEventEnd() == nextEvent.getEventStart()){
                return CHANGE_NOTHING;
            }else {
                return CREATE_NEXT_ALARM_SERVICE;
            }
        }

        //Lets see if the last event is static or nothing
        if (upComingEvents.get(upComingEvents.size() - 1).isStatic()){
            //least likely thing to do is to move everything back.
            //They will probably try to put everything into proportion
            long secondToLast = DataMethods.roundNearestMinute(upComingEvents.get(arraySize - 2).getEventEnd());
            long startOfStatic = DataMethods.roundNearestMinute(upComingEvents.get(arraySize - 1).getEventStart());
            if (startOfStatic == secondToLast){
                return PROPORTION_DELAY;
            }else {
                long endTime = probablyStatic.getEventStart();
                long difference = currentTime - endTime;
                long addedDifference = ((arraySize - 1) * difference) - CommonBehavior.getAmountBufferTime(upComingEvents);
                if (secondToLast + addedDifference > startOfStatic) {
                    return PROPORTION_DELAY;
                } else {
                    return DELAY_EVERY_EVENT;
                }
            }
        }else {
            //Will definitely be moving everything back
            return DELAY_EVERY_EVENT;

        }
    }


}
