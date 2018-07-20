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
import com.todoplanner.matthewwen.todoplanner.objects.Event;

import java.util.ArrayList;
import java.util.Date;

public class DeveloperEventPendingAdapter extends
        RecyclerView.Adapter<DeveloperEventPendingAdapter.PendingViewHolder>{

    private ArrayList<Event> allEvents;
    private LayoutInflater inflater;

    public DeveloperEventPendingAdapter(Context context, ArrayList<Event> allEvents){
        this.allEvents = allEvents;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycle_layout_developer_event_pending_item, parent, false);
        return new PendingViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PendingViewHolder holder, int position) {
        Event event = allEvents.get(position);
        holder.idtv.setText("ID: " + Integer.toString(event.getID()));
        holder.nameTv.setText("Event Name: " + event.getEventName());
        holder.startTv.setText("Start: " + new Date(event.getEventStart()).toString());
        holder.endTv.setText("End: " + new Date(event.getEventEnd()).toString());
        holder.taskIDTv.setText("Task ID: " + Integer.toString(event.getTaskId()));
        holder.noteTv.setText("Note: " + event.getNote());
        holder.stationTv.setText("Station: " + event.getStaticInt());
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

    class PendingViewHolder extends RecyclerView.ViewHolder{

        TextView idtv;
        TextView nameTv;
        TextView startTv;
        TextView endTv;
        TextView taskIDTv;
        TextView noteTv;
        TextView stationTv;

        PendingViewHolder(View itemView) {
            super(itemView);
            idtv = itemView.findViewById(R.id.developer_event_pending_id_tv);
            nameTv = itemView.findViewById(R.id.developer_event_pending_name_tv);
            startTv = itemView.findViewById(R.id.developer_event_pending_start_tv);
            endTv = itemView.findViewById(R.id.developer_event_pending_end_tv);
            taskIDTv = itemView.findViewById(R.id.developer_event_task_pending_id_tv);
            noteTv = itemView.findViewById(R.id.developer_event_pending_note_tv);
            stationTv = itemView.findViewById(R.id.developer_event_pending_item_stationary);
        }
    }
}
