package com.matthewwen.todoplanner.rv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.matthewwen.todoplanner.PhoneDatabase;
import com.matthewwen.todoplanner.R;
import com.matthewwen.todoplanner.object.Section;
import com.matthewwen.todoplanner.object.TodoTasks;

import java.util.ArrayList;

import static com.matthewwen.todoplanner.ApiRequest.get_tasks;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {

    public ArrayList<Section> sectionList;
    public TaskAdapter taskAdapter;
    public Context context;
    public DrawerLayout drawerLayout;
    public Toolbar toolbar;

    public SectionAdapter(Context context, ArrayList<Section> sectionList, TaskAdapter taskAdapter, DrawerLayout drawerLayout, Toolbar toolbar) {
        this.sectionList = sectionList;
        this.taskAdapter = taskAdapter;
        this.context = context;
        this.drawerLayout = drawerLayout;
        this.toolbar = toolbar;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_section, parent, false);
        return new SectionViewHolder(view);
    }

    @SuppressLint("StaticFieldLeak")
    public void updateData(final int position) {
        if (sectionList.size() > 0) {
            new AsyncTask<Void, Void, ArrayList<TodoTasks>>() {
                @Override
                public ArrayList<TodoTasks> doInBackground(Void... voids) {
                    return get_tasks(context, sectionList.get(position).id);
                }

                @SuppressLint("RtlHardcoded")
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Section section = sectionList.get(position);

                    // Read from database
                    section.allTask = PhoneDatabase.getTodoTask(context, section.id);
                    Log.v("MWEN", "list size: " + section.allTask.size());

                    drawerLayout.closeDrawer(Gravity.LEFT);
                    toolbar.setTitle(sectionList.get(position).name);
                    taskAdapter.allTasks = section.allTask;
                    taskAdapter.notifyDataSetChanged();

                    // memory and data change
                    sectionList.set(position, section);
                }

                @SuppressLint("Assert")
                @Override
                protected void onPostExecute(ArrayList<TodoTasks> Tasks) {
                    super.onPostExecute(Tasks);

                    if (Tasks != null) {
                        // memory and database change.
                        taskAdapter.allTasks = Tasks;
                        taskAdapter.notifyDataSetChanged();
                        PhoneDatabase.deleteSectionTodo(sectionList.get(position).id);
                        for (int i = 0; i < Tasks.size(); i++) {
                            PhoneDatabase.insertTask(context, Tasks.get(i));
                        }
                    }
                }
            }.execute();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, final int position) {
        holder.textView.setText(sectionList.get(position).name);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                updateData(position);
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    static class SectionViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        View view;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.name_tv);
            view = itemView;
        }
    }
}
