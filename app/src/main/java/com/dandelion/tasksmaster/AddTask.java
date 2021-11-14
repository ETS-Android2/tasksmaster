package com.dandelion.tasksmaster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskQl;

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

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        taskDao=appDatabase.taskDao();


        Button addTaskBtn = findViewById(R.id.saveTaskBtn);
        addTaskBtn.setOnClickListener(v -> {
            String title = taskTitle.getText().toString();
            String body = taskBody.getText().toString();
            String state = taskState.getText().toString();

            TaskQl task = TaskQl.builder()
                    .title(title)
                    .body(body)
                    .state(state)
                    .build();

            // mutations are used to create, update, or delete data
            Amplify.API.mutate(
                    ModelMutation.create(task),
                    response -> Log.i("TasksApp", "add task with id" + response.getData().getId()),
                    error -> Log.e("TasksApp", "Create failed", error)
            );

            Intent goToHomePage = new Intent(AddTask.this, MainActivity.class);
            startActivity(goToHomePage);
        });


    }
}
