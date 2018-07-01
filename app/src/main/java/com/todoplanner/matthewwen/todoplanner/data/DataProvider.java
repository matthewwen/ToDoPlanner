package com.todoplanner.matthewwen.todoplanner.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.data.DataContract.TaskEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.NoteEntry;

public class DataProvider extends ContentProvider {

    //the Log Tag
    private static final String TAG = DataProvider.class.getSimpleName();

    //keep the database as a local variable.
    private DataDbHelper mDbHelper;

    //this is a uri matcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //from the table 'userTask'
    private static final int TABLE_TODO = 100;

    //from the table 'userNotes'
    private static final int TABLE_NOTE = 200;

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //This is for the tasks
        uriMatcher.addURI(DataContract.AUTHORITY, TaskEntry.TABLE_NAME, TABLE_TODO);

        //This is for the notes.
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + " SELECT * FROM " + NoteEntry.TABLE_NAME, TABLE_NOTE);

        return uriMatcher;
    }

    //create the database and get the context by getcontext(); return true when done.
    @Override
    public boolean onCreate() {
        mDbHelper = new DataDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] columns, @Nullable String selection,
                        @Nullable String[] selectonArgs, @Nullable String orderBy) {
        int id = sUriMatcher.match(uri);
        switch (id){
            case TABLE_TODO: return queryTask(columns, selection, selectonArgs, orderBy);
            case TABLE_NOTE: return queryNotes(columns, selection, selectonArgs, orderBy);
            default:
                Log.e(TAG, "Url does not work: " + uri.toString());
                return null;
        }
    }

    private Cursor queryTask(String[] columns, String selection, String[] selectionArgs, String orderBy){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        return sqLiteDatabase.query(TaskEntry.TABLE_NAME, columns, selection, selectionArgs, null, null,  orderBy);
    }

    private Cursor queryNotes(String[] columns, String selection, String[] selectionArgs, String orderBy){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        return sqLiteDatabase.query(NoteEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, orderBy);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

}
