package com.dandelion.tasksmaster;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskQl;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class AddTask extends AppCompatActivity {
    AppDatabase appDatabase;
    TaskDao taskDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());


            Log.i("Main Activity", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("Main Activity", "Could not initialize Amplify", error);
        }

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
        });

        Button upload = findViewById(R.id.addImage);
        upload.setOnClickListener(view -> {

            uploadFile();
            Toast.makeText(getApplicationContext(),  "image uploaded", Toast.LENGTH_SHORT).show();

        });
    }

    private void uploadFile() {
        File exampleFile = new File(getApplicationContext().getFilesDir(), "ExampleKey");

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(exampleFile));
            writer.append("Example file contents");
            writer.close();
        } catch (Exception exception) {
            Log.e("MyAmplifyApp", "Upload failed", exception);
        }

        Amplify.Storage.uploadFile(
                "ExampleKey",
                exampleFile,
                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );
    }
}

