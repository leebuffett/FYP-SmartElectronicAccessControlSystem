package com.example.smartelectronicaccesscontrolsystem.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.smartelectronicaccesscontrolsystem.AppSingleton;
import com.example.smartelectronicaccesscontrolsystem.ChangePassword;
import com.example.smartelectronicaccesscontrolsystem.CheckNetwork;
import com.example.smartelectronicaccesscontrolsystem.Logout;
import com.example.smartelectronicaccesscontrolsystem.Model.Admin;
import com.example.smartelectronicaccesscontrolsystem.R;
import com.example.smartelectronicaccesscontrolsystem.authenticationPage1;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_Fragment extends Fragment {

    private Admin admin;
    private TextView profileName,profileEmail;
    private CircleImageView profile_image;
    private boolean twoFA= true;
  //  private FirebaseAuth firebaseAuth;
    public static DatabaseReference usersTable = FirebaseDatabase.getInstance().getReference("Users");
    private static String email,password;
    private String username, phoneNumber, countryCode,addUserUrl,addedUserId;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    boolean network = false;

    private String url;

    CheckNetwork checkNetwork = new CheckNetwork();


    public Profile_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileEmail = (TextView) view.findViewById(R.id.ProfileEmail);
        profileName = (TextView) view.findViewById(R.id.ProfileName);
        profile_image=(CircleImageView) view.findViewById(R.id.profile_image);
        ListView listview = (ListView) view.findViewById(R.id.profileListview);


        if(firebaseAuth.getUid()!=null) {
            DatabaseReference databaseReference = firebaseDatabase.getReference("Admin").child(firebaseAuth.getUid());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    admin = dataSnapshot.getValue(Admin.class);

                    assert admin != null;
                    profileEmail.setText(admin.getAdminEmail());
                    profileName.setText(admin.getAdminName());

                    url = admin.getAdminImage();
                    twoFA = admin.isTwoFAStatus();

                    Picasso.get().load(url).into(profile_image);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        if (checkNetwork.isInternetAvailable(getActivity()))  //if connection available
        {
            network=true;
        }
        else {
            network=false;
        }

        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("Change Password");
        arrayList.add("MultiFactor Authentication");
        arrayList.add("Logout");

        final Activity thisActivity = getActivity();
        if(thisActivity!=null) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arrayList);

            listview.setAdapter(arrayAdapter);
            if (network) {

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                //Toast.makeText(getActivity(),"change password",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(thisActivity, ChangePassword.class));
                                break;

                            case 1:
                                if (twoFA == true) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Information")
                                            .setMessage("You Already Enable 2FA")
                                            .setNegativeButton("Cancel", null)
                                            .setPositiveButton("Disable", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                                    builder1.setTitle("Confirmation")
                                                            .setMessage("Confirm disable 2FA ?")
                                                            .setNegativeButton("Cancel", null)
                                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    removeUser();
                                                                }


                                                            });
                                                    builder1.show();
                                                }
                                            });
                                    builder.show();
                                } else if (twoFA == false) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Enable 2FA")
                                            .setMessage("Are you want to enable 2FA ?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    startActivity(new Intent(getActivity(), authenticationPage1.class));
                                                }
                                            })
                                            .setNegativeButton("Cancel", null);

                                    builder.show();
                                }
                                break;

                            case 2:
                                //startActivity(new Intent(getActivity(), UploadImage.class));
                                alertsignout();
                                break;

                            default:
                                break;

                        }
                    }


                });
            }
        }

        return view;
    }

public void removeUserFunction(String userid){

    String removeURL="https://api.authy.com/protected/json/users/"+userid+"/remove?api_key=ROG6QdAs7hw637PJMMqqe4DIwPKMCd83";

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,removeURL,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            DatabaseReference databaseReference = firebaseDatabase.getReference("Admin").child(firebaseAuth.getUid());
                            databaseReference.child("twoFAStatus").setValue(false);

                            Toast.makeText(getActivity(), "Successfully disable 2FA", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Error! ! !", Toast.LENGTH_LONG).show();
                        }
                    });
            (AppSingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue()).add(jsObjRequest);

}

    public void removeUser(){
        DatabaseReference df = FirebaseDatabase.getInstance().getReference("Admin");
        df.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email = (dataSnapshot.getValue(Admin.class)).getAdminEmail();
                phoneNumber = (dataSnapshot.getValue(Admin.class)).getPhoneNumber();
                countryCode = (dataSnapshot.getValue(Admin.class)).getPhoneCountryCode();

                addUserUrl  = "https://api.authy.com/protected/json/users/new?user[email]="+email
                        +"&user[cellphone]="+phoneNumber
                        +"&user[country_code]="+countryCode+"&api_key=ROG6QdAs7hw637PJMMqqe4DIwPKMCd83";

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,addUserUrl,null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Gson gson = new Gson();
                                try {
                                    JsonObject addedUser = gson.fromJson(response.getString("user"),JsonObject.class);
                                    addedUserId = (addedUser.get("id")).getAsString();

                                    removeUserFunction(addedUserId);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("ERROR Function! ",error.getMessage());
                            }
                        });

                (AppSingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue()).add(jsObjRequest);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

}


    public void alertsignout(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Logout")
                .setMessage("Are you sure want to logout?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(getActivity(), Logout.class));

                        getActivity().getSupportFragmentManager().beginTransaction().remove(getParentFragment()).commit();


                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

}
