package com.example.smartelectronicaccesscontrolsystem.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.smartelectronicaccesscontrolsystem.AppSingleton;
import com.example.smartelectronicaccesscontrolsystem.Model.Notification;
import com.example.smartelectronicaccesscontrolsystem.Model.Admin;
import com.example.smartelectronicaccesscontrolsystem.NotificationDetails;
import com.example.smartelectronicaccesscontrolsystem.R;
import com.example.smartelectronicaccesscontrolsystem.Model.Notification;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mcontext;
 // //  private List<Notification> mnotify;
    private List<Notification> mnotify;
    private ArrayList<String> key;


    private boolean twoFA;

    String addedUserId;
    String userId;

    private static boolean ontotp = false;

    Calendar cal = Calendar.getInstance();
    private int day = cal.get(Calendar.DAY_OF_MONTH);
    private int year = cal.get(Calendar.YEAR);

    SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    private String month_name = month_date.format(Calendar.getInstance().getTime());

    SimpleDateFormat day_of_month_format = new SimpleDateFormat("dd", Locale.ENGLISH);
    private String day_of_month = day_of_month_format.format(Calendar.getInstance().getTime());



    public NotificationAdapter(Context mcontext,List<Notification> mnotify,ArrayList<String> key){
        this.mcontext=mcontext;
        this.mnotify=mnotify;
        this.key=key;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(mcontext).inflate(R.layout.notification_list,parent,false);


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

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

      final Notification notification = mnotify.get(position);
       holder.notify.setText(notification.getTitle());
        holder.notifytime.setText(notification.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(twoFA==false){
                    AlertDialog.Builder builder=new AlertDialog.Builder(mcontext);
                    builder.setTitle("Enable Two Factor Authentication")
                            .setMessage("You are required to enable two factor authentication first ! !")
                            .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();
                }

                else if(twoFA==true && ontotp==true){
                    Intent i = new Intent(mcontext, NotificationDetails.class);
//                    i.putExtra("notifyid",notification.getId());
                    i.putExtra("notifyid",key.get(position));
                    i.putExtra("day",String.valueOf(day_of_month));
                    i.putExtra("month",month_name);
                    i.putExtra("year",String.valueOf(year));
                    mcontext.startActivity(i);
                }

//       //         //this else if if for testing used, when twillio authy account can be used, this should be removed
//                else if(twoFA==true && ontotp==false){
//                    Intent i = new Intent(mcontext, NotificationDetails.class);
//                    i.putExtra("notifyid",notification.getId());
//                    i.putExtra("day",String.valueOf(day));
//                    i.putExtra("month",month_name);
//                    i.putExtra("year",String.valueOf(year));
//                    mcontext.startActivity(i);
//                }

//comment else twoFA==true && ontotp==false (because twillio account suspended)

                else if(twoFA==true && ontotp==false){
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(mcontext);
                    mydialog.setTitle("Enter TOTP Code");

                    final EditText totpcode = new EditText(mcontext);
                    totpcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                    totpcode.setMaxEms(10);
                    totpcode.setGravity(Gravity.CENTER);
                    mydialog.setView(totpcode);

                    userId=getUserID();

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
                                                        Intent i = new Intent(mcontext, NotificationDetails.class);
//                                                        i.putExtra("notifyid",notification.getId());
                                                        i.putExtra("notifyid",key.get(position));
                                                        i.putExtra("day",String.valueOf(day_of_month));
                                                        i.putExtra("month",month_name);
                                                        i.putExtra("year",String.valueOf(year));
                                                        mcontext.startActivity(i);
                                                }
                                                else
                                                    Toast.makeText(mcontext, "You typed a wrong code!", Toast.LENGTH_LONG).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(mcontext, "Error! ! !", Toast.LENGTH_LONG).show();
                        /*Toast.makeText(context,
                                "You typed a wrong code!",
                                Toast.LENGTH_LONG).show();*/
                        /*codeTxt.setText("");
                        errorTxt.setVisibility(View.VISIBLE);
                        */ //codeTxt.startAnimation( AnimationUtils.loadAnimation(context, R.anim.errormsg_slide));

                                        }
                                    });
                            (AppSingleton.getInstance(mcontext).getRequestQueue()).add(jsObjRequest);
                        }
                    });
                    mydialog.setNegativeButton("Cancel",null);
                    mydialog.show();


                }
                else{
                   // Toast.makeText(mcontext,"Error",Toast.LENGTH_SHORT).show();
                }

                }

        });
    }

    private String getUserID() {


        DatabaseReference df = FirebaseDatabase.getInstance().getReference("Admin");
        df.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email = (dataSnapshot.getValue(Admin.class)).getAdminEmail();
//                username = (dataSnapshot.getValue(UserProfile.class)).getUserName();
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

                (AppSingleton.getInstance(mcontext.getApplicationContext()).getRequestQueue()).add(jsObjRequest);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return addedUserId;
       // return "265284941";
    }

    @Override
    public int getItemCount() {
        return mnotify.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView notify,notifytime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notify = (TextView) itemView.findViewById(R.id.notify);
            notifytime = (TextView) itemView.findViewById(R.id.notifytime);

        }
    }
}
