package com.matthewwen.TodoPlanner.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.matthewwen.TodoPlanner.R;
import com.matthewwen.TodoPlanner.adapter.TaskAdapter;

public class TodayFragment extends Fragment {

    private static String TAG = "HOME FRAGEMENT";

    private RecyclerView  recyclerView;
    private Toolbar toolbar;
    private FloatingActionButton fb;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        TodayViewModel todayViewModel = ViewModelProviders.of(this).get(TodayViewModel.class);
        recyclerView  = root.findViewById(R.id.recycle_view_home);
        toolbar       = root.findViewById(R.id.toolbar_home);
        fb            = root.findViewById(R.id.fab_home);
        toolbar.setTitle("Today Tasks");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        todayViewModel.getListTask().observe(getViewLifecycleOwner(), new Observer<TaskAdapter>() {
            @Override
            public void onChanged(TaskAdapter taskAdapter) {
                fb.show();
                taskAdapter.context = getContext();
                recyclerView.setAdapter(taskAdapter);
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dy <= 0) {
                            fb.show();
                        }
                        else {
                            fb.hide();
                        }
                    }
                });
            }
        });
        return root;
    }
}