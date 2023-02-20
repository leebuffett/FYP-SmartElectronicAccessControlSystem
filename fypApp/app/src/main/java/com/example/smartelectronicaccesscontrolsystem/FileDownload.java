package com.example.smartelectronicaccesscontrolsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.provider.FontsContract;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartelectronicaccesscontrolsystem.Model.Notification;
import com.example.smartelectronicaccesscontrolsystem.Model.authlog;
import com.example.smartelectronicaccesscontrolsystem.Model.staff;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class FileDownload extends AppCompatActivity implements View.OnClickListener {

    private static List<authlog> mlog;

    TextView startDate, endDate;
    DatabaseReference[] reference = new DatabaseReference[365];
    DatabaseReference reference1;
    DatabaseReference[] keyReference = new DatabaseReference[365];
    String startYear, startMonth,startDay;
    String writeStart,writeEnd;
    String endYear, endMonth,endDay;
//    String[] staffid = new String[365];
    String[] name= new String[365];
    String[] desc= new String[365];
    String [] time= new String[365];
    int r = 5;
    boolean exist = true;
    //    int counter = 0;
    int z=0;
    String month_name, day_of_month;

    Date start, end, today;

    int dr=0;
    int kr=0;
    int counter=0;

    int test = 12;
    String testing;

    TextView scr1,scr2,scr3,scr4,scr5;
    TableLayout tl;
    TableRow tr;

    String datestart;
    String dateend;

    //used for startat, endat
    int countertest=0;
//    String testName[] = new String[50];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_download);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("File Download");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get item id
        startDate = (TextView) findViewById(R.id.startDate);
        endDate = (TextView) findViewById(R.id.endDate);


        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        //calendar
        final Calendar myCalendar = Calendar.getInstance();

        switch (v.getId()){
            case R.id.startDate:
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        startYear = String.valueOf(year);
                        startMonth = String.valueOf(month);
                        startDay = String.valueOf(dayOfMonth);

                        startDate.setText(sdf.format(myCalendar.getTime()));

                        //for write to file purpose
                        writeStart=startDate.getText().toString();

                    }
                };
                new DatePickerDialog(FileDownload.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.endDate:
                date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        endYear = String.valueOf(year);
                        endMonth = String.valueOf(month);
                        endDay = String.valueOf(dayOfMonth);

                        endDate.setText(sdf.format(myCalendar.getTime()));

                        //for write to file purpose
                        writeEnd=endDate.getText().toString();

                    }
                };
                new DatePickerDialog(FileDownload.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            default:break;
        }
    }

    public void generate(View view)  {

        boolean truefalse = false;

        try {
            start = new SimpleDateFormat("dd/MM/yyyy").parse(startDate.getText().toString());
            end = new SimpleDateFormat("dd/MM/yyyy").parse(endDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        today = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.format(today);

        if(startDate.getText().toString().matches("")){
            Toast.makeText(FileDownload.this,"Do not leave it blank",Toast.LENGTH_SHORT).show();
            startDate.requestFocus();
            truefalse=false;
        }
        else if (endDate.getText().toString().matches("")){
            Toast.makeText(FileDownload.this,"Do not leave it blank",Toast.LENGTH_SHORT).show();
            endDate.requestFocus();
            truefalse=false;
        }

        else if (start.after(today) || end.after(today)){
            Toast.makeText(FileDownload.this,"ERROR!!! Date should not exceed today date",Toast.LENGTH_SHORT).show();
            truefalse=false;
        }
        else if (end.before(start)){
            Toast.makeText(FileDownload.this,"ERROR!!! End Date should not before Start Date",Toast.LENGTH_SHORT).show();
            truefalse=false;
        }
        else{
            datestart = startDate.getText().toString();
            dateend = endDate.getText().toString();
            truefalse=true;
        }



        if(truefalse==true){

            mlog = new ArrayList<>();


            mlog.clear();

            while(!start.after(end)){

                SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.ENGLISH);
                month_name = month_date.format(start);

                SimpleDateFormat day_of_month_format = new SimpleDateFormat("dd", Locale.ENGLISH);
                day_of_month = day_of_month_format.format(start);

                reference[dr] = FirebaseDatabase.getInstance().getReference().child("Authentication Log File").child(startYear).child(month_name).child(day_of_month);
                reference[dr].addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren()) {


                            String id1 = ds.child("id").getValue(String.class);

                            name[countertest] = ds.child("name").getValue(String.class);
//                            staffid[countertest]= ds.child("staffId").getValue(String.class);
                            time[countertest]= ds.child("time").getValue(String.class);
                            desc[countertest]= ds.child("description").getValue(String.class);


                            authlog alog = new authlog(id1,desc[countertest],name[countertest],time[countertest]);

                            mlog.add(alog);

                            countertest++;

                        }

                        showData();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                Calendar cal = Calendar.getInstance();
                cal.setTime(start); // convert your date to Calendar object
                int daysToIncrement = +1;
                cal.add(Calendar.DATE, daysToIncrement);
                start = cal.getTime(); // again get back your date object

                dr++;

                startYear = String.valueOf(cal.get(Calendar.YEAR));
                startMonth = String.valueOf(cal.get(Calendar.MONTH));
                startDay = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));


            }


            startDate.setText("");
            endDate.setText("");

            AlertDialog.Builder builder = new AlertDialog.Builder(FileDownload.this);
            builder.setMessage("Log File is generated. Please check from your device file.")
                    .setNeutralButton("okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(getIntent());
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();

        }

    }


    private void showData() {


        Workbook wb= new HSSFWorkbook();
        Cell cell=null;
        final CellStyle cellStyle,cellStyle1;
        cellStyle=wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        cellStyle1=wb.createCellStyle();

        final Sheet sheet = wb.createSheet("Authentication Log File");
        //Now column and row
        Row row =sheet.createRow(0); //create row (first row)

        cell=row.createCell(0);  //create column (first column)
        cell.setCellValue("Authentication LOG FILE");
        cell.setCellStyle(cellStyle1);

        //second row
        row=sheet.createRow(1);
        cell=row.createCell(0);
        cell.setCellValue("Start Date: ["+writeStart+"] End Date: ["+writeEnd+"]");
        cell.setCellStyle(cellStyle1);

        //forth row
        row=sheet.createRow(3);

        cell=row.createCell(0);
        cell.setCellValue("No");
        cell.setCellStyle(cellStyle);

//        cell=row.createCell(1);
//        cell.setCellValue("Staff Id");
//        cell.setCellStyle(cellStyle);

        cell=row.createCell(1);
        cell.setCellValue("Name");
        cell.setCellStyle(cellStyle);

        cell=row.createCell(2);
        cell.setCellValue("Time");
        cell.setCellStyle(cellStyle);

        cell=row.createCell(3);
        cell.setCellValue("Description");
        cell.setCellStyle(cellStyle);

        int i=0;

        for(int y = 4; y<(mlog.size()+4);y++) {
            row = sheet.createRow(y); //create row (first row)

            cell = row.createCell(0);  //insert number
            cell.setCellValue(String.valueOf(y-3));
            cell.setCellStyle(cellStyle);
            // Log.d("row", String.valueOf(k));

//            cell = row.createCell(1);  //insert staffid
//            cell.setCellValue(String.valueOf(staffid[i]));
//            cell.setCellStyle(cellStyle);
//            //       Log.d("row", String.valueOf(staffid[k]));

            cell = row.createCell(1);  //insert name
            cell.setCellValue(String.valueOf(name[i]));
            cell.setCellStyle(cellStyle);
            //        Log.d("row", String.valueOf(name[k]));

            cell = row.createCell(2);  //insert time
            cell.setCellValue(String.valueOf(time[i]));
            cell.setCellStyle(cellStyle);
            //          Log.d("row", String.valueOf(time[k]));

            cell = row.createCell(3);  //insert description
            cell.setCellValue(String.valueOf(desc[i]));
            cell.setCellStyle(cellStyle);
            //        Log.d("row", String.valueOf(desc[k]));
            i++;
        }

        //set column width
        sheet.setColumnWidth(0,(10*500));
        sheet.setColumnWidth(1,(20*300));
        sheet.setColumnWidth(2,(20*300));
        sheet.setColumnWidth(3,(20*300));
//        sheet.setColumnWidth(4,(20*300));


        File file = new File(getExternalFilesDir(null),"AuthenticationLogFile.xls");
        FileOutputStream outputStream =null;



        try {
            outputStream=new FileOutputStream(file);
            wb.write(outputStream);
            // Toast.makeText(mcontext,"OK",Toast.LENGTH_LONG).show();
        } catch (java.io.IOException e) {
            e.printStackTrace();

            //  Toast.makeText(mcontext,"NO OK",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public boolean validate() {



        boolean truefalse = false;

        today = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.format(today);

        if(startDate.getText().toString().matches("")){
            Toast.makeText(FileDownload.this,"Do not leave it blank",Toast.LENGTH_SHORT).show();
            startDate.requestFocus();
            truefalse=false;
        }
        else if (endDate.getText().toString().matches("")){
            Toast.makeText(FileDownload.this,"Do not leave it blank",Toast.LENGTH_SHORT).show();
            endDate.requestFocus();
            truefalse=false;
        }

        else if (start.after(today) || end.after(today)){
            Toast.makeText(FileDownload.this,"ERROR!!! Date should not exceed today date",Toast.LENGTH_SHORT).show();
            truefalse=false;
        }
        else if (end.before(start)){
            Toast.makeText(FileDownload.this,"ERROR!!! End Date should not before Start Date",Toast.LENGTH_SHORT).show();
            truefalse=false;
        }
        else{
            truefalse=true;
        }

        return truefalse;

    }
}
