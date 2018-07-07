package com.todoplanner.matthewwen.todoplanner.loaders;

import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;

import com.todoplanner.matthewwen.todoplanner.objects.Note;
import com.todoplanner.matthewwen.todoplanner.data.DataContract.NoteEntry;

import java.util.ArrayList;

public class NoteLoader extends AsyncTaskLoader<ArrayList<Note>>{

    public NoteLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public ArrayList<Note> loadInBackground() {
        String[] selection = {NoteEntry._ID,
        NoteEntry.COLUMN_NOTE_HEADING,
        NoteEntry.COLUMN_NOTE_NOTES};

        @SuppressLint("Recycle")
        Cursor cursor = getContext().getContentResolver().query(NoteEntry.NOTE_CONTENT_URI,
                selection,
                null,
                null,
                null);

        if (cursor == null) return new ArrayList<>();

        cursor.move(-1);

        ArrayList<Note> allNotes = new ArrayList<>();
        while (cursor.moveToNext()){
            int idIndex = cursor.getColumnIndex(NoteEntry._ID);
            int headingIndex = cursor.getColumnIndex(NoteEntry.COLUMN_NOTE_HEADING);
            int noteIndex = cursor.getColumnIndex(NoteEntry.COLUMN_NOTE_NOTES);

            int id = cursor.getInt(idIndex);
            String heading = cursor.getString(headingIndex);
            String note = cursor.getString(noteIndex);

            Note temp = new Note(id, heading, note);
            allNotes.add(temp);
        }

        return allNotes;
    }
}
