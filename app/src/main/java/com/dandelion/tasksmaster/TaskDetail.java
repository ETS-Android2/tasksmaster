package com.dandelion.tasksmaster;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;


public class TaskDetail extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();

        TextView titleText = findViewById(R.id.taskTitleView);
        String title = intent.getExtras().getString("title");
        titleText.setText(title);

        String body = intent.getExtras().getString("body");
        TextView taskBody = findViewById(R.id.taskBodyField);
        taskBody.setText(body);

        String state = intent.getExtras().getString("state");
        TextView taskState = findViewById(R.id.taskStateField);
        taskState.setText(state);

        String url = intent.getExtras().getString("image");
        ImageView image = findViewById(R.id.storeImg);
        Picasso.get().load(url).into(image);

        Button goHomeButtonDetail = findViewById(R.id.homeButtonDetail);
        goHomeButtonDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Intent goHomeTasks = new Intent(TaskDetail.this, MainActivity.class);
                startActivity(goHomeTasks);
            }
        });
    }
}

