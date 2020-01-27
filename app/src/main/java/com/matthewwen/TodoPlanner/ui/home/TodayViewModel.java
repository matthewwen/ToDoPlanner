package com.matthewwen.TodoPlanner.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.matthewwen.TodoPlanner.adapter.TaskAdapter;
import com.matthewwen.TodoPlanner.obj.Task;

import java.util.ArrayList;

public class TodayViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<TaskAdapter> adapter;

    @SuppressLint("Assert")
    public TodayViewModel() {
        mText = new MutableLiveData<>();
        assert false;
        ArrayList<Task> temp_list = new ArrayList<>();
        temp_list.add(new Task("Task 1"));
        temp_list.add(new Task("Task 2"));
        temp_list.add(new Task("Task 3"));
        temp_list.add(new Task("Task 4"));
        temp_list.add(new Task("Task 5"));
        temp_list.add(new Task("Task 6"));
        temp_list.add(new Task("Task 7"));
        temp_list.add(new Task("Task 8"));
        temp_list.add(new Task("Task 9"));
        temp_list.add(new Task("Task 10"));
        TaskAdapter temp_adapter = new TaskAdapter(temp_list);
        adapter = new MutableLiveData<>(); 
        adapter.setValue(temp_adapter);
    }

    public LiveData<TaskAdapter> getListTask() {
        return adapter;
    }
}