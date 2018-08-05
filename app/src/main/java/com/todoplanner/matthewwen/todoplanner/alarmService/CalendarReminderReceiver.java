package com.todoplanner.matthewwen.todoplanner.alarmService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.CommonBehavior;
import com.todoplanner.matthewwen.todoplanner.jobServices.JobServiceMethods;
import com.todoplanner.matthewwen.todoplanner.notifications.NotificationsUtils;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class CalendarReminderReceiver extends BroadcastReceiver{



    private static final String TAG = CalendarReminderReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get the Uri and the type
        String action = intent.getAction();
        Uri uri = Uri.parse(action);
        String type = intent.getStringExtra(context.getString(R.string.notification_event_start_end_event_key));

        //get all the events
        ArrayList<Event> upcomingEvent = CommonBehavior.getEvents(context);

        //get the event
        Event event = DataMethods.getTodayEvent(upcomingEvent, uri);

        if (event == null) {
            return;
        }

        boolean setEnd = true;
        if (type.equals(NotificationsUtils.EVENT_REMINDER_START) && event.isStatic()){
            //move all the before events to respected area
            DataMethods.updateFromNewStatic(context, upcomingEvent, event.getID());
            //cancel all alarm services
            AlarmServiceMethods.cancelReminderAlarmService(context);
            //set up next alarm service
            int arraySize = upcomingEvent.size();
            Event probStaticEvent = upcomingEvent.get(arraySize - 1);
            if (!probStaticEvent.isAlarmSet() && probStaticEvent.isStatic()){
                AlarmServiceMethods.setStaticAlarmStartEvent(context, probStaticEvent);
            }
            //set up set end
            if (upcomingEvent.size() == 2){
                setEnd = !(upcomingEvent.get(0).getEventEnd() == upcomingEvent.get(1).getEventStart());
            }
        }

        if (type.equals(NotificationsUtils.EVENT_REMINDER_START)) {
            //make it in progress
            event.setInProgress();

            //put it in the database
            DataMethods.updateTodayEvent(context, event);

            //making the next alarm service
            if (setEnd) {AlarmServiceMethods.setAlarmEventEnd(context, event);}

            //cancel all job service
            JobServiceMethods.cancelEventJobService(context, JobServiceMethods.DELAY_AND_NOTIFY);
        }else {
            Log.v(TAG, "Job Service should be created");
            JobServiceMethods.automatedDelayEventJobService(context);
        }

        Log.v(TAG, "Notification is displayed?");
        //display notification
        NotificationsUtils.displayCalendarNotification(context, event, type);
    }


}
