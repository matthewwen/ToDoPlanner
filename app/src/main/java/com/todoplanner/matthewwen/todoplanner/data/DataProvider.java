package com.todoplanner.matthewwen.todoplanner.data;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.todoplanner.matthewwen.todoplanner.data.DataContract.TaskEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.NoteEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.EventEntry;

import java.util.Objects;

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

    //from the table 'userEvent
    private static final int TABLE_EVENT = 300;

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //This is for the tasks
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + TaskEntry.TABLE_NAME, TABLE_TODO);

        //This is for the notes.
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + NoteEntry.TABLE_NAME, TABLE_NOTE);

        //This is for the events
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + EventEntry.TABLE_NAME, TABLE_EVENT);

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
                        @Nullable String[] selectionArgs, @Nullable String orderBy) {
        int id = sUriMatcher.match(uri);
        switch (id){
            case TABLE_TODO: return queryTask(uri, columns, selection, selectionArgs, orderBy);
            case TABLE_NOTE: return queryNotes(uri, columns, selection, selectionArgs, orderBy);
            case TABLE_EVENT: return queryEvent(uri, columns, selection, selectionArgs, orderBy);
            default:
                Log.e(TAG, "Url does not work: " + uri.toString());
                return null;
        }
    }

    private Cursor queryTask(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null,  orderBy);
        ContentResolver resolver = Objects.requireNonNull(getContext()).getContentResolver();
        if (resolver == null) return null;
        cursor.setNotificationUri(resolver, uri);
        return cursor;
    }

    private Cursor queryNotes(Uri uri, String[] columns, String selection, String[] selectionArgs, String orderBy){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(NoteEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    private Cursor queryEvent(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(EventEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
        ContentResolver resolver = Objects.requireNonNull(getContext()).getContentResolver();
        if (resolver == null) return null;
        cursor.setNotificationUri(resolver, uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int id = sUriMatcher.match(uri);
        switch (id){
            case TABLE_TODO: return taskInsert(uri, contentValues);
            case TABLE_NOTE: return noteInsert(uri, contentValues);
            case TABLE_EVENT: return eventInsert(uri, contentValues);
        }
        return uri;
    }

    private Uri noteInsert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(NoteEntry.TABLE_NAME, null, contentValues);

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri taskInsert(Uri uri, ContentValues contentValues){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(TaskEntry.TABLE_NAME, null, contentValues);

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri eventInsert(Uri uri, ContentValues contentValues){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(EventEntry.TABLE_NAME, null, contentValues);

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int id = sUriMatcher.match(uri);
        switch (id){
            case TABLE_TODO: return deleteTaskAll(uri);
            case TABLE_NOTE: return deleteNoteAll(uri);
            case TABLE_EVENT: return deleteAllEvent(uri);
        }
        return 0;
    }

    private int deleteNoteAll(Uri uri) {
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return mDbHelper.getWritableDatabase().delete(NoteEntry.TABLE_NAME, null, null);
    }

    private int deleteTaskAll(Uri uri){
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return mDbHelper.getWritableDatabase().delete(TaskEntry.TABLE_NAME, null, null);
    }

    public int deleteAllEvent(Uri uri){
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return mDbHelper.getWritableDatabase().delete(EventEntry.TABLE_NAME, null, null);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

}
