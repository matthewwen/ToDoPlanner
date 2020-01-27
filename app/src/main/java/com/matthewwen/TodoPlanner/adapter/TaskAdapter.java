package com.matthewwen.TodoPlanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matthewwen.TodoPlanner.R;
import com.matthewwen.TodoPlanner.obj.Task;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context context;
    ArrayList<Task> items;
    public TaskAdapter(ArrayList<Task> items) {
        this.context = null;
        this.items   = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.viewmodel_task, parent, false);
        return new Item(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item i = (Item) holder;
        i.taskName.setText(items.get(position).name);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        View itemView;
        TextView taskName;
        Item(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            taskName = itemView.findViewById(R.id.textview_task_name);
        }
    }
}
