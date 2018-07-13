package com.todoplanner.matthewwen.todoplanner.adapter.eventAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase.developerEventActivities.DeveloperPastEventActivity;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

public class DeveloperEventPastAdapter extends RecyclerView.Adapter<DeveloperEventPastAdapter.PastViewHolder> {

    LayoutInflater inflater;
    ArrayList<Event> allEvents;

    public DeveloperEventPastAdapter(Context context, ArrayList<Event> allEvents){
        inflater = LayoutInflater.from(context);
        this.allEvents = allEvents;
    }

    @NonNull
    @Override
    public PastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycle_layout_developer_event_past_item, parent, false);
        return new PastViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PastViewHolder holder, int position) {
        Event event = allEvents.get(position);
        holder.idtv.setText("ID: " + Integer.toString(event.getID()));
        holder.nameTv.setText("Event Name: " + event.getEventName());
        holder.startTv.setText("Start: " + new Date(event.getEventStart()).toString());
        holder.endTv.setText("End: " + new Date(event.getEventEnd()).toString());
        holder.taskIDTv.setText("Task ID: " + Integer.toString(event.getTaskId()));
        holder.noteTv.setText("Note: " + event.getNote());
    }

    @Override
    public int getItemCount() {
        return allEvents.size();
    }

    class PastViewHolder extends RecyclerView.ViewHolder{

        TextView idtv;
        TextView nameTv;
        TextView startTv;
        TextView endTv;
        TextView taskIDTv;
        TextView noteTv;

        PastViewHolder(View itemView) {
            super(itemView);
            idtv = itemView.findViewById(R.id.developer_event_past_id_tv);
            nameTv = itemView.findViewById(R.id.developer_event_past_name_tv);
            startTv = itemView.findViewById(R.id.developer_event_past_start_tv);
            endTv = itemView.findViewById(R.id.developer_event_past_end_tv);
            taskIDTv = itemView.findViewById(R.id.developer_event_task_past_id_tv);
            noteTv = itemView.findViewById(R.id.developer_event_past_note_tv);
        }
    }

    public void clear(){
        allEvents = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setAllEvents(ArrayList<Event> allEvents){
        this.allEvents = allEvents;
        notifyDataSetChanged();
    }
}
