package com.codepath.tasks;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.tasks.data.TaskContract.TaskEntry;

public class EditItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** EditText field to enter the task description */
    private EditText mDescription;

    /** EditText field to enter the task priority */
    private Spinner mPrioritySpinner;

    /** EditText field to enter the task status */
    private Spinner mStatusSpinner;

    private int mPriority = TaskEntry.PRIORITY_LOW;
    private int mStatus = TaskEntry.STATUS_NOT_STARTED;

    private static final int TASK_LOADER = 0;
    private Uri mCurrentTaskUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        mCurrentTaskUri = getIntent().getData();

        // Find all relevant views that we will need to read user input from
        mDescription = (EditText) findViewById(R.id.edit_description);
        mPrioritySpinner = (Spinner) findViewById(R.id.spinner_priority);
        mStatusSpinner = (Spinner) findViewById(R.id.spinner_status);

        if (mCurrentTaskUri == null) {
            setTitle(R.string.edit_item_activity_add_task);
            mDescription.setText(getIntent().getExtras().getString("newTask"));
        }
        else {
            setTitle(R.string.edit_item_activity_edit_task);
            getLoaderManager().initLoader(TASK_LOADER, null, this);
        }

        setupPrioritySpinner();
        setupStatusSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the priority of the task.
     */
    private void setupPrioritySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter prioritySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_priority_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        prioritySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mPrioritySpinner.setAdapter(prioritySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.priority_low))) {
                        mPriority = TaskEntry.PRIORITY_LOW;
                    } else if (selection.equals(getString(R.string.priority_medium))) {
                        mPriority = TaskEntry.PRIORITY_MEDIUM;
                    } else {
                        mPriority = TaskEntry.PRIORITY_HIGH;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPriority = TaskEntry.PRIORITY_LOW;
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the status of the task.
     */
    private void setupStatusSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter statusSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_status_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mStatusSpinner.setAdapter(statusSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.status_not_started))) {
                        mStatus = TaskEntry.STATUS_NOT_STARTED;
                    } else if (selection.equals(getString(R.string.status_in_progress))) {
                        mStatus = TaskEntry.STATUS_IN_PROGRESS;
                    } else {
                        mStatus = TaskEntry.STATUS_COMPLETED;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mStatus = TaskEntry.STATUS_NOT_STARTED;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveTask();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                getContentResolver().delete(
                        mCurrentTaskUri,   // the user dictionary content URI
                        null, null         // the values to insert
                );
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get user input from editor and save new task into database.
     */
    private void saveTask() {

        Uri rowId;

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String descString = mDescription.getText().toString().trim();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_DESCRIPTION, descString);
        values.put(TaskEntry.COLUMN_STATUS, mStatus);
        values.put(TaskEntry.COLUMN_PRIORITY, mPriority);

        if (TextUtils.isEmpty(descString))
            return;

        if (mCurrentTaskUri == null)
            rowId = getContentResolver().insert(
                    TaskEntry.CONTENT_URI,   // the user dictionary content URI
                    values                          // the values to insert
            );
        else
            getContentResolver().update(
                mCurrentTaskUri,   // the user dictionary content URI
                values, // the values to update
                null,
                null
            );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TaskEntry._ID,
                TaskEntry.COLUMN_DESCRIPTION,
                TaskEntry.COLUMN_PRIORITY,
                TaskEntry.COLUMN_STATUS
        };

        return new CursorLoader(this,
                mCurrentTaskUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            mDescription.setText(cursor.getString(cursor.getColumnIndex(TaskEntry.COLUMN_DESCRIPTION)));
            mPrioritySpinner.setSelection(cursor.getInt(cursor.getColumnIndex(TaskEntry.COLUMN_PRIORITY)));
            mStatusSpinner.setSelection(cursor.getInt(cursor.getColumnIndex(TaskEntry.COLUMN_STATUS)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDescription.setText("");
        mPrioritySpinner.setSelection(0);
        mStatusSpinner.setSelection(0);
    }
}
