package com.codepath.tasks.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.codepath.tasks.data.TaskContract.TaskEntry;
import java.security.Provider;

/**
 * Created by Oleg on 7/14/2017.
 */

public class TaskProvider extends ContentProvider {

    public static final String LOG_TAG = Provider.class.getSimpleName();

    /** URI matcher code for the content URI for the tasks table */
    private static final int TASKS = 100;

    /** URI matcher code for the content URI for a single task in the tasks table */
    private static final int TASK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.codepath.tasks/tasks" will map to the
        // integer code {@link #TASKS}. This URI is used to provide access to MULTIPLE rows
        // of the tasks table.
        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TASKS, TASKS);

        // The content URI of the form "content://com.codepath.tasks/tasks/#" will map to the
        // integer code {@link #TASK_ID}. This URI is used to provide access to ONE single row
        // of the tasks table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.codepath.tasks/tasks/3" matches, but
        // "content://com.codepath.tasks/tasks" (without a number at the end) doesn't match.
        sUriMatcher.addURI(TaskContract.CONTENT_AUTHORITY, TaskContract.PATH_TASKS + "/#", TASK_ID);
    }
    /** Database helper that will provide us access to the database */
    private TaskDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new TaskDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch(match) {
            case TASKS:
                cursor = database.query(TaskEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TASK_ID:
                selection = TaskEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case TASKS:
                return insertTask(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertTask(Uri uri, ContentValues values) {

        // Check that the description is not null
        String name = values.getAsString(TaskEntry.COLUMN_DESCRIPTION);
        if (name == null) {
            throw new IllegalArgumentException("Task requires a description");
        }

        // Check that the priority is not null
        Integer priority = values.getAsInteger(TaskEntry.COLUMN_PRIORITY);
        if (priority == null) {
            throw new IllegalArgumentException("Task requires a priority");
        }

        // Check that the status is not null
        Integer status = values.getAsInteger(TaskEntry.COLUMN_STATUS);
        if (status == null) {
            throw new IllegalArgumentException("Task requires a status");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new task with the given values
        long id = database.insert(TaskEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return updateTask(uri, contentValues, selection, selectionArgs);
            case TASK_ID:
                // For the TASK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = TaskEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTask(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update tasks in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more tasks).
     * Return the number of rows that were successfully updated.
     */
    private int updateTask(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted = 0;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TASK_ID:
                // Delete a single row given by the ID in the URI
                selection = TaskEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
