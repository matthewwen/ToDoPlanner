package com.todoplanner.matthewwen.todoplanner.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.objects.Task;

import java.util.ArrayList;

public class TaskLoader extends AsyncTaskLoader<ArrayList<Task>>{

    public TaskLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public ArrayList<Task> loadInBackground() {
        //create array list
        ArrayList<Task> taskArrayList = new ArrayList<>();

        //Projection
        String[] projection = {
                DataContract.TaskEntry._ID,
                DataContract.TaskEntry.COLUMN_TASK_NAME,
                DataContract.TaskEntry.COLUMN_TASK_DUE_DATE,
                DataContract.TaskEntry.COLUMN_TASK_HAVE_SUB_TASK,
                DataContract.TaskEntry.COLUMN_TASK_PARENT_TASK
        };

        //get cursor
        Cursor cursor = getContext().getContentResolver().query(
                DataContract.TaskEntry.TASK_CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if (cursor == null) return taskArrayList;

        cursor.move(-1); // move to the very top

        int idIndex = cursor.getColumnIndex(DataContract.TaskEntry._ID);
        int nameIndex = cursor.getColumnIndex(DataContract.TaskEntry.COLUMN_TASK_NAME);
        int dateIndex = cursor.getColumnIndex(DataContract.TaskEntry.COLUMN_TASK_DUE_DATE);
        int subIndex = cursor.getColumnIndex(DataContract.TaskEntry.COLUMN_TASK_HAVE_SUB_TASK);
        int parentIndex = cursor.getColumnIndex(DataContract.TaskEntry.COLUMN_TASK_PARENT_TASK);

        while (cursor.moveToNext()){
            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            long date = cursor.getLong(dateIndex);
            int sub = cursor.getInt(subIndex);
            int parent = cursor.getInt(parentIndex);

            Task temp = new Task(id, name, date, sub, parent);
            taskArrayList.add(temp);
        }

        cursor.close();

        return taskArrayList;
    }
}
