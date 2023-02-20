package com.example.smartelectronicaccesscontrolsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartelectronicaccesscontrolsystem.Model.staff;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;


public class add_edit_staff extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    FirebaseVisionImage firebaseVisionImage;
    CountryCodePicker ccp;
    EditText staff_name,staff_age,staff_email,staff_phone;
    TextView birthOfDate;
    ProgressBar registerProgress,deleteProgress;
    Button registerBtn, updateBtn,deleteBtn;
    ImageButton uploadBtn;
    CircleImageView StaffImg;
    String[] Gender = {"Male", "Female"};
    String staff_gender;
    int sum = 0;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    Intent intent;
    String name, phone, cc, bod, age, gender, email, image, id;
    //String name, phone, cc, bod, age, gender, email, image, id;
    Spinner mySpinner;


    //used for validate same name, and etc
    private String validateName[] =new String[365];
    private String validateEmail[]=new String[365];
    private String validatePhno[]=new String[365];


    //used for validate array
    private int counter =0;


    //check date
    Date today, staffbod;

    //check number of faces in profile image
    private boolean manyfaces=false;
    private boolean nofaces=false;

    //delay user to click the button
    private boolean abletoClick = true;


    //    private static SecureRandom SECURE_RANDOM = new SecureRandom();
    private StorageReference mStorageRef;
    public Uri uri;
    private String downloadUrl, add_name, staffID;

    //storage (display in edit image)
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;


    //for aysnc task face result
//    GetFaceResult getFaceResult = new GetFaceResult();
//    String result="";
    String faceResult="isTrue"; //default will allow, if the application no click upload Image, it will be allow to bypass validation

    //get Drawble //for edit train model used
    Drawable myDrawable;

    //get activity name
    String activityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_staff);

        //get item id
        birthOfDate= (TextView) findViewById(R.id.dateOfBirth);
        mySpinner = (Spinner) findViewById(R.id.genderSpinner);
        uploadBtn = findViewById(R.id.uploadBtn);
        StaffImg = findViewById(R.id.Staff_img);
        updateBtn = (Button) findViewById(R.id.update_staff);
        registerBtn = (Button) findViewById(R.id.Register_staff);
        staff_name = (EditText) findViewById(R.id.staff_name);
        staff_age = (EditText) findViewById(R.id.age);
        staff_email = (EditText) findViewById(R.id.email);
        ccp = findViewById(R.id.ccp);
        staff_phone = findViewById(R.id.txt_staff_phone);
        registerProgress = findViewById(R.id.registerStaffProgress);

        //textview8
        TextView tx8 = (TextView) findViewById(R.id.textView8);


        //get value from previous activity, and will be shown in the layout
        intent = getIntent();
        activityName = intent.getStringExtra("activity");

        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        cc = intent.getStringExtra("cc");
        bod = intent.getStringExtra("bod");
        age = intent.getStringExtra("age");
        gender = intent.getStringExtra("gender");
        email = intent.getStringExtra("email");
        image = intent.getStringExtra("image");
        id = intent.getStringExtra("staffId");


        //get other user credential, used to compare validation
        getOtherStaffInfo();

        if(activityName.equals("editStaff")){
            editStaffFunction();
        }
        else if(activityName.equals("addStaff")){
            addStaffFunction();
        }
        else{
            finish();
        }


    }

    //call python to perform face recognition validation in add page
    private void add_validation() {

        //pass (NONE) as a parameter to the web browser

        AsyncHttpClient client = new AsyncHttpClient();


        client.get("http://210.195.151.15/cgi-bin/fyp/a.php?func=add&param1=None&param2=None", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                abletoClick = true;
          //      Toast.makeText(add_edit_staff.this, "web browser", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                abletoClick = true;
             //   Toast.makeText(add_edit_staff.this, "fail web browser", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //call python to retrain the model (pass parameter --> name [use add_name])
    private void add_train_model() {

        //pass name as a parameter to the browser
        String newStaff = staff_name.getText().toString();

       // String newStaff = add_name;                       // or use [add_name]

        AsyncHttpClient client = new AsyncHttpClient();


        client.get("http://210.195.151.15/cgi-bin/fyp/a.php?func=add&param1="+newStaff+"&param2=None", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               // Toast.makeText(add_edit_staff.this, "web browser", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    //call python to perform face recognition validation in edit page
    private void edit_validation(){

        //pass original name parameter to the web api (use [name])

        AsyncHttpClient client = new AsyncHttpClient();


        client.get("http://210.195.151.15/cgi-bin/fyp/a.php?func=edit_compare&param1="+name+"&param2=None", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                abletoClick = true;
           //     Toast.makeText(add_edit_staff.this, "web browser", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                abletoClick = true;
          //      Toast.makeText(add_edit_staff.this, "fail web browser", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //call python to retrain the model (pass parameter --> old name and also new name)
    private void edit_train_model(String functionName, String param1, String param2) {

//        //pass old/original name parameter to the web api (use [name])
//        String oldName = name;
//
//        //pass new name parameter to the web api --> (can use [add_name] or [staff_name.getText().toString()])
//        String newName = staff_name.getText().toString();
//       // String newName = add_name;          //can use [add_name] too

        AsyncHttpClient client = new AsyncHttpClient();


        client.get("http://210.195.151.15/cgi-bin/fyp/a.php?func="+functionName+"&param1="+param1+"&param2="+param2, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
              //  Toast.makeText(add_edit_staff.this, "web browser", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    //edit Staff function will called if the user want to edit staff ! ! !
    private void editStaffFunction() {
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Staff");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference();

        //set visibility of the button
        registerBtn.setVisibility(View.INVISIBLE);


        //set textview and edit text to the user's value
        staff_name.setText(name);
        staff_phone.setText(phone);
        ccp.setCountryForPhoneCode(Integer.valueOf(cc));
        birthOfDate.setText(bod);
        staff_age.setText(age);
        staff_email.setText(email);

        //set image
        Picasso.get().load(image).into(StaffImg);

        //get image // use for edit train model
        myDrawable = StaffImg.getDrawable();


        //used for Gender drop down list
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(add_edit_staff.this,
                R.layout.spinner_textview_align,Gender);
        myAdapter.setDropDownViewResource(R.layout.spinner_textview_align);
        mySpinner.setAdapter(myAdapter);
        if(gender.equals("Male")){
            mySpinner.setSelection(0);
        }
        else{
            mySpinner.setSelection(1);
        }
        mySpinner.setOnItemSelectedListener(this);

        //when upload Img button or calendar is selected, will call onClick function
        uploadBtn.setOnClickListener(this);
        birthOfDate.setOnClickListener(this);
    }




    //add Staff function will called if the user want to add staff ! ! !
    private void addStaffFunction() {

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Staff");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference();


        //set visibility of the button
        updateBtn.setVisibility(View.INVISIBLE);

        //used for Gender drop down list
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(add_edit_staff.this,
                R.layout.spinner_textview_align,Gender);
        myAdapter.setDropDownViewResource(R.layout.spinner_textview_align);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(this);

        //when upload Img button or calendar is selected, will call onClick function
        uploadBtn.setOnClickListener(this);
        birthOfDate.setOnClickListener(this);
    }

    //When upload image or calendar is selected.
    @Override
    public void onClick(View v) {

        //used for dateOfBirth, choose from calendar
        final Calendar myCalendar = Calendar.getInstance();

        switch (v.getId()) {
            //if
            case R.id.uploadBtn:
                CropImage.activity().start(add_edit_staff.this);break;
            case R.id.dateOfBirth:
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        birthOfDate.setText(sdf.format(myCalendar.getTime()));

                    }
                };
                new DatePickerDialog(add_edit_staff.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();break;
            default:break;

        }
    }

    // When Gender drop down list item is selected, retrieve selected data
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        staff_gender=Gender[position];
    }

    //used for Gender drop down list
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //will be used when user upload image and crop the image.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (result != null) {
                    uri = result.getUri(); //path of image in phone
                    StaffImg.setImageURI(uri); //set image in imageview
                    abletoClick = false; //set to false, user should wait for the result produce by mlkit
                    faceResult="Processing"; //set to System waiting....
                    uploadBtn.setClickable(false);
                    detectFaceFromImage(uri);
                }
            }
        }
    }

    //When update button pressed, this function will called.
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void UpdateStaffBtn(View view){




//        //simply set img name
//        final String image = "asdasd";

        //get item text value
        final String bod = birthOfDate.getText().toString();
        final String stffname = staff_name.getText().toString();
        final String age = staff_age.getText().toString();
        final String email = staff_email.getText().toString();
        final String phoneNo = staff_phone.getText().toString();
        final String countryCode = ccp.getSelectedCountryCode();

        //staff image
        final Drawable compareDrawable = StaffImg.getDrawable();


        //assign add_name (for uri name)
        add_name = stffname;

        boolean validate = validation();

        if(validate) {
            AlertDialog.Builder builder = new AlertDialog.Builder(add_edit_staff.this);
            builder.setMessage("Confirm Update?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //call web api to retrain the model
                            if(myDrawable.getConstantState().equals(compareDrawable.getConstantState()) && stffname.equals(name)){
                                //func=edit_none&param1=None&param2=None
                             //   Toast.makeText(add_edit_staff.this, "both none", Toast.LENGTH_SHORT).show();
                                edit_train_model("edit_none","None","None");
                            }

                            else if(!myDrawable.getConstantState().equals(compareDrawable.getConstantState()) && stffname.equals(name)){
                                //func=edit_img&param1=name&param2=None
                       //         Toast.makeText(add_edit_staff.this, "img onli", Toast.LENGTH_SHORT).show();
                                edit_train_model("edit_img",name,"None");
                            }
                            else if(myDrawable.getConstantState().equals(compareDrawable.getConstantState()) && !stffname.equals(name)){
                                //func=edit_name&param1=name&param2=name
                       //         Toast.makeText(add_edit_staff.this, "name onli", Toast.LENGTH_SHORT).show();
                                edit_train_model("edit_name",name,stffname);
                    //            Log.d("testname",name+stffname);
                            }
                            else if(!myDrawable.getConstantState().equals(compareDrawable.getConstantState()) && !stffname.equals(name)){
                                //func=edit_both&param1=name&param2=name
                    //            Toast.makeText(add_edit_staff.this, "both", Toast.LENGTH_SHORT).show();
                                edit_train_model("edit_both",name,stffname);
                            }
                            else{
                                Toast.makeText(add_edit_staff.this, "Error.......", Toast.LENGTH_SHORT).show();
                            }

                            //update to firebase
                            rootNode = FirebaseDatabase.getInstance();
                            reference = rootNode.getReference("staff");

                            //upload image to storage first
                            updateuploadImg();


                            //       Map<String, staff> staffClass = new HashMap<>();
                            HashMap<String, Object> staffClass = new HashMap<>();
                            staffClass.put(id, new staff(id, stffname, age, phoneNo, countryCode, email, bod, staff_gender));
                            reference.updateChildren(staffClass);

                            //prompt successful Message
                            registerProgress.setVisibility(View.VISIBLE);
                            updateBtn.setVisibility(View.GONE);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(add_edit_staff.this, "Staff Updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }, 2000);
                        }
                    })
                    .setNegativeButton("Cancel", null);
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    //When register button pressed, this function will called.
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void RegisterStaffBtn(View view){

        //label staff id with the total staff plus one
        //      final String staffId="staff" + (sum+1);
//        final String addstaffId = nextSessionId();
        final String image = downloadUrl;

        //get item text value
        final String bod = birthOfDate.getText().toString();
        final String name = staff_name.getText().toString();
        final String age = staff_age.getText().toString();
        final String email = staff_email.getText().toString();
        final String phoneNo = staff_phone.getText().toString();
        final String countryCode = ccp.getSelectedCountryCode();


        //assign add_name (for uri name)
        add_name = name;

        //used for ltr comparison //no duplicate name and etc
//        getOtherStaffInfo();

        //perform validation
        boolean validate = validation();

        //if no error, register staff into firebase
        if(validate) {
            AlertDialog.Builder builder = new AlertDialog.Builder(add_edit_staff.this);
            builder.setMessage("Confirm Register?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            registerProgress.setVisibility(View.VISIBLE);
                            registerBtn.setVisibility(View.GONE);


                            rootNode = FirebaseDatabase.getInstance();
                            reference = rootNode.getReference("staff");

                            staffID = reference.push().getKey();

                            //upload image to storage first
                            uploadImg();


                            //       Map<String, staff> staffClass = new HashMap<>();
                            HashMap<String, Object> staffClass = new HashMap<>();
                            staffClass.put(staffID, new staff(staffID, name, age, phoneNo, countryCode, email, bod, staff_gender));
                            reference.updateChildren(staffClass);

                            //call python to retrain the model
                            add_train_model();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(add_edit_staff.this, "Staff Registered", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }, 4000);
                        }
                    })
                    .setNegativeButton("Cancel", null);

            AlertDialog alert = builder.create();
            alert.show();
//        }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean validation() {

        //call backend retrieve image result first
        if(abletoClick) {
            GetFaceResult getFaceResult = new GetFaceResult();
            getFaceResult.execute();
        }

        // get user input
        final String bod = birthOfDate.getText().toString();
        final String name = staff_name.getText().toString();
        final String age = staff_age.getText().toString();
        final String email = staff_email.getText().toString();
        final String phoneNo = staff_phone.getText().toString();
        final String countryCode = ccp.getSelectedCountryCode();

        //combine country code and number
        final String combinephNum = countryCode+phoneNo;


        //used for validate the email address
        final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher emailmatcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);



        //used for validate date
        today = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.format(today);

        try {
            staffbod = new SimpleDateFormat("dd/MM/yyyy").parse(bod);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(StaffImg.getDrawable().getConstantState().equals(StaffImg.getContext().getDrawable(R.drawable.profile_image_blank).getConstantState())){

            Toast.makeText(add_edit_staff.this, "Please upload profile image", Toast.LENGTH_SHORT).show();
            return false;

        }

        else if(name.isEmpty()){
            staff_name.setError("This field is empty! ! ");
            staff_name.requestFocus();
            return false;
        }

        else if(bod.isEmpty()){
            birthOfDate.setError("This field is empty! ! ");
            birthOfDate.requestFocus();
            return false;
        }
        else if(age.isEmpty()){
            staff_age.setError("This field is empty! ! ");
            staff_age.requestFocus();
            return false;
        }
        else if(email.isEmpty()){
            staff_email.setError("This field is empty! ! ");
            staff_email.requestFocus();
            return false;
        }
        else if(phoneNo.isEmpty()){
            staff_phone.setError("This field is empty! ! ");
            staff_phone.requestFocus();
            return false;
        }



        else if(staffbod.after(today)){
            birthOfDate.setError("Cannot exceed today date");
            Toast.makeText(add_edit_staff.this,"Cannot exceed today date", Toast.LENGTH_SHORT).show();
            birthOfDate.requestFocus();
            return false;
        }

        else if(!emailmatcher.find()){
            staff_email.setError("Invalid email format ");
            staff_email.requestFocus();
            return false;
        }

        else if(!validateAge()){
            staff_age.setError("Age not matched ");
            staff_age.requestFocus();
            return false;
        }

        else if (!validateAge18()){
            staff_age.setError("Age should atleast 18 ");
            staff_age.requestFocus();
            return false;
        }

        else if(!abletoClick){      //ask user to wait for mlkit result.....
            Toast.makeText(add_edit_staff.this, "System still process the image......", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(nofaces){       //if the image does not have any faces inside, prompt an error message
            uploadBtn.setClickable(true);
            Toast.makeText(add_edit_staff.this, "Please upload profile image with one face", Toast.LENGTH_SHORT).show();
            return false;
        }

        else if(manyfaces){         //if the image contains of many faces, which detected by mlkit, prompt an error message
            uploadBtn.setClickable(true);
            Toast.makeText(add_edit_staff.this, "Disallow to upload group photo", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(faceResult.equals("Processing")){      //if system still recognizing the faces, should ask user for waiting.....
            Toast.makeText(add_edit_staff.this, "System still process the image......", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(faceResult.equals("Matched")){      //if image result is 'face exist', should prompt an error message
            uploadBtn.setClickable(true);
            Toast.makeText(add_edit_staff.this, "Face exist.... Upload again", Toast.LENGTH_SHORT).show();
            return false;
        }


        else {
            uploadBtn.setClickable(true);
            boolean truefalse=true;

            for(int y=0; y<=counter;y++){

                if(name.equalsIgnoreCase(validateName[y])){
                    staff_name.setError("Name already exist ");
                    staff_name.requestFocus();
                    truefalse =false;
                    break;
                }

                else if(email.equalsIgnoreCase(validateEmail[y])){
                    staff_email.setError("Email already exist ");
                    staff_email.requestFocus();
                    truefalse =false;
                    break;
                }

                else if(combinephNum.equalsIgnoreCase(validatePhno[y])){
                    staff_phone.setError("Phone number already exist");
                    staff_phone.requestFocus();
                    truefalse =false;
                    break;
                }

            }

            return truefalse;
        }

    }

    private boolean validateAge18() {

        String age = staff_age.getText().toString();

        if(Integer.valueOf(age)>=18){
            return true;
        }

        return false;
    }

    private boolean validateAge() {

        String bod = birthOfDate.getText().toString();
        String age = staff_age.getText().toString();

        String substring = bod.substring(Math.max(bod.length() - 4, 0));

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int ageyear = Integer.valueOf(substring);

        int actualage = year - ageyear;


        if(age.equals(String.valueOf(actualage))){

            return true;
        }

        return false;
    }

    private void getOtherStaffInfo() {



        final String bod = birthOfDate.getText().toString();
        final String name = staff_name.getText().toString();
        final String age = staff_age.getText().toString();
        final String email = staff_email.getText().toString();
        final String phoneNo = staff_phone.getText().toString();
        final String countryCode = ccp.getSelectedCountryCode();

        final String combinephNum = countryCode+phoneNo;


        rootNode = FirebaseDatabase.getInstance();
        DatabaseReference dbreference = rootNode.getReference("staff");

        dbreference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if(!snapshot.child("id").getValue(String.class).equals(id)) {
                        String cc = snapshot.child("countryCode").getValue(String.class);
                        String ph = snapshot.child("phoneNumber").getValue(String.class);

                        validateName[counter] = snapshot.child("name").getValue(String.class);
                        validateEmail[counter] = snapshot.child("email").getValue(String.class);
                        validatePhno[counter] = cc + ph;

                        counter++;
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void uploadImg(){


        StorageReference riversRef = mStorageRef.child("staffs/"+add_name+".jpg");

        riversRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Get a URL to the uploaded content
                        Task<Uri> firebaseUri = taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl = String.valueOf(uri);

                                reference.child(staffID).child("image").setValue(downloadUrl);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    public void updateuploadImg(){



        CircleImageView imgtest = (CircleImageView) findViewById(R.id.Staff_img);

        Bitmap bitmap = ((BitmapDrawable) imgtest.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();

        final StorageReference riversRef = mStorageRef.child("staffs/"+add_name+".jpg");
        //Toast.makeText(this, id, Toast.LENGTH_SHORT).show();

        // delete the old image first
        String storageUrl = "staffs/"+ name+".jpg";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(storageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                //upload again the image file to the firebase storage
                riversRef.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content


//                        Task<Uri> firebaseUri = taskSnapshot.getMetadata().getReference().getDownloadUrl(None);
                                Task<Uri> firebaseUri = taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadUrl = String.valueOf(uri);

                                        reference.child(id).child("image").setValue(downloadUrl);

                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });


    }

    public void uploadTempImage(){

        //for add (based on intent activity)
        StorageReference riversRef = mStorageRef.child("tempStaff/Unknown.jpg");

        riversRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Get a URL to the uploaded content

                        if(activityName.equals("editStaff")){
                            edit_validation();
                        }
                        else if(activityName.equals("addStaff")){
                            add_validation();
                        }
                        else{
                            Toast.makeText(add_edit_staff.this, "unable to perform validation", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });


        //for edit (based on intent activity) //edit use back(Unknown.jpg)

    }

    private void detectFaceFromImage(Uri uri) {
        try {
            firebaseVisionImage = FirebaseVisionImage.fromFilePath(add_edit_staff.this, uri);
            FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                    new FirebaseVisionFaceDetectorOptions.Builder()
                            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                            .build();
            FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                    .getVisionFaceDetector(highAccuracyOpts);

            detector.detectInImage(firebaseVisionImage)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionFace> faces) {
                            for (FirebaseVisionFace face : faces) {

                            }
                            if(faces.size()==0){
                                manyfaces=false;
                                nofaces =true;
                                abletoClick = true;
                                uploadBtn.setClickable(true);
                                Toast.makeText(add_edit_staff.this, "Please upload profile image with one face", Toast.LENGTH_SHORT).show();
                            }
                            else if(faces.size()>1){
                                nofaces =false;
                                manyfaces=true;
                                abletoClick = true;
                                uploadBtn.setClickable(true);
                                Toast.makeText(add_edit_staff.this, "Disallow to upload group photo", Toast.LENGTH_SHORT).show();
                            }
                            else if(faces.size()==1){
                                manyfaces=false;
                                nofaces =false;
                                faceResult="Processing";
                                uploadTempImage();


                            }
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                    Toast.makeText(add_edit_staff.this, "no face detected", Toast.LENGTH_SHORT).show();
                                }
                            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //call python to get the face result (whether the image is duplicated or not)
//    public String getFaceResult(){
//        String ip = "http://210.195.151.15/b.py";
//        Handler h = new Handler();
//
//        try{
//            URL url = new URL(ip);
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setRequestMethod("GET");
//            httpURLConnection.setDoInput(true);
//
//            httpURLConnection.connect();
//
//            InputStream is = httpURLConnection.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            String line = "";
//            while((line = reader.readLine())!=null){
//                result += line;
//            }
//            Log.i("pubipfinder", "EXT IP: " + result);
//            httpURLConnection.disconnect();
//        }catch (MalformedURLException e){
//            e.printStackTrace();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//
//
//        return result;
//    }


    class GetFaceResult extends AsyncTask<String,String,String> {

        String ip = "http://210.195.151.15/a.txt";
        Handler h = new Handler();
        String result="";


        @Override
        protected String doInBackground(String... strings) {

            try{
                URL url = new URL(ip);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);

                httpURLConnection.connect();

                InputStream is = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = "";
                while((line = reader.readLine())!=null){
                    result += line;
                }
                Log.i("pubipfinder", "EXT IP: " + result);
                httpURLConnection.disconnect();
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("testPost",s);
            faceResult = s; //getting the result from web api, whether image is duplicated or not, and apply it to the variable faceResult
            Log.d("testPostPOst",faceResult);

        }
    }

}
