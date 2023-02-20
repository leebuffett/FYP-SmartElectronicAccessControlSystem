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
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener{


    private EditText emailtext, passwordtext;
    private Button loginbutton;
    private FirebaseAuth myfirebase;
    private ProgressBar progressbar;
    private TextView forgotpass;

    private FirebaseAuth.AuthStateListener firebaseAuthListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        emailtext = (EditText) findViewById(R.id.emailtext);
        passwordtext = (EditText) findViewById(R.id.passwordtext);
        loginbutton =(Button) findViewById(R.id.loginButton);
        forgotpass = (TextView) findViewById(R.id.forgotpass);


        progressbar = (ProgressBar) findViewById(R.id.progressBarLogin);

        myfirebase= FirebaseAuth.getInstance();
        if (myfirebase.getCurrentUser()!=null){
            finish();
            Intent intent2 = new Intent(Login.this,home.class);
            startActivity(intent2);
        }
      forgotpass.setOnClickListener(this);
        loginbutton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                progressbar.setVisibility(v.VISIBLE);
                String loginEmail = emailtext.getText().toString();
                String loginPassword = passwordtext.getText().toString();
                if (loginEmail.isEmpty()) {
                    emailtext.setError("Please enter email correctly! ! !");
                    emailtext.requestFocus();
                    Toast.makeText(Login.this, "Fiels are empty! !", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                }
                else if(loginPassword.isEmpty()){
                    passwordtext.setError("Do not leave blank here! ! !");
                    passwordtext.requestFocus();
                    Toast.makeText(Login.this, "Fiels are empty! !", Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                }

                else if(!loginEmail.isEmpty() && !loginPassword.isEmpty()){
                    myfirebase.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                            if(task.isSuccessful()){
                                finish();
                                Toast.makeText(Login.this, "Success", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Login.this, home.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(Login.this, "Login Fail", Toast.LENGTH_LONG).show();
                                progressbar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else
                    Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();


                break;

            case R.id.forgotpass:
                startActivity(new Intent(Login.this, ResetPassword.class));
                break;

            /*case R.id.btnTest:
                Intent intent2 = new Intent(MainActivity.this,home.class);
                startActivity(intent2);
                break;*/

        }
    }





}
