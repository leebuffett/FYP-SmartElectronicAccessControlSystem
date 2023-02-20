package com.example.smartelectronicaccesscontrolsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartelectronicaccesscontrolsystem.Model.Notification;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class NotificationDetails extends AppCompatActivity {


    private TextView notifyTitle,notifyDesc,notifyDate, notifyTime;
    private ImageView notifyImg;
    private DatabaseReference databaseReference;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_details);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Message Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        notifyTitle=(TextView) findViewById(R.id.notifyTitle);
        notifyDesc=(TextView) findViewById(R.id.notifyDescription);
        notifyDate=(TextView) findViewById(R.id.notifyDate);
        notifyTime=(TextView) findViewById(R.id.notifyTime);
        notifyImg = (ImageView) findViewById(R.id.notifyimg);

        intent = getIntent();
        String notifyId = intent.getStringExtra("notifyid");
        String day = intent.getStringExtra("day");
        String month = intent.getStringExtra("month");
        String year = intent.getStringExtra("year");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Notification").child(year).child(month).child(day).child(notifyId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Notification notification = dataSnapshot.getValue(Notification.class);
                Picasso.get().load(notification.getImage()).into(notifyImg);
                notifyTitle.setText(notification.getTitle());
                notifyTime.setText(notification.getTime());
                notifyDate.setText(notification.getDate());
                notifyDesc.setText(notification.getDescription());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
