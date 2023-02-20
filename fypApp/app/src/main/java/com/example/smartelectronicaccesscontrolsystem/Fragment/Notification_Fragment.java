package com.example.smartelectronicaccesscontrolsystem.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartelectronicaccesscontrolsystem.Adapter.NotificationAdapter;
import com.example.smartelectronicaccesscontrolsystem.CheckNetwork;
import com.example.smartelectronicaccesscontrolsystem.Model.Notification;
import com.example.smartelectronicaccesscontrolsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class Notification_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> mnotify;
    private ArrayList<String> key;



    Calendar cal = Calendar.getInstance();
    private int day = cal.get(Calendar.DAY_OF_MONTH);
    private int year = cal.get(Calendar.YEAR);
    private int count=1;

    SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    private String month_name = month_date.format(Calendar.getInstance().getTime());

    SimpleDateFormat day_of_month_format = new SimpleDateFormat("dd", Locale.ENGLISH);
    private String day_of_month = day_of_month_format.format(Calendar.getInstance().getTime());

    CheckNetwork checkNetwork = new CheckNetwork();

    private TextView noNotification;

    public Notification_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


            final View view = inflater.inflate(R.layout.fragment_notification, container, false);

            Button offlineBtn = (Button) view.findViewById(R.id.offlineBtn);
            offlineBtn.setVisibility(View.GONE);

            TextView networkTxt=(TextView) view.findViewById(R.id.networktxt);
            networkTxt.setVisibility(View.GONE);



            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Notification");

        if (checkNetwork.isInternetAvailable(getActivity()))  //if connection available
        {

             noNotification = (TextView) view.findViewById(R.id.noNotification);

            recyclerView = view.findViewById(R.id.recycle_view);
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            mnotify = new ArrayList<>();
            key= new ArrayList<>();

            readnotify();



        }
        else{
            networkTxt.setVisibility(View.VISIBLE);
            offlineBtn.setVisibility(View.VISIBLE);
            offlineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager()
                            .beginTransaction()
                            .detach(Notification_Fragment.this)
                            .attach(Notification_Fragment.this)
                            .commit();
                }
            });

        }


     return view;
    }

    public void readnotify(){

      FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

      final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notification").child(String.valueOf(year)).child(month_name).child(String.valueOf(day_of_month));
        //final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Notification");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mnotify.clear();
//                key = null; //clear array
                key.clear();
                Log.d("arraytest", String.valueOf(key));
//                int counter =0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Notification notification = snapshot.getValue(Notification.class);
//                    key[counter]=String.valueOf(snapshot.getKey());
                    key.add(String.valueOf(snapshot.getKey()));
                    mnotify.add(notification);
//                    counter++;
                }
                Collections.sort(mnotify, new Comparator<Notification>() {
                    @Override
                    public int compare(Notification o1, Notification o2) {
                        return o2.getId().compareTo(o1.getId());
                    }
                });
                Collections.sort(key, Collections.reverseOrder());
                Log.d("arraytest", String.valueOf(key));

                notificationAdapter = new NotificationAdapter(getContext(),mnotify, key);
                recyclerView.setAdapter(notificationAdapter);

                count=notificationAdapter.getItemCount();
                if (count == 0) {
                    noNotification.setVisibility(View.VISIBLE);
                    noNotification.setText("There is no notification for today ! !");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
