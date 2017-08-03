package com.codepath.tasks;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.codepath.tasks.data.TaskContract.TaskEntry;

//import android.support.design.widget.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView taskListView;

    private static final int TASK_LOADER = 0;
    TaskCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        taskListView.setEmptyView(emptyView);

        mCursorAdapter = new TaskCursorAdapter(this, null);
        taskListView.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(TASK_LOADER, null, this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setupListViewListener();
        setupOnItemClickListener();
/*
        // Setup plus to open EditorActivity
        FloatingActionButton plus = (FloatingActionButton) findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                startActivity(intent);
            }
        });
*/
    }

    private void insertTask() {
        // Create test ContentValues object where column names are the keys,

        ContentValues values = new ContentValues();

        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        if (!itemText.equals("") )
            values.put(TaskEntry.COLUMN_DESCRIPTION, itemText.trim());
        else
            values.put(TaskEntry.COLUMN_DESCRIPTION, "Something to think about...");
        values.put(TaskEntry.COLUMN_PRIORITY, TaskEntry.PRIORITY_LOW);
        values.put(TaskEntry.COLUMN_STATUS, TaskEntry.STATUS_NOT_STARTED);

        getContentResolver().insert(
                TaskEntry.CONTENT_URI,   // the user dictionary content URI
                values                          // the values to insert
        );
    }

    public void onAddItem(View view) {

        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String taskDesc = etNewItem.getText().toString();

        Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
        intent.putExtra("newTask", taskDesc);
        startActivity(intent);

        etNewItem.setText("");
    }

    private void setupListViewListener() {
        taskListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {

                getContentResolver().delete(
                        ContentUris.withAppendedId(TaskEntry.CONTENT_URI, id),   // the user dictionary content URI
                        null,
                        null
                );

                mCursorAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void setupOnItemClickListener() {
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                Uri uri = ContentUris.withAppendedId(TaskEntry.CONTENT_URI, id);
                intent.setData(uri);
                startActivity(intent);
            }
        });
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
                TaskEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_task:
                insertTask();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_tasks:
                // Do nothing for now
                getContentResolver().delete(
                        TaskEntry.CONTENT_URI,  // the user dictionary content URI
                        null, null              // the values to insert
                );
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
