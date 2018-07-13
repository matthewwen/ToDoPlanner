package com.todoplanner.matthewwen.todoplanner.data;

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
import com.todoplanner.matthewwen.todoplanner.data.DataContract.TodayEventEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.PendingEventEntry;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.PastEventEntry;

import java.util.Date;
import java.util.Objects;

import static com.todoplanner.matthewwen.todoplanner.data.DataContract.TodayEventEntry.COLUMN_EVENT_START;

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

    //from the table 'userTodayEvent'
    private static final int TABLE_TODAY_EVENT = 300;
    private static final int TABLE_EVENT_TODAY_ID = 301;

    //from the table 'userPendingEvent'
    private static final int TABLE_PENDING_EVENT = 302;
    private static final int TABLE_PENDING_EVENT_ID = 303;

    //from the table 'userPastEvent'
    private static final int TABLE_PAST_EVENT = 304;
    private static final int TABLE_PAST_EVENT_ID = 305;

    private static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //This is for the tasks
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + TaskEntry.TABLE_NAME, TABLE_TODO);

        //This is for the notes.
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + NoteEntry.TABLE_NAME, TABLE_NOTE);

        //This is for the Today events
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + TodayEventEntry.TABLE_NAME, TABLE_TODAY_EVENT);
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + TodayEventEntry.TABLE_NAME +"/#", TABLE_EVENT_TODAY_ID);

        //This is for the Past events
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + PastEventEntry.TABLE_NAME, TABLE_PAST_EVENT);
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + PastEventEntry.TABLE_NAME +"/#", TABLE_PAST_EVENT_ID);

        //This is for the Pending events
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + PendingEventEntry.TABLE_NAME, TABLE_PENDING_EVENT);
        uriMatcher.addURI(DataContract.AUTHORITY, DataContract.PATH_DATA + "/" + PendingEventEntry.TABLE_NAME +"/#", TABLE_PENDING_EVENT_ID);

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
            case TABLE_TODAY_EVENT: return queryTodayEvent(uri, columns, selection, selectionArgs, orderBy);
            case TABLE_EVENT_TODAY_ID: {
                selection = TodayEventEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return queryTodayEvent(uri, columns, selection, selectionArgs, orderBy);
            }
            case TABLE_PENDING_EVENT: return queryPendingEvent(uri, columns, selection, selectionArgs, orderBy);
            case TABLE_PENDING_EVENT_ID: {
                selection = TodayEventEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return queryPendingEvent(uri, columns, selection, selectionArgs, orderBy);
            }
            case TABLE_PAST_EVENT: return queryPastEvent(uri,columns,selection,selectionArgs,orderBy);
            case TABLE_PAST_EVENT_ID: {
                selection = TodayEventEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return queryPastEvent(uri, columns, selection, selectionArgs, orderBy);
            }
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

    private Cursor queryTodayEvent(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(TodayEventEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
        ContentResolver resolver = Objects.requireNonNull(getContext()).getContentResolver();
        if (resolver == null) return null;
        cursor.setNotificationUri(resolver, uri);
        return cursor;
    }

    private Cursor queryPastEvent(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(PastEventEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    private Cursor queryPendingEvent(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.query(PendingEventEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
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
            case TABLE_TODAY_EVENT: return eventTodayInsert(uri, contentValues);
            case TABLE_PAST_EVENT: return eventPastInsert(uri, contentValues);
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

    private Uri eventTodayInsert(Uri uri, @Nullable ContentValues contentValues){
        if (contentValues == null) return uri;

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SQLiteDatabase reader = mDbHelper.getReadableDatabase();

        //getting the cursor
        Cursor cursor = reader.rawQuery("SELECT * FROM " + TodayEventEntry.TABLE_NAME +
                " WHERE " + TodayEventEntry.COLUMN_EVENT_END + " > " + contentValues.getAsLong(COLUMN_EVENT_START), null);
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        //index columns and rows
        int eventStartIndex = cursor.getColumnIndex(TodayEventEntry.COLUMN_EVENT_START);
        int eventEndIndex = cursor.getColumnIndex(TodayEventEntry.COLUMN_EVENT_END);

        //getting the user start and end time
        Long newStartEvent = contentValues.getAsLong(TodayEventEntry.COLUMN_EVENT_START);
        Long newEndEvent = contentValues.getAsLong(TodayEventEntry.COLUMN_EVENT_END);

        //checking each event the cursor caught
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()){
            Long eventStart = cursor.getLong(eventStartIndex);
            Long eventEnd = cursor.getLong(eventEndIndex);
            if ((newStartEvent > eventStart && newStartEvent < eventEnd) ||
                    (newEndEvent > eventStart && newEndEvent < eventEnd)){
                Log.v(TAG, "Error adding event. It will be overlapping another event");
                cursor.close();
                return uri;
            }

        }
        cursor.close();

        Cursor cursor1 = reader.rawQuery("SELECT * FROM " + TodayEventEntry.TABLE_NAME +
                " WHERE " + TodayEventEntry.COLUMN_EVENT_IN_PROGRESS + " = " + TodayEventEntry.EVENT_IN_PROGRESS, null);
        cursor1.setNotificationUri(getContext().getContentResolver(), uri);

        int inProgressValues = TodayEventEntry.EVENT_NOT_IN_PROGRESS;
        cursor1.moveToPosition(-1);
        if (cursor1.getCount() == 0){
            Long current = new Date().getTime();
            if (newStartEvent <= current && newEndEvent >= current){
                inProgressValues = TodayEventEntry.EVENT_IN_PROGRESS;
            }
            cursor1.close();
        }

        contentValues.put(TodayEventEntry.COLUMN_EVENT_IN_PROGRESS, inProgressValues);

        long id = db.insert(TodayEventEntry.TABLE_NAME, null, contentValues);

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri eventPastInsert(Uri uri, ContentValues values){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(PastEventEntry.TABLE_NAME, null, values);
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int id = sUriMatcher.match(uri);
        switch (id){
            case TABLE_TODO: return deleteTaskAll(uri);
            case TABLE_NOTE: return deleteNoteAll(uri);
            case TABLE_TODAY_EVENT: return deleteTodayEvent(uri, s, strings);
            case TABLE_EVENT_TODAY_ID: {
                s = TodayEventEntry._ID + "=?";
                strings = new String[] {Long.toString(ContentUris.parseId(uri))};
                return deleteTodayEvent(uri, s, strings);
            }
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

    public int deleteTodayEvent(Uri uri, String selection, String[] selectionArgs){
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return mDbHelper.getWritableDatabase().delete(TodayEventEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int id = sUriMatcher.match(uri);
        Log.v(TAG, "Update uri: " + uri.toString());
        switch (id){
            case TABLE_EVENT_TODAY_ID:
                s = TodayEventEntry._ID + " =?";
                strings = new String[]{Long.toString(ContentUris.parseId(uri))};
                return updateEvent(uri, contentValues, s, strings);
        }
        return 0;
    }

    public int updateEvent(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int number = db.update(TodayEventEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return number;
    }

}
