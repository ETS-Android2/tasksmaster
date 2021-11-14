package com.dandelion.tasksmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // save
        Button saveName = findViewById(R.id.usernameSave);
        saveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this , MainActivity.class);
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();
                // share preferences
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                SharedPreferences.Editor SharedPreferencesEditor = sharedPreferences.edit();
                // get name
                EditText userNameField = findViewById(R.id.usernameField);
                String username = userNameField.getText().toString();
                //save to shared
                SharedPreferencesEditor.putString("username", username);
                SharedPreferencesEditor.apply();
                startActivity(intent);


            }
        });

    }
}