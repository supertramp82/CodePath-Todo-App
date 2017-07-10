package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        String txtEditTodo = getIntent().getStringExtra("editTodo");
        position = getIntent().getIntExtra("position", 0);

        EditText etEditItem = (EditText) findViewById(R.id.etEditItem);
        etEditItem.setText(txtEditTodo);
    }

    public void onSave(View view) {
        EditText etEditItem = (EditText) findViewById(R.id.etEditItem);
        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("txtEditTodo", etEditItem.getText().toString());
        data.putExtra("code", 200); // ints work too
        data.putExtra("position", position);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
