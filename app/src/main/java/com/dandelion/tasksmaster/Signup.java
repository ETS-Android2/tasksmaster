package com.dandelion.tasksmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Signup.this);
        SharedPreferences.Editor SharedPreferencesEditor = sharedPreferences.edit();

        EditText username = findViewById(R.id.username);

        EditText email = findViewById(R.id.signupEmail);
        Log.i("signup", "signup email" + email.getText().toString());

        EditText password = findViewById(R.id.signupPassword);
        Log.i("signup", "signup password " + password.getText().toString() );

        try {
            Amplify.addPlugin(new AWSApiPlugin());
            // Add this line, to include the Auth plugin.
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());


            Log.i("Main Activity", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("Main Activity", "Could not initialize Amplify", error);
        }

        signUpRecord();

        Button signup = findViewById(R.id.signupButton);
        signup.setOnClickListener(v -> {
            Amplify.Auth.fetchAuthSession(
                    result -> Log.i("AmplifyQuickstart", result.toString()),
                    error -> Log.e("AmplifyQuickstart", error.toString())
            );
            AuthSignUpOptions options = AuthSignUpOptions.builder()
                    .userAttribute(AuthUserAttributeKey.email(), email.getText().toString())
                    .build();
            Log.i("Signup", "onCreate: nawal" + email.getText().toString());
            Amplify.Auth.signUp(email.getText().toString(), password.getText().toString(), options,
                    result -> Log.i("AuthQuickStart", "Result: " + result.toString()),
                    error -> Log.e("AuthQuickStart", "Sign up failed", error)
            );


            Intent intent = new Intent(Signup.this, Confirm.class);
            startActivity(intent);

            SharedPreferencesEditor.putString("username", username.getText().toString());
            SharedPreferencesEditor.apply();
            startActivity(intent);
        });
    }

    private void signUpRecord() {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .name("Logged In Button Pressed")
                .addProperty("username", true)
                .build();

        Amplify.Analytics.recordEvent(event);
    }
}