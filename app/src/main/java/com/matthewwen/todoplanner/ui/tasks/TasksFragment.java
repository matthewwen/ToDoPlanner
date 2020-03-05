package com.matthewwen.todoplanner.ui.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matthewwen.MyApplication;
import com.matthewwen.todoplanner.ApiRequest;
import com.matthewwen.todoplanner.PhoneDatabase;
import com.matthewwen.todoplanner.R;
import com.matthewwen.todoplanner.object.Section;
import com.matthewwen.todoplanner.rv.SectionAdapter;

import java.util.ArrayList;

public class TasksFragment extends Fragment {

    private TasksViewModel tasksViewModel;
    private AsyncTask<Void, Void, ArrayList<Section>> asyncTask = null;
    private long loadedId = -1;

    @Override
    public void onStop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PhoneDatabase.tearDown();
                if (asyncTask != null) {
                    asyncTask.cancel(true);
                    asyncTask = null;
                }
            }
        }).start();
        super.onStop();
    }

    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        tasksViewModel = ViewModelProviders.of(this).get(TasksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        final DrawerLayout drawerLayout = root.findViewById(R.id.drawer_layout);

        // List of Sections
        RecyclerView rv = root.findViewById(R.id.section_rv);
        RecyclerView taskRv = root.findViewById(R.id.task_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        taskRv.setLayoutManager(new LinearLayoutManager(getContext()));

        tasksViewModel.setView(getContext(), drawerLayout, toolbar);
        final SectionAdapter adapter = tasksViewModel.adapter;
        rv.setAdapter(adapter);
        taskRv.setAdapter(adapter.taskAdapter);

        this.asyncTask = new AsyncTask<Void, Void, ArrayList<Section>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                adapter.sectionList = PhoneDatabase.getTodoSection(getContext());
                adapter.notifyDataSetChanged();
                long id = PreferenceManager.getDefaultSharedPreferences(getContext()).getLong("SectionId", -1);
                adapter.updateData(id);
                loadedId = id;
            }

            @Override
            protected ArrayList<Section> doInBackground(Void... voids) {
                return ApiRequest.get_section(getContext());
            }
            @Override
            protected void onPostExecute(ArrayList<Section> Sections) {
                super.onPostExecute(Sections);
                if (isCancelled()) {
                    return;
                }
                adapter.sectionList = Sections;
                adapter.notifyDataSetChanged();
                long id = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext()).getLong("SectionId", -1);
                if (id != loadedId) {
                    adapter.updateData(id);
                }

                for (int i = 0; i < Sections.size(); i++) {
                    PhoneDatabase.insertSection(getContext(), Sections.get(i));
                }
            }
        };
        this.asyncTask.execute();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        return root;
    }
}
