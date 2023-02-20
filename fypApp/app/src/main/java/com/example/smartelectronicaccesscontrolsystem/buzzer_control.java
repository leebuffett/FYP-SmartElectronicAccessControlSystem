package com.example.smartelectronicaccesscontrolsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class buzzer_control extends AppCompatActivity implements View.OnClickListener{


    TextView alarm_status,alarm_textview;
    Button alarm_button;
    DatabaseReference reference;
    String status = "1";
    CircleImageView alarmImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buzzer_control);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Alarm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get item id
        alarm_status = (TextView) findViewById(R.id.alarm_status);
        alarm_textview = (TextView) findViewById(R.id.alarm_textView);
        alarm_button = (Button) findViewById(R.id.alarm_button);
        alarmImg = (CircleImageView) findViewById(R.id.alarm_img);

        //show door status from firebase
        showStatus();

        //when door_button clicked
        alarm_button.setOnClickListener(this);
    }

    public void showStatus(){
        reference = FirebaseDatabase.getInstance().getReference().child("Sensors").child("Buzzer");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status=dataSnapshot.getValue().toString();

                if(status.equals("1")){
                    alarmImg.setBackgroundResource(R.mipmap.alarm_img);
                    alarm_status.setText("Triggered");
                    alarm_textview.setText("Tap to turn off the alarm");
                    alarm_button.setText("Turn Off");
                }
                else if(status.equals("0")){
                    alarmImg.setBackgroundResource(R.mipmap.alarm_off_img);
                    alarm_status.setText("Off");
                    alarm_button.setText("Turn On");
                    alarm_textview.setText("Tap to trigger the alarm");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        reference = FirebaseDatabase.getInstance().getReference().child("Sensors").child("Buzzer");
        switch (status){
            case "1":
                reference.setValue(0);
                break;
            case "0":
                reference.setValue(1);
                break;
            default:break;
        }

    }
}

