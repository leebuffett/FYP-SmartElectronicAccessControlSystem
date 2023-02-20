package com.example.smartelectronicaccesscontrolsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartelectronicaccesscontrolsystem.Adapter.NotificationAdapter;
import com.example.smartelectronicaccesscontrolsystem.Adapter.StaffAdapter;
import com.example.smartelectronicaccesscontrolsystem.Fragment.Notification_Fragment;
import com.example.smartelectronicaccesscontrolsystem.Model.Notification;
import com.example.smartelectronicaccesscontrolsystem.Model.staff;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class staff_list extends AppCompatActivity {


    private RecyclerView recyclerView;
    private StaffAdapter staffAdapter;
    private List<staff> allStaff;
    private Intent intent;
    private int count=1;
    private String staffActivity;

    CheckNetwork checkNetwork = new CheckNetwork();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_list);

        //used to know which activity come from
        intent = getIntent();
        staffActivity = intent.getStringExtra("activityname");

        Button offlineBtn = (Button) findViewById(R.id.offlineBtn);
        offlineBtn.setVisibility(View.GONE);

        TextView networkTxt=(TextView) findViewById(R.id.networktxt);
        networkTxt.setVisibility(View.GONE);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Staff List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (checkNetwork.isInternetAvailable(this))  //if connection available
        {
            TextView noNotification = (TextView) findViewById(R.id.noNotification);

            recyclerView = findViewById(R.id.recycle_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            allStaff = new ArrayList<>();

            readnotify();

            if (count == 0) {
                noNotification.setVisibility(View.VISIBLE);
                noNotification.setText("There is no staff here ! !");
            }
        }
        else{
            networkTxt.setVisibility(View.VISIBLE);
            offlineBtn.setVisibility(View.VISIBLE);
            offlineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate(); //when button is clicked, refresh the activity page
                }
            });
        }
    }

    public void readnotify(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("staff");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allStaff.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    staff empStaff = snapshot.getValue(staff.class);
                    allStaff.add(empStaff);
                }
                Collections.sort(allStaff, new Comparator<staff>() {
                    @Override
                    public int compare(staff o1, staff o2) {
                        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                    }
                });


                staffAdapter = new StaffAdapter(getApplication().getApplicationContext(),allStaff,staffActivity);
                recyclerView.setAdapter(staffAdapter);

                count=staffAdapter.getItemCount();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

