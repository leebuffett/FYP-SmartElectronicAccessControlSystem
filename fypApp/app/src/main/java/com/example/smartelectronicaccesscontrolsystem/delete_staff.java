package com.example.smartelectronicaccesscontrolsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class delete_staff extends AppCompatActivity {

    Button deleteBtn;
    ProgressBar deleteProgress;
    TextView nameStaff, dobStaff, ageStaff, emailStaff,genderStaff,ccStaff, phoneStaff;
    CircleImageView StaffImage;
    Intent intent;
    String name, phone, cc, bod, age, gender, email, image, id;

    //storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_staff);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Delete Staff");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get item id
        StaffImage = (CircleImageView) findViewById(R.id.Staff_img);
        deleteBtn = (Button) findViewById(R.id.delete_staff);
        deleteProgress = findViewById(R.id.deleteStaffProgress);
        nameStaff = (TextView) findViewById(R.id.nameStaff);
        dobStaff = (TextView) findViewById(R.id.dobStaff);
        ageStaff = (TextView) findViewById(R.id.ageStaff);
        emailStaff = (TextView) findViewById(R.id.emailStaff);
        genderStaff = (TextView) findViewById(R.id.genderStaff);
        phoneStaff = (TextView) findViewById(R.id.phoneStaff);

        //get value from previous activity, and will be shown in the layout
        intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        cc = intent.getStringExtra("cc");
        bod = intent.getStringExtra("bod");
        age = intent.getStringExtra("age");
        gender = intent.getStringExtra("gender");
        email = intent.getStringExtra("email");
        image = intent.getStringExtra("image");
        id = intent.getStringExtra("staffId");

//        //set phone number
        String phonenumber = cc + phone;

        //Set value for textview
        nameStaff.setText(name);
        dobStaff.setText(bod);
        ageStaff.setText(age);
        emailStaff.setText(email);
        genderStaff.setText(gender);
        phoneStaff.setText(phonenumber);

        //set image
        Picasso.get().load(image).into(StaffImage);

    }

    //When update button pressed, this function will called.
    public void DeleteStaffBtn(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(delete_staff.this);
        builder.setMessage("Confirm Delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //delete staff from firebase
                        deleteProgress.setVisibility(View.VISIBLE);
                        deleteBtn.setVisibility(View.GONE);

                        //remove pic from firebase storage
                        removeImg();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("staff").child(id);
                        reference.removeValue();

                        //call delete_train_model to communicate with web API, retrain the face model
                        delete_train_model();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(delete_staff.this,"Staff Deleted",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        },2000);
                    }
                })
                .setNegativeButton("Cancel",null);

        AlertDialog alert = builder.create();
        alert.show();



    }

    private void removeImg() {
        String storageUrl = "staffs/"+name+".jpg";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(storageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(delete_staff.this, "File deleted", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!

            }
        });


    }

    //call python to retrain the model (pass parameter --> name)
    private void delete_train_model() {

        //pass name parameter to web api
        String deleteName = name;

        AsyncHttpClient client = new AsyncHttpClient();


        client.get("http://210.195.151.15/cgi-bin/fyp/a.php?func=delete&param1="+deleteName+"&param2=None", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
             //   Toast.makeText(delete_staff.this, "web browser", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }
}

