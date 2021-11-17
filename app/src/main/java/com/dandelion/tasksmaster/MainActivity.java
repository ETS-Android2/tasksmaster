package com.dandelion.tasksmaster;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amazonaws.mobileconnectors.pinpoint.targeting.TargetingClient;
import com.amazonaws.mobileconnectors.pinpoint.targeting.endpointProfile.EndpointProfile;
import com.amazonaws.mobileconnectors.pinpoint.targeting.endpointProfile.EndpointProfileUser;
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.TaskQl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    String userName = getSharedPreferences("pref", MODE_PRIVATE).getString("userInfo", "no user info");

    public static final String TAG = MainActivity.class.getSimpleName();

    private static PinpointManager pinpointManager;

    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", userStateDetails.getUserState().toString());
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            final String token = task.getResult();
                            Log.d(TAG, "Registering push notifications token: " + token);
                            pinpointManager.getNotificationClient().registerDeviceToken(token);
                        }
                    });
        }
        return pinpointManager;
    }

    public void assignUserIdToEndpoint() {
        TargetingClient targetingClient = pinpointManager.getTargetingClient();
        EndpointProfile endpointProfile = targetingClient.currentEndpoint();
        EndpointProfileUser endpointProfileUser = new EndpointProfileUser();
        endpointProfileUser.setUserId("UserIdValue");
        endpointProfile.setUser(endpointProfileUser);
        targetingClient.updateEndpointProfile(endpointProfile);
        Log.d(TAG, "Assigned user ID " + endpointProfileUser.getUserId() +
                " to endpoint " + endpointProfile.getEndpointId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize PinpointManager
        getPinpointManager(getApplicationContext());

        assignUserIdToEndpoint();

        Button addTask = findViewById(R.id.addTaskBtn);
        addTask.setOnClickListener(v -> {
            addTaskRecord();
            Intent goToAddTask = new Intent(MainActivity.this, AddTask.class);
            startActivity(goToAddTask);
        });

        Button allTasks = findViewById(R.id.allTasksBtn);
        allTasks.setOnClickListener(v -> {
            allTasksRecord();
            Intent goToAllTasks = new Intent(MainActivity.this, AllTasks.class);
            startActivity(goToAllTasks);
        });

        Button settings = findViewById(R.id.SettingsBtn);
        settings.setOnClickListener(v -> {
            settingsRecord();
            Intent goToSettings = new Intent(MainActivity.this, Settings.class);
            startActivity(goToSettings);
        });

        Amplify.Auth.signInWithWebUI(
                this,
                result -> Log.i("AuthQuickStart", result.toString()),
                error -> Log.e("AuthQuickStart", error.toString())
        );

        Amplify.Auth.fetchAuthSession(
                result -> Log.i("AmplifyQuickstart", result.toString()),
                error -> Log.e("AmplifyQuickstart", error.toString())
        );

        List<UserTasks> userTasks = new ArrayList<>();
        RecyclerView myTasks = findViewById(R.id.recycle);
        myTasks.setLayoutManager(new LinearLayoutManager(this));
        myTasks.setAdapter(new TaskAdapter(userTasks));


        @SuppressLint("NotifyDataSetChanged") Handler handler = new Handler(Looper.myLooper(), msg -> {
            Objects.requireNonNull(myTasks.getAdapter()).notifyDataSetChanged();
            return false;
        });

        Amplify.API.query(
                ModelQuery.list(TaskQl.class),
                response -> {
                    for (TaskQl todo : response.getData()) {
                        UserTasks userTasksOrg = new UserTasks(todo.getTitle(), todo.getBody(), todo.getState(), todo.getImage());
                        Log.i("graph testing", todo.getTitle());
                        userTasks.add(userTasksOrg);
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );

        Button signoutButton = findViewById(R.id.logoutBtn);
        signoutButton.setOnClickListener(V -> {
            Amplify.Auth.signOut(
                    () -> Log.i("AuthQuickstart", "Signed out successfully"),
                    error -> Log.e("AuthQuickstart", error.toString())

            );

            final Handler handler1 = new Handler();
            handler1.postDelayed(this::recreate, 3000);
        });

        Button signupButton = findViewById(R.id.signupMain);
        signupButton.setOnClickListener(v -> {
            Intent goToSignup = new Intent(MainActivity.this, Signup.class);
            startActivity(goToSignup);
        });

        Button signinButton = findViewById(R.id.signinMain);
        signinButton.setOnClickListener(v -> {
            Intent goToSignin = new Intent(MainActivity.this, Login.class);
            startActivity(goToSignin);
        });
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

    private void addTaskRecord() {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("Add Task Button Pressed")
                .addProperty("UserName", userName)
                .build();
        Amplify.Analytics.recordEvent(event);
    }

    private void allTasksRecord() {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("All Tasks Button Pressed")
                .addProperty("UserName", userName)
                .build();
        Amplify.Analytics.recordEvent(event);
    }


    private void settingsRecord(){
        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("Settings Button Pressed")
                .addProperty("UserName", userName)
                .build();

        Amplify.Analytics.recordEvent(event);
    }
}