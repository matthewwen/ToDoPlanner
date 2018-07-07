package com.todoplanner.matthewwen.todoplanner.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.objects.Task;

import java.util.ArrayList;

public class DeveloperTaskAdapter extends RecyclerView.Adapter<DeveloperTaskAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Task> allTask;

    public DeveloperTaskAdapter(Context context, ArrayList<Task> allTask){
        inflater = LayoutInflater.from(context);
        this.allTask = allTask;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycle_layout_developer_task_item, null, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task temp = allTask.get(position);

        int id = temp.getID();
        String taskName = temp.getTaskName();
        long dueDate = temp.getDevDueDate();
        int subTask = temp.devSubTask();
        int parent = temp.getParent();

        holder.idTV.setText("ID: " + Integer.toString(id));
        holder.nameTV.setText("Task Name: " + taskName);
        holder.dateTV.setText("Due Date: " + Long.toString(dueDate));
        holder.subTV.setText("Sub Task: " + Integer.toString(subTask));
        holder.parentTV.setText("Parent: " + Integer.toString(parent));

    }

    @Override
    public int getItemCount() {
        return allTask.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView idTV;
        TextView nameTV;
        TextView dateTV;
        TextView subTV;
        TextView parentTV;

        ViewHolder(View itemView) {
            super(itemView);
            idTV = itemView.findViewById(R.id.developer_task_id_tv);
            nameTV = itemView.findViewById(R.id.developer_task_task_name_tv);
            dateTV = itemView.findViewById(R.id.developer_task_due_date_tv);
            subTV = itemView.findViewById(R.id.developer_task_sub_task_tv);
            parentTV = itemView.findViewById(R.id.developer_task_parent_task_tv);

        }
    }

    public void addAll(ArrayList<Task> temp){
        allTask = temp;
        notifyDataSetChanged();
    }

    public void clear(){
        allTask = new ArrayList<>();
        notifyDataSetChanged();
    }
}
