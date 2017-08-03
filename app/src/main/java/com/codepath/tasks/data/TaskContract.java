package com.codepath.tasks.data;


import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Tasks app.
 */
public final class TaskContract {

    public static final String CONTENT_AUTHORITY = "com.codepath.tasks";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TASKS = "tasks";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private TaskContract() {}

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single task.
     */
    public static final class TaskEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TASKS);

        /** Name of database table for tasks */
        public final static String TABLE_NAME = "tasks";

        /**
         * Unique ID number for the task (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Description for the task.
         *
         * Type: TEXT
         */
        public final static String COLUMN_DESCRIPTION = "description";

        /**
         * Priority of the task.
         *
         * The only possible values are LOW, MEDIUM, HIGH
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRIORITY = "priority";

        /**
         * Status of the task.
         *
         * The only possible values are NOT STARTED, IN PROGRESS, COMPLETED
         *
         * Type: INTEGER
         */
        public final static String COLUMN_STATUS = "status";

        /**
         * Possible values for the tasks prority and status.
         */
        public static final int PRIORITY_LOW = 0;
        public static final int PRIORITY_MEDIUM = 1;
        public static final int PRIORITY_HIGH = 2;

        public static final int STATUS_NOT_STARTED = 0;
        public static final int STATUS_IN_PROGRESS = 1;
        public static final int STATUS_COMPLETED = 2;
    }

}

