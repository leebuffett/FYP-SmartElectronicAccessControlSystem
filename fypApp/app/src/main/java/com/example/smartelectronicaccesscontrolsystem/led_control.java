package com.example.smartelectronicaccesscontrolsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class led_control extends AppCompatActivity implements View.OnClickListener{

    Button led_button;
    TextView led_status,led_textview;
    DatabaseReference reference;
    String status = "0";
    CircleImageView led_img;
//    String halo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.led_control);

        //database reference
        reference = FirebaseDatabase.getInstance().getReference().child("Sensors").child("LED");

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("LED");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get item id
        led_button = (Button) findViewById(R.id.led_button);
        led_status = (TextView) findViewById(R.id.led_status);
        led_img = (CircleImageView) findViewById(R.id.led_img);
        led_textview = (TextView) findViewById(R.id.led_textView);

        //show status
        showStatus();

        //when seek bar is used
        led_button.setOnClickListener(this);
    }

    public void showStatus(){
        reference = FirebaseDatabase.getInstance().getReference().child("Sensors").child("LED");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status=dataSnapshot.getValue().toString();

                if(status.equals("1")){
                    led_img.setBackgroundResource(R.mipmap.led_on_img);
                    led_status.setText("On");
                    led_textview.setText("Tap to turn off the LED");
                    led_button.setText("Turn Off");
                }
                else if(status.equals("0")){
                    led_img.setBackgroundResource(R.mipmap.led_off_img);
                    led_status.setText("Off");
                    led_textview.setText("Tap to turn on the LED");
                    led_button.setText("Turn On");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        halo=status;
//        led_status.setText(halo);
    }

    @Override
    public void onClick(View v) {
        reference = FirebaseDatabase.getInstance().getReference().child("Sensors").child("LED");
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
