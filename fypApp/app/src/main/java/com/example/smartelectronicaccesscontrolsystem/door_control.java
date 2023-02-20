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

public class door_control extends AppCompatActivity implements View.OnClickListener {

    TextView door_status,door_textview;
    Button door_button;
    DatabaseReference reference;
    String status = "0";
    CircleImageView doorImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.door_control);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Door Lock");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get item id
        door_status = (TextView) findViewById(R.id.door_status);
        door_textview = (TextView) findViewById(R.id.door_textView);
        door_button = (Button) findViewById(R.id.door_button);
        doorImg = (CircleImageView) findViewById(R.id.door_img);

        //show door status from firebase
        showStatus();

        //when door_button clicked
        door_button.setOnClickListener(this);
    }

    public void showStatus(){
        reference = FirebaseDatabase.getInstance().getReference().child("Sensors").child("Solenoid Lock");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status=dataSnapshot.getValue().toString();


                if(status.equals("0")){
                    doorImg.setBackgroundResource(R.mipmap.door_lock_img);
                    door_textview.setText("Tap to unlock");
                    door_button.setText("Unlock");
                    door_status.setText("Locked");
                }
                else if(status.equals("1")){
                    doorImg.setBackgroundResource(R.mipmap.door_unlock_img);
                    door_textview.setText("Tap to lock");
                    door_button.setText("Lock");
                    door_status.setText("Unlocked");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        reference = FirebaseDatabase.getInstance().getReference().child("Sensors").child("Solenoid Lock");
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
