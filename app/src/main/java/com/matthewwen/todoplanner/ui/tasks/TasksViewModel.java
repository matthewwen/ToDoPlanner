package com.matthewwen.todoplanner.ui.tasks;

import android.content.Context;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.matthewwen.todoplanner.object.Section;
import com.matthewwen.todoplanner.rv.SectionAdapter;
import com.matthewwen.todoplanner.rv.TaskAdapter;

import java.util.ArrayList;

public class TasksViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    SectionAdapter adapter;


    public TasksViewModel() {
        mText = new MutableLiveData<>();
        this.adapter = new SectionAdapter(null, new ArrayList<Section>(), new TaskAdapter(), null, null);

    }

    public void setView(Context context, DrawerLayout drawerLayout, Toolbar toolbar) {
        this.adapter.context = context;
        this.adapter.drawerLayout = drawerLayout;
        this.adapter.toolbar = toolbar;
        this.adapter.taskAdapter.context = context;
    }


    public LiveData<String> getText() {
        return mText;
    }

}