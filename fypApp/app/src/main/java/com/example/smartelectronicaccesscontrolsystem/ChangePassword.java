package com.example.smartelectronicaccesscontrolsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.smartelectronicaccesscontrolsystem.Fragment.Profile_Fragment;

public class ChangePassword extends AppCompatActivity  implements View.OnClickListener{

    private EditText etoldPass,etnewPass,etretypePass;
    private ProgressBar progressBar;
    private Button btnChgPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        etoldPass = (EditText) findViewById(R.id.editTextOldpass);
        etnewPass = (EditText) findViewById(R.id.editTextNewPass);
        etretypePass = (EditText) findViewById(R.id.editTextRetypePass);
        btnChgPass = (Button) findViewById(R.id.btnChgPass);


        progressBar = (ProgressBar) findViewById(R.id.progressBarPass);


        btnChgPass.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        final String newpass,oldpass,retypepass;

        oldpass = etoldPass.getText().toString();
        newpass = etnewPass.getText().toString();
        retypepass= etretypePass.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        btnChgPass.setVisibility(View.GONE);


        if(oldpass.isEmpty()){
            etoldPass.setError("This field is empty! ! !");
            etoldPass.requestFocus();
            progressBar.setVisibility(View.GONE);
            btnChgPass.setVisibility(View.VISIBLE);
        }
        else if (newpass.isEmpty()){
            etnewPass.setError("This field is empty! ! !");
            etnewPass.requestFocus();
            progressBar.setVisibility(View.GONE);
            btnChgPass.setVisibility(View.VISIBLE);
        }
        else if(retypepass.isEmpty()){
            etretypePass.setError("This field is empty! ! ");
            etretypePass.requestFocus();
            progressBar.setVisibility(View.GONE);
            btnChgPass.setVisibility(View.VISIBLE);
        }
        else if(!newpass.equals(retypepass)){
            Toast.makeText(ChangePassword.this,"New Password and retype password not matched! !", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            btnChgPass.setVisibility(View.VISIBLE);
        }
        else {

            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldpass);



            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ChangePassword.this,"Password is changed! !",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    btnChgPass.setVisibility(View.VISIBLE);
                                    finish();
                                    //startActivity(new Intent(ChangePassword.this,home.class));
                                }
                                else {
                                    Toast.makeText(ChangePassword.this, "Failed to change password! !", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    btnChgPass.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(ChangePassword.this, "Failed to change password! !", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btnChgPass.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}

