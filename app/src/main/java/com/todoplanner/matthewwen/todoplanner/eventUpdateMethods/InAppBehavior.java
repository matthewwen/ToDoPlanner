package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods;

import android.content.Context;
import android.util.Log;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.objects.Event;
import java.util.ArrayList;
import java.util.Calendar;

public class InAppBehavior {

    public static final String TAG = InAppBehavior.class.getSimpleName();

    private static final int CHANGE_NOTHING = 0;
    private static final int DELAY_EVERY_EVENT = 1;
    private static final int FORWARD_EVERY_EVENT = 2;
    private static final int PROPORTION_DELAY = 3;
    private static final int PROPORTION_FORWARD = 4;
    private static final int CREATE_NEXT_ALARM_SERVICE = 5;

    public static void userPressedFinished(Context context){
        ArrayList<Event> allEvents = CommonBehavior.getEvents(context);  //First one is the finished one, Second one is the next event.
        if (allEvents == null){
            Log.v(TAG, "All Events are null");
            return;
        }
        Log.v(TAG, "This the All Events Size: "+ allEvents.size());
        Event finished = allEvents.remove(0);
        int type = getType(finished, allEvents);
        Log.v(TAG, "This is the Type: "+ type);
        switch (type){
            case CHANGE_NOTHING: CommonBehavior.changeNothing(context, allEvents.get(0), finished);
                break;
            case DELAY_EVERY_EVENT: CommonBehavior.delayEveryEvent(context, allEvents, finished);
                break;
            case FORWARD_EVERY_EVENT: CommonBehavior.forwardEveryEvent(context, allEvents, finished);
                break;
            case PROPORTION_DELAY:
                break;
            case PROPORTION_FORWARD:
                break;
            case CREATE_NEXT_ALARM_SERVICE:
                break;
        }
    }

    private static int getType(Event finished, ArrayList<Event> upComingEvents){
        long endTime = DataMethods.roundNearestMinute(finished.getEventEnd());
        long nextEventStartTime = DataMethods.roundNearestMinute(upComingEvents.get(0).getEventStart());
        long currentTime = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis());
        int arraySize = upComingEvents.size();
        boolean staticEvent = upComingEvents.get(arraySize - 1).isStatic();

        if ((endTime > currentTime) &&
                        (endTime < nextEventStartTime)){
            return getTypeForward(staticEvent, upComingEvents, arraySize);
        }

        if ((currentTime < nextEventStartTime) &&
                        (currentTime > endTime)){
            return CREATE_NEXT_ALARM_SERVICE;
        }

        if ((currentTime > nextEventStartTime) &&
                        (currentTime > endTime)){
            return getTypeDelay(staticEvent, currentTime, endTime, upComingEvents, arraySize);
        }

        if (endTime == currentTime){
            return CHANGE_NOTHING;
        }

        if (endTime < currentTime){ //Finished Late
            return getTypeDelay(staticEvent, currentTime, endTime, upComingEvents, arraySize);
        }else { //Finished Early
            //endTime > current Time
            return getTypeForward(staticEvent, upComingEvents, arraySize);

        }
    }

    private static int getTypeForward(boolean staticEvent,
                                      ArrayList<Event> upComingEvents, int arraySize){
        if (staticEvent){
            long secondToLast = DataMethods.roundNearestMinute(upComingEvents.get(arraySize - 2).getEventEnd());
            long startOfStatic = DataMethods.roundNearestMinute(upComingEvents.get(arraySize - 1).getEventStart());
            if (startOfStatic == secondToLast){
                return PROPORTION_FORWARD;
            }else {
                return FORWARD_EVERY_EVENT;
            }
        }else {
            return FORWARD_EVERY_EVENT;
        }
    }

    private static int getTypeDelay(boolean staticEvent, long currentTime, long endTime,
                                    ArrayList<Event> upComingEvents, int arraySize){
        if (staticEvent){
            long secondToLast = DataMethods.roundNearestMinute(upComingEvents.get(arraySize - 2).getEventEnd());
            long startOfStatic = DataMethods.roundNearestMinute(upComingEvents.get(arraySize - 1).getEventStart());
            if (startOfStatic == secondToLast){
                return PROPORTION_DELAY;
            }else {
                long difference = currentTime - endTime;
                long addedDifference = ((arraySize - 1) * difference) - CommonBehavior.getAmountBufferTime(upComingEvents);
                if (secondToLast + addedDifference > startOfStatic){
                    return PROPORTION_DELAY;
                }else {
                    return DELAY_EVERY_EVENT;
                }
            }
        }else {
            return DELAY_EVERY_EVENT;
        }
    }


}