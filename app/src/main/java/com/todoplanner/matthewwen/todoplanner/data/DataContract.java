package com.todoplanner.matthewwen.todoplanner.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {

    //the authority
    public final static String AUTHORITY = "com.todoplanner.matthewwen.todoplanner";

    //the uri with the content authority
    public final static Uri BASE_CONTENT_AUTHORITY = Uri.parse("content://" + AUTHORITY);

    //the path to the table
    public static final String PATH_DATA = "userData";

    //this is the full content authority
    public static String CONTENT_AUTHORITY = AUTHORITY + "/" + PATH_DATA ;

    public static final class TaskEntry implements BaseColumns{

        //the table names label as TABLE_NAME
        public static final String TABLE_NAME = "userTasks";

        //the content uri with the appended path
        public static final Uri TASK_CONTENT_URI = BASE_CONTENT_AUTHORITY.buildUpon().appendPath(PATH_DATA).appendPath(TABLE_NAME).build();

        //the column names label as COLUMN_SOMETHING = "text"
        public static final String COLUMN_TASK_NAME = "taskName";
        public static final String COLUMN_TASK_DUE_DATE = "dueDate";
        public static final String COLUMN_TASK_HAVE_SUB_TASK = "subTask"; //1 means yes, 0 means no
        public static final String COLUMN_TASK_PARENT_TASK = "parentTask"; //-1 means no parent tasks, anything else yes.

        /**
         * The Value is the integer from the database.
         * @param value
         * @return
         */
        public boolean haveSubTask(int value){
            return value == 1;
        }

    }

    public static final class NoteEntry implements BaseColumns{

        //the table names label as TABLE_NAME
        public static final String TABLE_NAME = "userNote";


        //the content uri with the appended path
        public static final Uri NOTE_CONTENT_URI = BASE_CONTENT_AUTHORITY.buildUpon().appendPath(PATH_DATA).appendPath(TABLE_NAME).build();

        //the column names label
        public static final String COLUMN_NOTE_HEADING = "heading";
        public static final String COLUMN_NOTE_NOTES = "notes";

    }

    public static final class EventEntry implements BaseColumns{

        //the table name
        public static final String TABLE_NAME = "userEvents";

        //content uri with the appended path
        public static final Uri EVENT_CONTENT_URI = BASE_CONTENT_AUTHORITY.buildUpon().appendPath(PATH_DATA).appendPath(TABLE_NAME).build();

        //the column names label
        public static final String COLUMN_EVENT_NAME = "eventName";
        public static final String COLUMN_EVENT_START = "eventStart";
        public static final String COLUMN_EVENT_END = "eventEnd";
        public static final String COLUMN_EVENT_NOTE = "eventNote";
        public static final String COLUMN_EVENT_TASK_ID = "eventTask"; //if -1, then it is just an event
        public static final String COLUMN_EVENT_IN_PROGRESS = "eventProgress"; //if 0, then no. If 1, then yes.

        //this is for constant values
        public static final int NO_TASK_ID = -1;
        public static final int EVENT_IN_PROGRESS = 1;
        public static final int EVENT_NOT_IN_PROGRESS = 0;

        //the projection
        public static final String[] PROJECTION = {_ID,
            COLUMN_EVENT_NAME,
            COLUMN_EVENT_START,
            COLUMN_EVENT_END,
            COLUMN_EVENT_NOTE,
            COLUMN_EVENT_TASK_ID,
            COLUMN_EVENT_IN_PROGRESS};

        public static final String[] PROJECTION_DATE = {_ID,
            COLUMN_EVENT_START,
            COLUMN_EVENT_END,
            COLUMN_EVENT_IN_PROGRESS};

        public static final String[] PROJECTION_IN_PROGRESS = {_ID,
            COLUMN_EVENT_IN_PROGRESS};
    }
}
