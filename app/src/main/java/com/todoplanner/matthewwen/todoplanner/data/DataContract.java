package com.todoplanner.matthewwen.todoplanner.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {

    //the authority
    public final static String AUTHORITY = "com.todoplanner.matthewwen.todplanner";

    //the uri with the content authority
    public final static Uri BASE_CONTENT_AUTHORITY = Uri.parse("content://" + AUTHORITY);

    //the path to the table
    public static final String PATH_DATA = "userData";

    public static final class TaskEntry implements BaseColumns{

        //the table names label as TABLE_NAME
        public static final String TABLE_NAME = "userTasks";

        //the content uri with the appended path
        public static final Uri TASK_CONTENT_URI = BASE_CONTENT_AUTHORITY.buildUpon().appendPath(TABLE_NAME).build();

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
        public static final Uri NOTE_CONTENT_URI = BASE_CONTENT_AUTHORITY.buildUpon().appendPath(TABLE_NAME).build();

        //the column names label
        public static final String COLUMN_NOTE_HEADING = "heading";
        public static final String COLUMN_NOTE_NOTES = "notes";

    }
}
