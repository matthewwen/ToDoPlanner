package com.matthewwen.todoplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.matthewwen.MyApplication;
import com.matthewwen.todoplanner.object.Section;
import com.matthewwen.todoplanner.object.TodoTasks;

import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;

import io.requery.android.database.sqlite.SQLiteDatabase;

@SuppressWarnings({"deprecated", "ResultOfMethodCallIgnored"})
@RunWith(AndroidJUnit4.class)
public class PhoneDatabase {

    private static SQLiteDatabase mDatabase = null;
    private static File mDatabaseFile = null;

    final static int version = 0;

    private static void setUp(Context context) {
        if (mDatabaseFile == null && mDatabase == null) {
            File dbDir = MyApplication.getAppContext().getDir("todoplanner", Context.MODE_PRIVATE);
            mDatabaseFile = new File(dbDir, "database.db");
            if (mDatabaseFile.exists()) {
                //mDatabaseFile.delete();
            }
            mDatabase     = SQLiteDatabase.openOrCreateDatabase(mDatabaseFile.getPath(), null);
            //mDatabase.setVersion(version);
        }
    }

    public static void tearDown() {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        if (mDatabaseFile != null) {
            mDatabaseFile = null;
        }
    }

    public static void insertSection(Context context, Section section) {
        setUp(context);
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS SECTION (Id INTEGER PRIMARY KEY, name TEXT, duedate INTEGER, complete INTEGER);");
        @SuppressLint("DefaultLocale")
        String inputStr = String.format("INSERT OR REPLACE INTO SECTION (Id, name, " +
                "duedate, complete) VALUES (%d, \'%s\', %d, %d);", section.id, section.name, section.duedate, section.complete);
        mDatabase.execSQL(inputStr);
    }

    @SuppressLint("DefaultLocale")
    public static void deleteSection(Context context, long id) {
        setUp(context);
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS SECTION (Id INTEGER PRIMARY KEY, name TEXT, duedate INTEGER, complete INTEGER);");
        mDatabase.execSQL(String.format("DELETE FROM SECTION WHERE Id=%d", id));
    }

    @SuppressLint("DefaultLocale")
    public static void deleteSectionTodo(long id) {
        setUp(null);
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS TASK (Id INTEGER PRIMARY KEY, name TEXT, duedate INTEGER, complete INTEGER, section INTEGER);");
        mDatabase.execSQL(String.format("DELETE FROM TASK WHERE section=%d", id));
    }

    @SuppressLint("DefaultLocale")
    public static void insertTask(Context context, TodoTasks tasks) {
        setUp(context);
        boolean hasNoId = tasks.id != -1;
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS TASK (" + (hasNoId ? "Id INTEGER PRIMARY KEY, ": "") + "name TEXT, duedate INTEGER, complete INTEGER, section INTEGER);");
        @SuppressLint("DefaultLocale")
        String inputStr = String.format("INSERT OR REPLACE INTO TASK (Id, name, duedate, " +
                "complete, section) VALUES (%d, \'%s\', %d, %d, %d);", tasks.id, tasks.name, tasks.duedate, tasks.complete, tasks.section);
        if (hasNoId) {
            inputStr = String.format("INSERT OR REPLACE INTO TASK (name, duedate, " +
                    "complete, section) VALUES (\'%s\', %d, %d, %d);", tasks.name, tasks.duedate, tasks.complete, tasks.section);
        }
        mDatabase.execSQL(inputStr);
    }

    public static ArrayList<Section> getTodoSection(Context context) {
        setUp(context);
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS SECTION (Id INTEGER PRIMARY KEY, name TEXT, duedate INTEGER, complete INTEGER);");
        Cursor c = mDatabase.rawQuery("SELECT * FROM SECTION", null);
        ArrayList<Section> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Section section = new Section(c.getLong(0), c.getString(1),
                        c.getLong(2), c.getLong(3));
                list.add(section);
            }while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public static ArrayList<TodoTasks> getTodoTask(Context context, long id) {
        setUp(context);
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS TASK (Id INTEGER PRIMARY KEY, name TEXT, duedate INTEGER, complete INTEGER, section INTEGER);");
        @SuppressLint("DefaultLocale")
        String queryStr = String.format("SELECT * FROM TASK WHERE section=%d", id);
        Log.v("MWEN", queryStr);
        Cursor c = mDatabase.rawQuery(queryStr, null);
        ArrayList<TodoTasks> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Log.v("MWEN", "creating task here");
                TodoTasks task = new TodoTasks(c.getLong(0), c.getString(1),
                        c.getLong(2), c.getLong(3), c.getLong(4));
                list.add(task);
            }while (c.moveToNext());
        }
        c.close();
        Log.v("MWEN", "list size: " + list.size());
        return list;
    }

    public static void deleteTodo(long id) {
        setUp(null);
        mDatabase.execSQL("CREATE TABLE IF NOT EXISTS TASK (Id INTEGER PRIMARY KEY, name TEXT, duedate INTEGER, complete INTEGER, section INTEGER);");
        mDatabase.execSQL(String.format("DELETE FROM TASK WHERE Id=%d", id));
    }
}
