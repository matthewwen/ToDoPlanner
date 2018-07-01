package com.todoplanner.matthewwen.todoplanner.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.TaskEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.NoteEntry;

public class DataDbHelper extends SQLiteOpenHelper {

    //NAme of the database
    private static final String DATABASE_NAME = "userdataDb.db";

    //version of the database (keep it at one)
    private static final int VERSION = 1;

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION); //factory should be null
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create the task table in a string
        final String TASK_CREATE_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                TaskEntry._ID + " INTEGER PRIMARY KEY, " +
                TaskEntry.COLUMN_TASK_NAME + "TEXT NOT NULL, " +
                TaskEntry.COLUMN_TASK_DUE_DATE + "LONG, " +
                TaskEntry.COLUMN_TASK_HAVE_SUB_TASK + "INTEGER NOT NULL, " +
                TaskEntry.COLUMN_TASK_PARENT_TASK + "INTEGER NOT NULL);";
        //execute in the database by saying sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(TASK_CREATE_TABLE);

        //create the notes table in a string
        final String NOTE_CREATE_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                NoteEntry._ID + " INTEGER PRIMARY KEY, " +
                NoteEntry.COLUMN_NOTE_HEADING + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_NOTE_NOTES  + " TEXT);";

        //execute int the database
        sqLiteDatabase.execSQL(NOTE_CREATE_TABLE);
    }

    //Delete current table on then rebuild.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
