package com.matthewwen.todoplanner.rv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matthewwen.todoplanner.R;
import com.matthewwen.todoplanner.object.TodoTasks;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public ArrayList<TodoTasks> allTasks;

    public TaskAdapter(){
        allTasks = new ArrayList<>();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_todotask, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.textView.setText(allTasks.get(position).name);
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.name_tv);
        }
    }
}
