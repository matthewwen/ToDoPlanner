package com.matthewwen.todoplanner.rv;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.matthewwen.todoplanner.object.Section;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends ArrayAdapter<String> {

    private List<Section> sectionList;
    public long id;

    public MenuAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        id = -1;
    }

    public void setList(ArrayList<Section> sectionList) {
        clear();
        for (int i = 0; i < sectionList.size(); i++) {
            add(sectionList.get(i).name);
            id = id == -1 ? sectionList.get(i).id: id;
        }
        this.sectionList = sectionList;
        notifyDataSetChanged();
    }

    public void setId(int id){
        this.id = sectionList.get(id).id;
    }

}
