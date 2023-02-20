package com.example.smartelectronicaccesscontrolsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.smartelectronicaccesscontrolsystem.Model.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class Logout extends AppCompatActivity {

//    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    //   private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout);


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( Logout.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Token").child(newToken);
                reference.removeValue();
            }
        });

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(Logout.this, Login.class));

            }
        },5000);

    }
}

