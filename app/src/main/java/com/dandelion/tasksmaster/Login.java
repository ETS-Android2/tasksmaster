package com.dandelion.tasksmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText email = findViewById(R.id.signinEmail);
        EditText password = findViewById(R.id.signinPassword);
        EditText code = findViewById(R.id.confirmCode);

        try {
            Amplify.addPlugin(new AWSApiPlugin());
            // Add this line, to include the Auth plugin.
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());


            Log.i("Main Activity", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("Main Activity", "Could not initialize Amplify", error);
        }

        Button login = findViewById(R.id.loginBtn);
        login.setOnClickListener(v -> {
            Amplify.Auth.signIn(
                    password.getText().toString(),
                    email.getText().toString(),
                    result -> Log.i("AuthQuickstart", result.isSignInComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
                    error -> Log.e("AuthQuickstart", error.toString())
            );

            Intent goHome = new Intent(Login.this, MainActivity.class);
            startActivity(goHome);
        });

    }
}


