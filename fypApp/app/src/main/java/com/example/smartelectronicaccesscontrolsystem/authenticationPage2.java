package com.example.smartelectronicaccesscontrolsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.smartelectronicaccesscontrolsystem.Model.Admin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


public class authenticationPage2 extends AppCompatActivity {

    private static String email;
    private String username, phoneNumber, countryCode,addUserUrl,addedUserId;
    private String qrCodeCallUrl;
    private String qrCodePath;

    public static final String API_KEY = "your_api_key";


    public static DatabaseReference adminTable = FirebaseDatabase.getInstance().getReference("Admin");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_page2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Authentication App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        connectApi();


    }
    public void connectApi(){


        adminTable.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /** 1.Get user's creds! phone number included.. **/
                email = (dataSnapshot.getValue(Admin.class)).getAdminEmail();
                username = (dataSnapshot.getValue(Admin.class)).getAdminName();
                phoneNumber = (dataSnapshot.getValue(Admin.class)).getPhoneNumber();
                countryCode = (dataSnapshot.getValue(Admin.class)).getPhoneCountryCode();
                addUserUrl  = "https://api.authy.com/protected/json/users/new?user[email]="+email
                        +"&user[cellphone]="+phoneNumber
                        +"&user[country_code]="+countryCode+"&api_key=ROG6QdAs7hw637PJMMqqe4DIwPKMCd83";

                /** 2.Add the user to the Authy API **/
                // post call for Authy api to add a user | response contains the added user's id
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,addUserUrl,null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();
                                try {
                                    JsonObject addedUser = gson.fromJson(response.getString("user"),JsonObject.class);
                                    addedUserId = (addedUser.get("id")).getAsString();

                                    //load qr
                                    loadqr(addedUserId);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("ERROR! ",error.getMessage());
                            }
                        });

                (AppSingleton.getInstance(getApplicationContext()).getRequestQueue()).add(jsObjRequest);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });



        // startActivity(new Intent(this, authenticationPage1.class));


    }

    public void loadqr(final String userid){

     qrCodeCallUrl="https://api.authy.com/protected/json/users/"+userid+"/secret?api_key=ROG6QdAs7hw637PJMMqqe4DIwPKMCd83";

        /** call authy api to get qr code **/
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,qrCodeCallUrl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            qrCodePath = response.getString("qr_code");
                            /** set the imageView's src **/
                            ImageView qrCodeImgVw = findViewById(R.id.qrcodeImg);

                            Picasso.get().load(qrCodePath).into(qrCodeImgVw);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR! ",error.getMessage());
                    }
                });


        // (AppSingleton.getInstance(getApplicationContext()).getRequestQueue()).add(jsObjRequest);
        AppSingleton.getInstance(this).addToRequestQueue(jsObjRequest);

        Button confrimAuth = (Button) findViewById(R.id.confrimAuth);
        confrimAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation(((EditText)findViewById(R.id.codeTxt)).getText().toString(),userid,authenticationPage2.this);
            }
        });

    }

    public void validation(String codeEnter,final String userId,final Context context){

        String codeValidationUrl="https://api.authy.com/protected/json/verify/"+codeEnter+"/"+userId+"?api_key=ROG6QdAs7hw637PJMMqqe4DIwPKMCd83";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,codeValidationUrl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if((response.getString("token")).equals("is valid")){
//                                            removeuser(userId);
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Admin").child(FirebaseAuth.getInstance().getUid());
                                databaseReference.child("twoFAStatus").setValue(true);
                                Toast.makeText(authenticationPage2.this, "Successfully enable 2FA", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(authenticationPage2.this, home.class));}
                            else
                                Toast.makeText(authenticationPage2.this, "You typed a wrong code!", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(authenticationPage2.this, "Error! ! !", Toast.LENGTH_LONG).show();
                    }
                });
        (AppSingleton.getInstance(context).getRequestQueue()).add(jsObjRequest);

    }



}
