package com.dandelion.tasksmaster;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskQl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
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

        try {
            Amplify.addPlugin(new AWSApiPlugin());
            // Add this line, to include the Auth plugin.
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());


            Log.i("Main Activity", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("Main Activity", "Could not initialize Amplify", error);
        }

        List<Task> tasks = new ArrayList<>();
        RecyclerView myTasks = findViewById(R.id.recycle);
        myTasks.setLayoutManager(new LinearLayoutManager(this));
        myTasks.setAdapter(new TaskAdapter(tasks));


        @SuppressLint("NotifyDataSetChanged") Handler handler = new Handler(Looper.myLooper(), msg -> {

            Objects.requireNonNull(myTasks.getAdapter()).notifyDataSetChanged();
            return false;
        });

        Amplify.API.query(
                ModelQuery.list(TaskQl.class),
                response -> {
                    for (TaskQl todo : response.getData()) {
                        Task taskOrg = new Task(todo.getTitle(), todo.getBody(), todo.getState());
                        Log.i("graph testing", todo.getTitle());
                        tasks.add(taskOrg);
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );
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