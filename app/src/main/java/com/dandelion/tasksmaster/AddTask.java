package com.dandelion.tasksmaster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.TaskQl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AddTask extends AppCompatActivity {
    AppDatabase appDatabase;
    TaskDao taskDao;

    private String imageName;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        try {
            Amplify.addPlugin(new AWSDataStorePlugin()); // stores records locally
            Amplify.addPlugin(new AWSApiPlugin()); // stores things in DynamoDB and allows us to perform GraphQL queries
            Amplify.addPlugin(new AWSCognitoAuthPlugin()); // Add this line, to include the Auth plugin.
            Amplify.configure(getApplicationContext());

            Log.i("Add tasks", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("Add tasks", "Could not initialize Amplify", error);
        }

        EditText taskTitle = findViewById(R.id.taskTitleField);
        EditText taskBody = findViewById(R.id.taskBodyField);
        EditText taskState = findViewById(R.id.taskStateField);

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        taskDao = appDatabase.taskDao();

        Button addTaskBtn = findViewById(R.id.saveTaskBtn);
        addTaskBtn.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "task added", Toast.LENGTH_SHORT).show();
            String title = taskTitle.getText().toString();
            String body = taskBody.getText().toString();
            String state = taskState.getText().toString();

            TaskQl task = TaskQl.builder()
                    .title(title)
                    .body(body)
                    .state(state)
                    .image(imageName)
                    .build();

            Amplify.API.mutate(
                    ModelMutation.create(task),
                    response -> Log.i("TasksApp", "add task with id" + response.getData().getId()),
                    error -> Log.e("TasksApp", "Create failed", error)
            );
            Intent intent = new Intent(AddTask.this, MainActivity.class);
            startActivity(intent);
        });


        Button home = findViewById(R.id.homeAddTask);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(AddTask.this, MainActivity.class);
            startActivity(intent);
        });

        Button addPhoto = findViewById(R.id.addImage);
        addPhoto.setOnClickListener(v ->
        {
            if (uri != null) {
                try {
                    InputStream exampleInputStream = getContentResolver().openInputStream(uri);
                    Amplify.Storage.uploadInputStream(
                            imageName,
                            exampleInputStream,
                            result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                            storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
                    );
                } catch (FileNotFoundException error) {
                    Log.e("MyAmplifyApp", "Could not find file to open for input stream.", error);
                }
            }

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent = Intent.createChooser(intent, "intent");
            startActivityForResult(intent, 44);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent dat) {
        super.onActivityResult(requestCode, resultCode, dat);

        File uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFileCopied");
        try {

            assert dat != null;
            InputStream exampleInputStream = getContentResolver().openInputStream(dat.getData());
            OutputStream outputStream = new FileOutputStream(uploadFile);
            imageName = dat.getData().toString();
            byte[] buff = new byte[1024];
            int length;
            while ((length = exampleInputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }

            exampleInputStream.close();
            outputStream.close();
            Amplify.Storage.uploadFile(
                    "image",
                    uploadFile,
                    result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                    storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


