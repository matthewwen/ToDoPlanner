package com.matthewwen.todoplanner.ui.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matthewwen.todoplanner.ApiRequest;
import com.matthewwen.todoplanner.R;
import com.matthewwen.todoplanner.object.Section;
import com.matthewwen.todoplanner.rv.SectionAdapter;
import com.matthewwen.todoplanner.rv.TaskAdapter;

import java.util.ArrayList;

public class TasksFragment extends Fragment {

    private TasksViewModel tasksViewModel;

    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tasksViewModel = ViewModelProviders.of(this).get(TasksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        final DrawerLayout drawerLayout = root.findViewById(R.id.drawer_layout);

        // List of Sections
        RecyclerView rv = root.findViewById(R.id.section_rv);
        RecyclerView taskRv = root.findViewById(R.id.task_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        taskRv.setLayoutManager(new LinearLayoutManager(getContext()));
        final SectionAdapter adapter = new SectionAdapter(getContext(), new ArrayList<Section>(), new TaskAdapter(), drawerLayout, toolbar);
        rv.setAdapter(adapter);
        taskRv.setAdapter(adapter.taskAdapter);

        new AsyncTask<Void, Void, ArrayList<Section>>() {
            @Override
            protected ArrayList<Section> doInBackground(Void... voids) {
                return ApiRequest.get_section(getContext());
            }
            @Override
            protected void onPostExecute(ArrayList<Section> Sections) {
                super.onPostExecute(Sections);
                adapter.sectionList = Sections;
                adapter.notifyDataSetChanged();
                adapter.updateData(0);
            }
        }.execute();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        tasksViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }
}
