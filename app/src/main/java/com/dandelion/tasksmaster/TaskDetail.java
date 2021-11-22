package com.dandelion.tasksmaster;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;


public class TaskDetail extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

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

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }

        Button goHomeButtonDetail = findViewById(R.id.homeButtonDetail);
        goHomeButtonDetail.setOnClickListener(V -> {
            Intent goHomeTasks = new Intent(TaskDetail.this, MainActivity.class);
            startActivity(goHomeTasks);
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Intent intent = getIntent();
        LatLng myLocation = new LatLng(getIntent().getDoubleExtra("lat", intent.getFloatExtra("lat",0)),
                getIntent().getDoubleExtra("lon", intent.getFloatExtra("lon",0)));
        googleMap.addMarker(new MarkerOptions().position(myLocation).title("My Location In Jordan"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }

}

