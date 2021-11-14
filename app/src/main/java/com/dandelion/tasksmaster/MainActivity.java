package com.dandelion.tasksmaster;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTask = findViewById(R.id.addTaskBtn);
        addTask.setOnClickListener(v -> {
            Intent goToAddTask = new Intent(MainActivity.this, AddTask.class);
            startActivity(goToAddTask);
        });

        Button allTasks = findViewById(R.id.allTasksBtn);
        allTasks.setOnClickListener(v -> {
            Intent goToAllTasks = new Intent(MainActivity.this, AllTasks.class);
            startActivity(goToAllTasks);
        });

        Button settings = findViewById(R.id.SettingsBtn);
        settings.setOnClickListener(v -> {
            Intent goToSettings = new Intent(MainActivity.this, Settings.class);
            startActivity(goToSettings);
        });

        // tasks from database
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        List<Task> tasks = appDatabase.taskDao().getAll();

        // get recycler view
        RecyclerView allTasksRecyclerView = findViewById(R.id.recycle);

        // set layout manager for the view (determine if liner list or grid list)
        allTasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set the adapter for this recycler
        allTasksRecyclerView.setAdapter(new TaskAdapter(tasks));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        String withMyTask = "'s Tasks";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String username = sharedPreferences.getString("username","user");

        TextView usernameField = findViewById(R.id.myTask);
        usernameField.setText(username + withMyTask);
    }
}