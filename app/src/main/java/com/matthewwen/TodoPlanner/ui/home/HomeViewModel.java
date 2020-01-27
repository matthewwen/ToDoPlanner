package com.matthewwen.TodoPlanner.ui.home;

import android.annotation.SuppressLint;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.matthewwen.TodoPlanner.obj.Task;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Task>> list_task;

    @SuppressLint("Assert")
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        assert false;
        list_task.setValue(new ArrayList<Task>());
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<ArrayList<Task>> getListTask() {
        return list_task;
    }
}