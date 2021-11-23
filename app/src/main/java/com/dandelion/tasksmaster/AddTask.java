package com.dandelion.tasksmaster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.TaskQl;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;

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
    FusedLocationProviderClient mFusedLocationClient;
    Double longitude;
    Double latitude;

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.i("add task", "The location is => " + mLastLocation);
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
                    .latitude(latitude)
                    .longitude(longitude)
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

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        ImageView imageView = findViewById(R.id.imageView3);
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (uri != null) {
                    imageView.setImageURI((uri));
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {

            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(10);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this); // this may or may not be needed
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;


    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 24);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLastLocation();
    }
}


