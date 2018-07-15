package com.todoplanner.matthewwen.todoplanner.adapter.eventAdapter;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.developerActivities.DeveloperEventActivity;
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Date;

public class DeveloperEventTodayAdapter extends RecyclerView.Adapter<DeveloperEventTodayAdapter.ViewHolder> {

    private ArrayList<Event> allEvents;
    private LayoutInflater inflater;
    private Context context;

    public DeveloperEventTodayAdapter(Context context, ArrayList<Event> allEvents){
        this.allEvents = allEvents;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycle_layout_developer_event_today_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Event event = allEvents.get(position);
        holder.idtv.setText("ID: " + Integer.toString(event.getID()));
        holder.nameTv.setText("Event Name: " + event.getEventName());
        holder.startTv.setText("Start: " + new Date(event.getEventStart()).toString());
        holder.endTv.setText("End: " + new Date(event.getEventEnd()).toString());
        holder.taskIDTv.setText("Task ID: " + Integer.toString(event.getTaskId()));
        holder.noteTv.setText("Note: " + event.getNote());
        holder.inProgTv.setText("In Progress ID: " + Integer.toString(event.getTheProgress()));
        holder.stationTv.setText("Stationary: " + Integer.toString(event.getStaticInt()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = ContentUris.withAppendedId(DataContract.TodayEventEntry.EVENT_CONTENT_URI, event.getID());
                Intent intent = new Intent(context, DeveloperEventActivity.class);
                intent.setAction(uri.toString());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return allEvents.size();
    }

    public void setAllEvents(ArrayList<Event> allEvents){
        this.allEvents = allEvents;
        notifyDataSetChanged();
    }

    public void clear(){
        allEvents = new ArrayList<>();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView idtv;
        TextView nameTv;
        TextView startTv;
        TextView endTv;
        TextView taskIDTv;
        TextView noteTv;
        TextView inProgTv;
        TextView stationTv;
        View itemView;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            idtv = itemView.findViewById(R.id.developer_event_today_item_id_tv);
            nameTv = itemView.findViewById(R.id.developer_event_today_item_name_tv);
            startTv = itemView.findViewById(R.id.developer_event_today_item_start_tv);
            endTv = itemView.findViewById(R.id.developer_event_today_item_end_tv);
            taskIDTv = itemView.findViewById(R.id.developer_event_task_today_item_id_tv);
            noteTv = itemView.findViewById(R.id.developer_event_today_item_note_tv);
            inProgTv = itemView.findViewById(R.id.developer_event_today_item_in_progress);
            stationTv = itemView.findViewById(R.id.developer_event_today_item_stationary);
        }
    }
}
