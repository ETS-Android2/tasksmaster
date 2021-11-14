package com.dandelion.tasksmaster;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class TaskDetail extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();

        String title = intent.getExtras().getString("title");
        TextView titleText = findViewById(R.id.taskTitleView);
        titleText.setText(title);

        String body = intent.getExtras().getString("body");
        TextView taskBody = findViewById(R.id.taskBodyField);
        taskBody.setText(body);

        String state = intent.getExtras().getString("state");
        TextView taskState = findViewById(R.id.taskStateField);
        taskState.setText(state);
    }
}