package com.example.smartelectronicaccesscontrolsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class hardware_control extends AppCompatActivity implements View.OnClickListener{

    CardView LED_card, door_lock_card, ultrasonic_card, alarmCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hardware_control);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hardware");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LED_card = (CardView) findViewById(R.id.LED_card);
        door_lock_card = (CardView) findViewById(R.id.door_lock_card);
        ultrasonic_card = (CardView) findViewById(R.id.ultrasonic_card);
        alarmCard = (CardView) findViewById(R.id.alarm_card);


        LED_card.setOnClickListener(this);
        door_lock_card.setOnClickListener(this);
        ultrasonic_card.setOnClickListener(this);
        alarmCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.door_lock_card:
//                i = new Intent(hardware_control.this, add_employee.class);
//                i.putExtra("activity","addStaff");
//                startActivity(i);
                startActivity(new Intent(hardware_control.this, door_control.class));
                break;
            case R.id.LED_card:
//                i = new Intent(hardware_control.this, edit_employee.class);
//                i.putExtra("activityname","edit");
//                startActivity(i);
                startActivity(new Intent(hardware_control.this,led_control.class));
                break;
            case R.id.ultrasonic_card:
//                i = new Intent(hardware_control.this, edit_employee.class);
//                i.putExtra("activityname","delete");
//                startActivity(i);
                startActivity(new Intent(hardware_control.this,ultrasonicsensor_control.class));
                break;
            case R.id.alarm_card:
//                i = new Intent(hardware_control.this, edit_employee.class);
//                i.putExtra("activityname","delete");
//                startActivity(i);
                startActivity(new Intent(hardware_control.this,buzzer_control.class));
                break;
            default:break;
        }
    }
}
