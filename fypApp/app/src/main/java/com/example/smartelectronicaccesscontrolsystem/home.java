package com.example.smartelectronicaccesscontrolsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.smartelectronicaccesscontrolsystem.Fragment.HomeFragment;
import com.example.smartelectronicaccesscontrolsystem.Fragment.Profile_Fragment;
import com.example.smartelectronicaccesscontrolsystem.Fragment.Notification_Fragment;
import com.example.smartelectronicaccesscontrolsystem.Model.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class home extends AppCompatActivity {
    //Button btn2;
    private BottomNavigationView btmView;
    // private int backpressed = 0;
    private FrameLayout navFrame;
    private HomeFragment homefrag;
    private Profile_Fragment profileFragment;
    private Notification_Fragment notificationFragment;
    private long backpressedTime;
    private Toast backToast;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( home.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);

                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("Token");
                //       Map<String, staff> staffClass = new HashMap<>();
                HashMap<String, Object> result = new HashMap<>();
                result.put(newToken,new Token(newToken));
                reference.updateChildren(result);
            }
        });

        //  Toast.makeText(home.this,"Halo",Toast.LENGTH_LONG).show();

        homefrag = new HomeFragment();
        profileFragment = new Profile_Fragment();
        notificationFragment = new Notification_Fragment();

        setFragment(homefrag);
        btmView = (BottomNavigationView) findViewById(R.id.navigation_bottom);
        navFrame = (FrameLayout) findViewById(R.id.frameLayout);

        btmView.setSelectedItemId(R.id.nav_home);
        btmView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        setFragment(homefrag);
                        return true;

                    case R.id.nav_notification:
                        setFragment(notificationFragment);
                        return true;

                    case R.id.nav_profile:
                        //startActivity(new Intent(getApplicationContext(),Main2Activity.class));
                        //overridePendingTransition(0,0);
                        setFragment(profileFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed(){

        if(backpressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else{
            backToast = Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backpressedTime = System.currentTimeMillis();

    }
}
