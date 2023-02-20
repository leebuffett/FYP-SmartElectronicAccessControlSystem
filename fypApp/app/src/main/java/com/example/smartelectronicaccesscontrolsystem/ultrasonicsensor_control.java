package com.example.smartelectronicaccesscontrolsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ultrasonicsensor_control extends AppCompatActivity {

    SeekBar sensorSeekbar;
    TextView distance_status;
    DatabaseReference reference;
    String status = "10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ultrasonicsensor_control);

        //database reference
        reference = FirebaseDatabase.getInstance().getReference().child("Sensors").child("Ultrasonic");

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ultrasonic Sensor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get item id
        sensorSeekbar = (SeekBar) findViewById(R.id.sensor_seekBar);
        distance_status = (TextView) findViewById(R.id.distance_status);

        //show status
        showStatus();

        //when seek bar is used
        sensorSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance_status.setText(progress+" cm");
                status = String.valueOf(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                reference.setValue(status);
            }
        });
    }

    public void showStatus(){
        reference = FirebaseDatabase.getInstance().getReference().child("Sensors").child("Ultrasonic");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status=dataSnapshot.getValue().toString();
                distance_status.setText(status+" cm");
                sensorSeekbar.setProgress(Integer.valueOf(status));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

