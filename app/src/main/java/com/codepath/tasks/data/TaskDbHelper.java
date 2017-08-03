package com.codepath.tasks.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.codepath.tasks.data.TaskContract.TaskEntry;

/**
 * Created by Oleg on 7/15/2017.
 */

public class TaskDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "etasker.db";

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TASKS_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + "(" +
                TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                TaskEntry.COLUMN_PRIORITY + " INTEGER NOT NULL, " +
                TaskEntry.COLUMN_STATUS + " INTEGER NOT NULL DEFAULT 0" +
                ");";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_TASKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
