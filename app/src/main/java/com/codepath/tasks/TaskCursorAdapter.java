package com.codepath.tasks;

/**
 * Created by Oleg on 7/15/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.codepath.tasks.data.TaskContract;

import static com.codepath.tasks.R.id.priority;

/**
 * {TaskCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of task data as its data source. This adapter knows
 * how to create list items for each row of task data in the {@link Cursor}.
 */
public class TaskCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link TaskCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public TaskCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO: Fill out this method and return the list item view (instead of null)
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO: Fill out this method
        TextView descTextView = (TextView) view.findViewById(R.id.description);
        TextView priorityTextView = (TextView) view.findViewById(priority);
        TextView statusTextView = (TextView) view.findViewById(R.id.status);

        int descColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION);
        int priorityColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY);
        int statusColumnIndex = cursor.getColumnIndex(TaskContract.TaskEntry.COLUMN_STATUS);

        String taskDescription = cursor.getString(descColumnIndex);
        String taskPriority = cursor.getString(priorityColumnIndex);
        String taskStatus = cursor.getString(statusColumnIndex);

        descTextView.setText(taskDescription);

        priorityTextView.setText(getPriorityText(taskPriority));
        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable priorityCircle = (GradientDrawable) priorityTextView.getBackground();
        // Get the appropriate background color based on the current task priority
        int priorityColor = getPriorityColor(context, taskPriority);
        // Set the color on the priority circle
        priorityCircle.setColor(priorityColor);

        statusTextView.setText(getStatusText(taskStatus));
    }

    private int getPriorityColor(Context context, String priority) {

        int priorityColorResourceId;

        switch (priority) {
            case "0":
                priorityColorResourceId = R.color.priority_low;
                break;
            case "1":
                priorityColorResourceId = R.color.priority_medium;
                break;
            case "2":
                priorityColorResourceId = R.color.priority_high;
                break;

            default:
                priorityColorResourceId = R.color.priority_low;
                break;
        }

        return ContextCompat.getColor(context, priorityColorResourceId);
    }

    private int getPriorityText(String priority) {

        switch (priority) {
            case "0":
                return R.string.priority_low;
            case "1":
                return R.string.priority_medium;
            case "2":
                return R.string.priority_high;
            default:
                return R.string.priority_low;
        }
    }

    private int getStatusText(String status) {

        switch (status) {
            case "0":
                return R.string.status_not_started;
            case "1":
                return R.string.status_in_progress;
            case "2":
                return R.string.status_completed;
            default:
                return R.string.status_not_started;
        }
    }
}
