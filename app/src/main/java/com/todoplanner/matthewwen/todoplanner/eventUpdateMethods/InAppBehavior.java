package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods;

import android.content.Context;
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

    public static void userPressedFinished(Context context){
        //Cancel Job Service
        JobServiceMethods.cancelEventJobService(context, JobServiceMethods.DELAY_AND_NOTIFY);

        ArrayList<Event> allEvents = DataMethods.getNecessaryTodayEvents(context);  //First one is the finished one, Second one is the next event.
        if (allEvents == null){
            return;
        }

        Event lastEvent = allEvents.get(allEvents.size() - 1);
        if (lastEvent.isStatic()){
            if (lastEvent.getAlarmSet() ==
                    DataContract.TodayEventEntry.ALARM_NOT_SET){
                SetAlarmServiceMethods.setStaticAlarmService(context, lastEvent);
            }
        }

        Log.v(TAG, "This the All Events Size: "+ allEvents.size());
        Event finished = allEvents.remove(0);
        int type = getType(finished, allEvents);
        Log.v(TAG, "This is the Type: "+ type);
        switch (type){
            case CHANGE_NOTHING_GO_TO_NEXT: CommonBehavior.changeNothingGoToNext(context, allEvents.get(0), finished);
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