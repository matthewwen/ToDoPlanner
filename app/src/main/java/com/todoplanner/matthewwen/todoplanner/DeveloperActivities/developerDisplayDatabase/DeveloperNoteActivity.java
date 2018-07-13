package com.todoplanner.matthewwen.todoplanner.developerActivities.developerDisplayDatabase;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.todoplanner.matthewwen.todoplanner.R;
import com.todoplanner.matthewwen.todoplanner.adapter.DeveloperNoteAdapter;
import com.todoplanner.matthewwen.todoplanner.data.DataContract;
import com.todoplanner.matthewwen.todoplanner.loaders.NoteLoader;
import com.todoplanner.matthewwen.todoplanner.objects.Note;

import java.util.ArrayList;

public class DeveloperNoteActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Note>>{

    private static final String heading = "Okay, this will be fun";
    private static final String body = "Once upon a time, there was a kid named matthew who wanted to learn how to code. " +
            "He tried his best by learning on this online web service called udacity. Udacity is kinda awesome but there " +
            "was this one weird kid named ?. He had a really thick accident. Too thick that I could not understand him. Instead " +
            "I gave up on my dream of learning to code because my vocab was worse than that stupid tutor. The end!";

    private DeveloperNoteAdapter mAdapter;

    private static final int LOADER_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_note);

        RecyclerView mRecyclerView = findViewById(R.id.developer_note_rv);
        mAdapter = new DeveloperNoteAdapter(this, new ArrayList<Note>());

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home: super.onBackPressed(); return true;
            case R.id.developer_menu_add_note: return insertDummyNote();
            case R.id.developer_menu_refresh_note: getLoaderManager().initLoader(LOADER_ID, null, this); return true;
            case R.id.developer_menu_clear_note:{ getContentResolver().
                    delete(DataContract.NoteEntry.NOTE_CONTENT_URI, null, null);
                getLoaderManager().restartLoader(LOADER_ID, null, this);
                return true; }
        }
        return false;
    }

    private boolean insertDummyNote(){
        ContentValues values = new ContentValues();
        values.put(DataContract.NoteEntry.COLUMN_NOTE_HEADING, heading);
        values.put(DataContract.NoteEntry.COLUMN_NOTE_NOTES, body);

        getContentResolver().insert(DataContract.NoteEntry.NOTE_CONTENT_URI, values);

        getLoaderManager().restartLoader(LOADER_ID, null, this);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.developer_add_note, menu);
        return true;
    }

    @Override
    public Loader<ArrayList<Note>> onCreateLoader(int id, Bundle args) {
        return new NoteLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Note>> loader, ArrayList<Note> data) {
        mAdapter.addAllNotes(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Note>> loader) {
        mAdapter.clear();
    }
}
