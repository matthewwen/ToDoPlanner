package com.todoplanner.matthewwen.todoplanner.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {

    //the authority
    public final static String AUTHORITY = "com.todoplanner.matthewwen.todoplanner";

    //the uri with the content authority
    final static Uri BASE_CONTENT_AUTHORITY = Uri.parse("content://" + AUTHORITY);

    //the path to the table
    public static final String PATH_DATA = "userData";

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

    public static final class TodayEventEntry implements BaseColumns{

        //the table name
        public static final String TABLE_NAME = "userTodayEvents";

        //content uri with the appended path
        public static final Uri EVENT_CONTENT_URI = BASE_CONTENT_AUTHORITY.buildUpon().appendPath(PATH_DATA).appendPath(TABLE_NAME).build();

        //the column names label
        public static final String COLUMN_EVENT_NAME = "eventName";
        public static final String COLUMN_EVENT_START = "eventStart";
        public static final String COLUMN_EVENT_END = "eventEnd";
        public static final String COLUMN_EVENT_NOTE = "eventNote";
        public static final String COLUMN_EVENT_TASK_ID = "eventTask"; //if -1, then it is just an event
        public static final String COLUMN_EVENT_IN_PROGRESS = "eventProgress"; //if 0, then no. If 1, then yes.
        public static final String COLUMN_EVENT_STATIONARY = "eventStationary"; //if 0, then no. If 1, then yes
        public static final String COLUMN_EVENT_ALARM_SET = "eventAlarmSet"; //if 0, then no. If 1, then yes
        public static final String COLUMN_EVENT_START_SHOWN = "eventStartIsShown";
        public static final String COLUMN_EVENT_END_SHOWN = "eventEndIsShown";

        //this is for constant values
        public static final int NO_TASK_ID = -1;
        public static final int EVENT_IN_PROGRESS = 1;
        public static final int EVENT_NOT_IN_PROGRESS = 0;

        //this is for stationary vs. not stationary
        public static final int EVENT_STATIONARY = 1;
        public static final int EVENT_NOT_STATIONARY = 0;

        //this is for alarm set vs. alarm not set
        public static final int ALARM_SET = 1;
        public static final int ALARM_NOT_SET = 0;

        //this is for the start notification is shown set vs. start notification not set
        public static final int START_SHOWN = 1;
        public static final int START_NOT_SHOWN = 0;

        //this is for the end notification is shown vs. end notification not set
        public static final int END_SHOWN = 1;
        public static final int END_NOT_SHOWN = 0;

        //the projection
        public static final String[] PROJECTION = {
                _ID,
            COLUMN_EVENT_NAME,
            COLUMN_EVENT_START,
            COLUMN_EVENT_END,
            COLUMN_EVENT_TASK_ID,
            COLUMN_EVENT_NOTE,
            COLUMN_EVENT_IN_PROGRESS,
            COLUMN_EVENT_STATIONARY,
            COLUMN_EVENT_ALARM_SET,
            COLUMN_EVENT_START_SHOWN,
            COLUMN_EVENT_END_SHOWN};

        //All the Index
        public static final int COLUMN_EVENT_ID_FULL_INDEX = 0;
        public static final int COLUMN_EVENT_NAME_FULL_INDEX = 1;
        public static final int COLUMN_EVENT_START_FULL_INDEX = 2;
        public static final int COLUMN_EVENT_END_FULL_INDEX = 3;
        public static final int COLUMN_EVENT_TASK_ID_FULL_INDEX = 4;
        public static final int COLUMN_EVENT_NOTE_FULL_INDEX = 5;
        public static final int COLUMN_EVENT_IN_PROGRESS_FULL_INDEX = 6;
        public static final int COLUMN_EVENT_STATIONARY_FULL_INDEX = 7;
        public static final int COLUMN_EVENT_ALARM_SET_FULL_INDEX = 8;
        public static final int COLUMN_EVENT_START_SHOWN_FULL_INDEX = 9;
        public static final int COLUMN_EVENT_END_SHOWN_FULL_INDEX = 10;
    }

    public static final class PendingEventEntry implements BaseColumns{
        //the table name
        public static final String TABLE_NAME = "userPendingEvents";

        //content uri with the appended path
        public static final Uri EVENT_CONTENT_URI = BASE_CONTENT_AUTHORITY.buildUpon().appendPath(PATH_DATA).appendPath(TABLE_NAME).build();

        //the column names label
        public static final String COLUMN_EVENT_NAME = "eventName";
        public static final String COLUMN_EVENT_START = "eventStart";
        public static final String COLUMN_EVENT_END = "eventEnd";
        public static final String COLUMN_EVENT_NOTE = "eventNote";
        public static final String COLUMN_EVENT_TASK_ID = "eventTask"; //if -1, then it is just an event
        public static final String COLUMN_EVENT_STATIONARY = "eventStationary"; //if 0, then no. If 1, then yes

        //this is for stationary vs. not stationary
        public static final int EVENT_STATIONARY = 1;
        public static final int EVENT_NOT_STATIONARY = 0;

        public static final int NO_TASK_ID = -1;

        //the projection
        public static final String[] PROJECTION = {
                _ID,
                COLUMN_EVENT_NAME,
                COLUMN_EVENT_START,
                COLUMN_EVENT_END,
                COLUMN_EVENT_TASK_ID,
                COLUMN_EVENT_NOTE,
                COLUMN_EVENT_STATIONARY};

        //All the Index
        public static final int COLUMN_EVENT_ID_FULL_INDEX = 0;
        public static final int COLUMN_EVENT_NAME_FULL_INDEX = 1;
        public static final int COLUMN_EVENT_START_FULL_INDEX = 2;
        public static final int COLUMN_EVENT_END_FULL_INDEX = 3;
        public static final int COLUMN_EVENT_TASK_ID_FULL_INDEX = 4;
        public static final int COLUMN_EVENT_NOTE_FULL_INDEX = 5;
        public static final int COLUMN_EVENT_STATIONARY_FULL_INDEX = 6;
    }

    public static final class PastEventEntry implements BaseColumns{
        //the table name
        public static final String TABLE_NAME = "userPastEvents";

        //content uri with the appended path
        public static final Uri EVENT_CONTENT_URI = BASE_CONTENT_AUTHORITY.buildUpon().appendPath(PATH_DATA).appendPath(TABLE_NAME).build();

        //the column names label
        public static final String COLUMN_EVENT_NAME = "eventName";
        public static final String COLUMN_EVENT_START = "eventStart";
        public static final String COLUMN_EVENT_END = "eventEnd";
        public static final String COLUMN_EVENT_NOTE = "eventNote";
        public static final String COLUMN_EVENT_TASK_ID = "eventTask"; //if -1, then it is just an event

        public static final int NO_TASK_ID = -1;

        //the projection
        public static final String[] PROJECTION = {
                _ID,
                COLUMN_EVENT_NAME,
                COLUMN_EVENT_START,
                COLUMN_EVENT_END,
                COLUMN_EVENT_TASK_ID,
                COLUMN_EVENT_NOTE
        };

        //All the Index
        public static final int COLUMN_EVENT_ID_FULL_INDEX = 0;
        public static final int COLUMN_EVENT_NAME_FULL_INDEX = 1;
        public static final int COLUMN_EVENT_START_FULL_INDEX = 2;
        public static final int COLUMN_EVENT_END_FULL_INDEX = 3;
        public static final int COLUMN_EVENT_TASK_ID_FULL_INDEX = 4;
        public static final int COLUMN_EVENT_NOTE_FULL_INDEX = 5;

    }
}
