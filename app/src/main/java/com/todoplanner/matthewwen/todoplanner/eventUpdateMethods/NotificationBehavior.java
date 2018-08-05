package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.alarmService.AlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationBehavior {

    private static final int CHANGE_NOTHING_GO_TO_NEXT = 0;
    private static final int DELAY_EVERY_EVENT = 1;
    private static final int PROPORTION_DELAY = 3;
    private static final int CREATE_NEXT_ALARM_SERVICE = 5;
    private static final int NNEVENT_STATIC_NEED_END_ALARM = 6;
    private static final int NNEVENT_STATIC_NO_END_ALARM = 7;

    public static final String TAG = NotificationBehavior.class.getSimpleName();

    public static void setUpNextEvent(Intent intent, Context context){
        //Cancel Job Service
        CommonBehavior.cancelJobService(context);

        //First one is the finished one, Second one is the next event.
        ArrayList<Event> allEvents = CommonBehavior.getEvents(context);
        if (allEvents == null){
            Log.v(TAG, "All Events are null");
            CommonBehavior.cancelAnyCurrentNotification(context);
            return;
        }

        Event lastEvent = allEvents.get(allEvents.size() - 1);
        if (lastEvent.isStatic()){
            if (lastEvent.getAlarmSet() ==
                    DataContract.TodayEventEntry.ALARM_NOT_SET){
                AlarmServiceMethods.setStaticAlarmStartEvent(context, lastEvent);
            }
        }

        long currentTime = DataMethods.roundNearestMinute(Calendar.getInstance().getTimeInMillis());

        Event finished = allEvents.remove(0);
        int type = getType(finished, allEvents, currentTime);
        switch (type){
            case CHANGE_NOTHING_GO_TO_NEXT: CommonBehavior.changeNothingGoToNext(context, allEvents.get(0), finished);
                break;
            case DELAY_EVERY_EVENT: CommonBehavior.delayEveryEvent(context, allEvents, finished);
                break;
            case PROPORTION_DELAY:
                break;
            case CREATE_NEXT_ALARM_SERVICE: CommonBehavior.setNextAlarmService(context, allEvents);
                break;
            case NNEVENT_STATIC_NEED_END_ALARM: CommonBehavior.nnEventNeedEndStatic(context, currentTime, allEvents.get(0));
                break;
            case NNEVENT_STATIC_NO_END_ALARM: CommonBehavior.nnEventNoEndStatic(context, currentTime, allEvents.get(0));
                break;
        }

        Uri uri = Uri.parse(intent.getAction());
        DataMethods.changeToPastEvent(context, uri);
    }

    public static int getType(Event finished, ArrayList<Event> upComingEvents, long currentTime){
        Event nextEvent = upComingEvents.get(0);

        //Checks if the next next event is static
        Event nnEvent = null;
        if (upComingEvents.size() > 1){
            nnEvent = upComingEvents.get(1);
        }

        boolean probablyStatic = upComingEvents.get(upComingEvents.size() - 1).isStatic();

        if (nnEvent != null && nnEvent.isStatic()){
            return getStaticType(nextEvent, nnEvent);
        }

        if (finished.getEventEnd() < currentTime){
            return getDelayType(currentTime, nextEvent, probablyStatic);
        }else {
            if (nextEvent.getEventStart() == finished.getEventEnd()){
                return CHANGE_NOTHING_GO_TO_NEXT;
            }else {
                return CREATE_NEXT_ALARM_SERVICE;
            }
        }
    }

    private static int getDelayType(long currentTime, Event next, boolean isStatic){
        if (currentTime < next.getEventStart() ){
            return CREATE_NEXT_ALARM_SERVICE;
        }

        if (currentTime == next.getEventStart()){
            return CHANGE_NOTHING_GO_TO_NEXT;
        }

        //the next event already started
        if (isStatic){
            return PROPORTION_DELAY;
        }

        return DELAY_EVERY_EVENT;
    }

    private static int getStaticType(Event nEvent, Event nnEvent){
        if (nEvent.getEventEnd() == nnEvent.getEventStart()){
            return NNEVENT_STATIC_NO_END_ALARM;
        }
        return NNEVENT_STATIC_NEED_END_ALARM;
    }


}
