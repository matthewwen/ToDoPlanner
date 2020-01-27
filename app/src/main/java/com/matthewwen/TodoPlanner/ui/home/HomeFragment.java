package com.matthewwen.TodoPlanner.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matthewwen.TodoPlanner.R;
import com.matthewwen.TodoPlanner.obj.Task;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static String TAG = "HOME FRAGEMENT";

    private HomeViewModel   homeViewModel;
    private RecyclerView    recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        recyclerView  = root.findViewById(R.id.recycle_view_home);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                Log.v(TAG, "It Changed to Home: " + s);
            }
        });

        homeViewModel.getListTask().observe(getViewLifecycleOwner(), new Observer<ArrayList<Task>>() {
            @Override
            public void onChanged(ArrayList<Task> tasks) {

            }
        });
        return root;
    }
}