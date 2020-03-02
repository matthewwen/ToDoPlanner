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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.matthewwen.todoplanner.ApiRequest;
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

    public SectionAdapter(Context context, ArrayList<Section> sectionList, TaskAdapter taskAdapter, DrawerLayout drawerLayout) {
        this.sectionList = sectionList;
        this.taskAdapter = taskAdapter;
        this.context = context;
        this.drawerLayout = drawerLayout;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_section, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, final int position) {
        holder.textView.setText(sectionList.get(position).name);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, ArrayList<TodoTasks>>() {
                    @Override
                    public ArrayList<TodoTasks> doInBackground(Void... voids) {
                        return get_tasks(context, sectionList.get(position).id);
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        taskAdapter.allTasks = new ArrayList<>();
                        taskAdapter.notifyDataSetChanged();
                    }

                    @Override
                    protected void onPostExecute(ArrayList<TodoTasks> Tasks) {
                        super.onPostExecute(Tasks);
                        taskAdapter.allTasks = Tasks;
                        taskAdapter.notifyDataSetChanged();
                    }
                }.execute();
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
