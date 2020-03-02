package com.matthewwen.todoplanner.ui.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.matthewwen.todoplanner.ApiRequest;
import com.matthewwen.todoplanner.R;
import com.matthewwen.todoplanner.object.section;
import com.matthewwen.todoplanner.rv.SectionAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class TasksFragment extends Fragment {

    private TasksViewModel tasksViewModel;

    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tasksViewModel =
                ViewModelProviders.of(this).get(TasksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);

        Toolbar toolbar = root.findViewById(R.id.toolbar);
        final DrawerLayout drawerLayout = root.findViewById(R.id.drawer_layout);

        RecyclerView rv = root.findViewById(R.id.section_rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        final SectionAdapter adapter = new SectionAdapter(new ArrayList<section>());
        rv.setAdapter(adapter);
        new AsyncTask<Void, Void, ArrayList<section>>() {
            @Override
            protected ArrayList<section> doInBackground(Void... voids) {
                return ApiRequest.get_section(getContext());
            }
            @Override
            protected void onPostExecute(ArrayList<section> sections) {
                super.onPostExecute(sections);
                adapter.sectionList = sections;
                adapter.notifyDataSetChanged();
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
