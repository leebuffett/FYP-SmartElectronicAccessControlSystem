package com.example.smartelectronicaccesscontrolsystem.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.smartelectronicaccesscontrolsystem.AppSingleton;
import com.example.smartelectronicaccesscontrolsystem.Model.Admin;
import com.example.smartelectronicaccesscontrolsystem.R;
import com.example.smartelectronicaccesscontrolsystem.hardware_control;
import com.example.smartelectronicaccesscontrolsystem.liveStream;
import com.example.smartelectronicaccesscontrolsystem.FileDownload;
import com.example.smartelectronicaccesscontrolsystem.staff_list;
import com.example.smartelectronicaccesscontrolsystem.staff_management;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    CardView emp_manage, hardware_ctrl, file_download, live_stream;

    private static boolean ontotp=false;//       //default should be false, (because twillio account suspended)
    String addedUserId;
    String userId;
    private boolean twoFA;




    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        ImageSlider imageSlider = view.findViewById(R.id.slider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.mipmap.slide1,""));
        slideModels.add(new SlideModel(R.mipmap.slide2,""));
        slideModels.add(new SlideModel(R.mipmap.slide3,""));
        imageSlider.setImageList(slideModels,true);


        emp_manage = view.findViewById(R.id.emp_management);
        hardware_ctrl = view.findViewById(R.id.hardware_control);
        file_download =view.findViewById(R.id.file_download);
        live_stream = view.findViewById(R.id.live_stream);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Admin").child(firebaseAuth.getUid()).child("twoFAStatus");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                twoFA= (boolean) snapshot.getValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        emp_manage.setOnClickListener(this);
        hardware_ctrl.setOnClickListener(this);
        file_download.setOnClickListener(this);
        live_stream.setOnClickListener(this);



        return view;
    }

    @Override
    public void onClick(final View v) {

        if(twoFA==false){
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setTitle("Enable Two Factor Authentication")
                    .setMessage("You are required to enable two factor authentication first ! !")
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        }

        else if(twoFA==true && ontotp==false) {
            AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
            mydialog.setTitle("Enter TOTP Code");

            final EditText totpcode = new EditText(getActivity());
            totpcode.setInputType(InputType.TYPE_CLASS_NUMBER);
            totpcode.setMaxEms(10);
            totpcode.setGravity(Gravity.CENTER);
            mydialog.setView(totpcode);

            userId = getUserID();
            mydialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    userId=getUserID();
                String codeValidationUrl="https://api.authy.com/protected/json/verify/"+totpcode.getText().toString()+"/"+userId+"?api_key=ROG6QdAs7hw637PJMMqqe4DIwPKMCd83";
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET,codeValidationUrl,null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if((response.getString("token")).equals("is valid")){
                                        ontotp=true;
                                        switch (v.getId()) {
                                            case R.id.hardware_control:
                                                startActivity(new Intent(getActivity(), hardware_control.class));
                                                break;
                                            case R.id.file_download:
                                                startActivity(new Intent(getActivity(), FileDownload.class));
                                                break;
                                            case R.id.live_stream:
                                                startActivity(new Intent(getActivity(), liveStream.class));
                                                break;
                                            case R.id.emp_management:
                                                startActivity(new Intent(getActivity(), staff_management.class));
                                                break;
                                            default:
                                                break;

                                        }
                                    }
                                    else
                                        Toast.makeText(getActivity(), "You typed a wrong code!", Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(), "Error! ! !", Toast.LENGTH_LONG).show();
                            /*Toast.makeText(context,
                                    "You typed a wrong code!",
                                    Toast.LENGTH_LONG).show();*/
                            /*codeTxt.setText("");
                            errorTxt.setVisibility(View.VISIBLE);
                            */ //codeTxt.startAnimation( AnimationUtils.loadAnimation(context, R.anim.errormsg_slide));

                            }
                        });
                (AppSingleton.getInstance(getActivity()).getRequestQueue()).add(jsObjRequest);
            }
    });
                    mydialog.setNegativeButton("Cancel",null);
                    mydialog.show();

        }

        else if(twoFA==true && ontotp==true) {
            switch (v.getId()) {
                case R.id.hardware_control:
                    startActivity(new Intent(getActivity(), hardware_control.class));
                    break;
                case R.id.file_download:
                    startActivity(new Intent(getActivity(), FileDownload.class));
                    break;
                case R.id.live_stream:
                    startActivity(new Intent(getActivity(), liveStream.class));
                    break;
                case R.id.emp_management:
                    startActivity(new Intent(getActivity(), staff_management.class));
                    break;
                default:
                    break;

            }
        }

        else
            Log.e("error","error");
    }

    private String getUserID() {


        DatabaseReference df = FirebaseDatabase.getInstance().getReference("Admin");
        df.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email = (dataSnapshot.getValue(Admin.class)).getAdminEmail();
//                username = (dataSnapshot.getValue(Admin.class)).getUserName();
//                // password= (dataSnapshot.getValue(User.class)).getPassword();
//                // password= (dataSnapshot.getValue(User.class)).getPassword();
                String phoneNumber = (dataSnapshot.getValue(Admin.class)).getPhoneNumber();
                String countryCode = (dataSnapshot.getValue(Admin.class)).getPhoneCountryCode();

                String addUserUrl  = "https://api.authy.com/protected/json/users/new?user[email]="+email
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

        return addedUserId;
        // return "265284941";
    }


}
