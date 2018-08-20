package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.adaption;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.alarmService.methods.SetAlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.jobServices.JobServiceMethods;
import com.todoplanner.matthewwen.todoplanner.objects.Event;
import java.util.ArrayList;
import java.util.Calendar;

public class InAppBehavior {

    public static final String TAG = InAppBehavior.class.getSimpleName();

    private static final int CHANGE_NOTHING_GO_TO_NEXT = 0;
    private static final int DELAY_EVERY_EVENT = 1;
    private static final int FORWARD_EVERY_EVENT = 2;
    private static final int PROPORTION_DELAY = 3;
    private static final int PROPORTION_FORWARD = 4;
    private static final int CREATE_NEXT_ALARM_SERVICE = 5;

    public static void userPressedFinished(Context context, Event finished){
        //Cancel Job Service
        JobServiceMethods.cancelEventJobService(context, JobServiceMethods.DELAY_AND_NOTIFY);
        //change to past event
        Uri uri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, finished.getID());
        DataMethods.changeToPastEvent(context, uri);
        Log.v(TAG, "Change to past event?");
        //cancel the end alarm service
        SetAlarmServiceMethods.cancelEndAlarmService(context);
        //get all upcoming events
        ArrayList<Event> allEvents = DataMethods.getNecessaryTodayEvents(context);  //First one is the finished one, Second one is the next finished.
        if (allEvents == null || allEvents.size() == 0){
            return;
        }
        //check for a static event
        Event lastEvent = allEvents.get(allEvents.size() - 1);
        SetAlarmServiceMethods.setStaticAlarmService(context, lastEvent);

        int type = getType(finished, allEvents);
        Log.v(TAG, "This is the Type: "+ type);
        switch (type){
            case CHANGE_NOTHING_GO_TO_NEXT: CommonBehavior.changeNothingGoToNext(context, allEvents.get(0));
                break;
            case DELAY_EVERY_EVENT: CommonBehavior.delayEveryEvent(context, allEvents);
                break;
            case FORWARD_EVERY_EVENT:
                long diff = allEvents.get(0).getEventStart() - finished.getEventEnd();
                long runOff = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis()) - diff;
                CommonBehavior.forwardEveryEvent(context, allEvents, runOff);
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
        long currentTime = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis());
        if (finished.getEventEnd() <= currentTime){
            return NotificationBehavior.getType(finished, upComingEvents, currentTime);
        }

        //for all these, finished time will be be after the current time, (meaning that you finished earlier than expected
        Event probStatic = upComingEvents.get(upComingEvents.size() - 1);
        boolean isStatic = probStatic.isStatic();
        if (isStatic){
            if (upComingEvents.size() == 1){
                return CREATE_NEXT_ALARM_SERVICE;
            }
            return PROPORTION_FORWARD;
        }
        return FORWARD_EVERY_EVENT;
    }
}