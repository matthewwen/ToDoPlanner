package com.todoplanner.matthewwen.todoplanner.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.TaskEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.NoteEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.TodayEventEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.PendingEventEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.PastEventEntry;

public class DataDbHelper extends SQLiteOpenHelper {

    //NAme of the database
    private static final String DATABASE_NAME = "userdataDb.db";

    //version of the database (keep it at one)
    private static final int VERSION = 1;

    DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION); //factory should be null
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create the task table in a string
        final String TASK_CREATE_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                TaskEntry._ID + " INTEGER PRIMARY KEY, " +
                TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, " +
                TaskEntry.COLUMN_TASK_DUE_DATE + " LONG, " +
                TaskEntry.COLUMN_TASK_HAVE_SUB_TASK + " INTEGER NOT NULL, " +
                TaskEntry.COLUMN_TASK_PARENT_TASK + " INTEGER NOT NULL);";
        //execute in the database by saying sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(TASK_CREATE_TABLE);

        //create the notes table in a string
        final String NOTE_CREATE_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                NoteEntry._ID + " INTEGER PRIMARY KEY, " +
                NoteEntry.COLUMN_NOTE_HEADING + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_NOTE_NOTES  + " TEXT);";
        //execute into the database
        sqLiteDatabase.execSQL(NOTE_CREATE_TABLE);

        //create the today event table in a string
        final String TODAY_EVENT_CREATE_TABLE = "CREATE TABLE " + TodayEventEntry.TABLE_NAME + " (" +
                TodayEventEntry._ID + " INTEGER PRIMARY KEY, " +
                TodayEventEntry.COLUMN_EVENT_NAME + " TEXT, " +
                TodayEventEntry.COLUMN_EVENT_START + " LONG NOT NULL, " +
                TodayEventEntry.COLUMN_EVENT_END + " LONG NOT NULL, " +
                TodayEventEntry.COLUMN_EVENT_NOTE + " TEXT, " +
                TodayEventEntry.COLUMN_EVENT_TASK_ID + " INTEGER NOT NULL, " +
                TodayEventEntry.COLUMN_EVENT_IN_PROGRESS + " INTEGER, " +
                TodayEventEntry.COLUMN_EVENT_STATIONARY + " INTEGER, " +
                TodayEventEntry.COLUMN_EVENT_ALARM_SET + " INTEGER);";

        //execute into the database
        sqLiteDatabase.execSQL(TODAY_EVENT_CREATE_TABLE);

        //create the pending event table in a string
        final String PENDING_EVENT_CREATE_TABLE = "CREATE TABLE " + PendingEventEntry.TABLE_NAME + " (" +
                PendingEventEntry._ID + " INTEGER PRIMARY KEY, " +
                PendingEventEntry.COLUMN_EVENT_NAME + " TEXT, " +
                PendingEventEntry.COLUMN_EVENT_START + " LONG NOT NULL, " +
                PendingEventEntry.COLUMN_EVENT_END + " LONG NOT NULL, " +
                PendingEventEntry.COLUMN_EVENT_NOTE + " TEXT, " +
                PendingEventEntry.COLUMN_EVENT_TASK_ID + " INTEGER NOT NULL, " +
                PendingEventEntry.COLUMN_EVENT_STATIONARY + " INTEGER);";

        //execute into the database
        sqLiteDatabase.execSQL(PENDING_EVENT_CREATE_TABLE);

        //create the past event table in a string
        final String PAST_EVENT_CREATE_TABLE = "CREATE TABLE " + PastEventEntry.TABLE_NAME + " (" +
                PastEventEntry._ID + " INTEGER PRIMARY KEY, " +
                PastEventEntry.COLUMN_EVENT_NAME + " TEXT, " +
                PastEventEntry.COLUMN_EVENT_START + " LONG NOT NULL, " +
                PastEventEntry.COLUMN_EVENT_END + " LONG NOT NULL, " +
                PastEventEntry.COLUMN_EVENT_NOTE + " TEXT, " +
                PastEventEntry.COLUMN_EVENT_TASK_ID + " INTEGER NOT NULL);";

        //execute into the database
        sqLiteDatabase.execSQL(PAST_EVENT_CREATE_TABLE);
    }

    //Delete current table on then rebuild.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TodayEventEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PastEventEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PendingEventEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
