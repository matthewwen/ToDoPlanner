package com.todoplanner.matthewwen.todoplanner.objects;

import com.todoplanner.matthewwen.todoplanner.data.DataContract;

public class Note implements Story{

    private int id;
    private String heading;
    private String details;

    public Note(int id, String heading){
        this.id = id; this.heading = heading;
    }

    public void setDetails(String details){
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public String getHeading() {
        return heading;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getStoryType() {
        return DataContract.NoteEntry.TABLE_NAME;
    }
}
