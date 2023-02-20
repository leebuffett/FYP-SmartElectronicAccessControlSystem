package com.example.smartelectronicaccesscontrolsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class staff_management extends AppCompatActivity implements View.OnClickListener{
    FirebaseAuth myfirebase= FirebaseAuth.getInstance();
    private CardView add_emp_card, edit_emp_card, dlt_emp_card;
    private Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_management);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Employee Management");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        add_emp_card = (CardView) findViewById(R.id.add_employee_card);
        edit_emp_card = (CardView) findViewById(R.id.edit_employee_card);
        dlt_emp_card = (CardView) findViewById(R.id.delete_employee_card);


        add_emp_card.setOnClickListener(this);
        edit_emp_card.setOnClickListener(this);
        dlt_emp_card.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_employee_card:
                i = new Intent(staff_management.this, add_edit_staff.class);
                i.putExtra("activity","addStaff");
                startActivity(i);break;
            case R.id.edit_employee_card:
                i = new Intent(staff_management.this, staff_list.class);
                i.putExtra("activityname","edit");
                startActivity(i);break;
            case R.id.delete_employee_card:
                i = new Intent(staff_management.this, staff_list.class);
                i.putExtra("activityname","delete");
                startActivity(i);break;
            default:break;
        }
    }
}
