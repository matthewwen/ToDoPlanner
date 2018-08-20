package com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.creationDeletion;

import android.content.ContentUris;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.todoplanner.matthewwen.todoplanner.alarmService.methods.SetAlarmServiceMethods;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.data.DataMethods;
import com.todoplanner.matthewwen.todoplanner.eventUpdateMethods.EventChangeBehavior;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;

public class DeletionMethod {

    private static final int FORWARD_EVERY_EVENT = 2;
    private static final int PROPORTION_FORWARD = 4;

    /**
     *The Method
     */
    private static void deleteEventForwardEverything(ArrayList<Event> allTodayEvents, Context context, int id){
        //go through, find the event
        boolean found = false;
        Event theEvent = null;
        while (allTodayEvents.size() > 0 && !found){
            if (allTodayEvents.get(0).getID() != id){
                allTodayEvents.remove(0);
            }else {
                theEvent = allTodayEvents.remove(0);
                found = true;
            }
        }
        //There was an issue
        if (!found) {
            Toast.makeText(context, "There was an error deleting event", Toast.LENGTH_LONG).show();
            return;
        }
        //deleting event
        DataMethods.changeToPastEvent(context, ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, id));
        //if it is the very last event
        if (allTodayEvents.size() == 0){
            return;
        }
        //What is left is all the events that need to be updated.
        long runOff;
        long currentTime = DataMethods.getCurrentTime(context);
        if (currentTime > theEvent.getEventStart()){
            runOff = allTodayEvents.get(0).getEventStart() - (theEvent.getEventEnd()-currentTime);
            EventChangeBehavior.moveEverythingForward(context, allTodayEvents, runOff);
        }else {
            runOff = allTodayEvents.get(0).getEventStart() - theEvent.getRange();
            EventChangeBehavior.moveEverythingForward(context, allTodayEvents, runOff);
        }
    }

    private static void deleteEventProportionDelay(ArrayList<Event> allTodayEvents, Context context, int id){
        long start = DataMethods.getCurrentTime(context);
        long end = -1;
        boolean found = false; int pos = -1;
        //get the oldStart, oldEnd, and pos of event that will be deleted.
        for (int i = 0; i < allTodayEvents.size() && (end == -1); i++){
            Event temp = allTodayEvents.get(i);
            if (temp.isStatic()){
                if (found){
                    end = temp.getEventEnd();
                }else {
                    start = temp.getEventStart();
                }
            }
            if (temp.getID() == id){
                found = true;
                pos = i;
            }
        }
        //delete events
        DataMethods.deleteTodayEvent(context, id);
        //move everything forward from position i to the end.
        long difference = allTodayEvents.get(pos).getRange();
        ArrayList<Event> changeEvents = new ArrayList<>();
        while (allTodayEvents.size() > pos + 1){
            changeEvents.add(allTodayEvents.remove(pos + 1));
        }
        if (changeEvents.size() == 0){
            return;
        }
        EventChangeBehavior.moveEverythingForward(context, changeEvents, difference);
        long oldEnd = changeEvents.get(changeEvents.size() - 1).getEventEnd();
        allTodayEvents.addAll(changeEvents);
        //proportion all the events and save to database
        EventChangeBehavior.moveProportion(context, allTodayEvents, start, oldEnd, start, end);
    }

    /**
     * Utilities
     */
    private static int getType(ArrayList<Event> allEvents, int id){
        boolean eventFound = false;
        for (int i = 0; i < allEvents.size(); i++){
            Event temp = allEvents.get(i);
            if (eventFound){
                //now check
                if (temp.isStatic()){
                    return PROPORTION_FORWARD;
                }
            }
            if (temp.getID() == id){
                eventFound = true;
            }
        }

        return FORWARD_EVERY_EVENT;
    }

    /**
     * The Call
     */
    public static void deleteEvent(Context context, int id){
        //get all the events
        ArrayList<Event> allTodayEvents = DataMethods.getAllTodayEvents(context);
        int type = getType(allTodayEvents, id);
        Log.v("Deletion Method", "Type: " + type);
        switch (type){
            case FORWARD_EVERY_EVENT: deleteEventForwardEverything(allTodayEvents, context, id); break;
            case PROPORTION_FORWARD: deleteEventProportionDelay(allTodayEvents, context, id);break;
        }
        SetAlarmServiceMethods.setAlarmService(context);
    }

}
