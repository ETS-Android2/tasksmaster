package com.dandelion.tasksmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class AddTask extends AppCompatActivity {
    AppDatabase appDatabase;
    TaskDao taskDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);


        EditText taskTitle = findViewById(R.id.taskTitleField);
        EditText taskBody = findViewById(R.id.taskBodyField);
        EditText taskState = findViewById(R.id.taskStateField);
        // get add task button
        Button addTaskBtn = findViewById(R.id.saveTaskBtn);
        // add listener
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDatabase appDatabase;
                String title = taskTitle.getText().toString();
                String body = taskBody.getText().toString();
                String state = taskState.getText().toString();

                appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks").allowMainThreadQueries().fallbackToDestructiveMigration().build();

                // save input fields into object
                Task task = new Task(title, body, state);
                // save to db
                appDatabase.taskDao().insertAll(task);
                // redirect to menu page
                Intent goToHomePage = new Intent(AddTask.this, MainActivity.class);
                startActivity(goToHomePage);
            }
        });


    }
}
