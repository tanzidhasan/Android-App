package com.example.nirob;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nirob.Object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;


public class SignUpActivity extends AppCompatActivity {

    public static final Pattern PASSWORD_PATTERNS
            = Pattern.compile("^" +
            "(?=.*[0-9])" +
            "(?=.*[a-z])" +
            "(?=.*[A-Z])" +
            "(?=.*[!@#$%^&*()_+-=])" +
            "(?=\\S+$)" +
            ".{6,}" +
            "$"
    );

    private TextView si_up_t_log;
    private EditText si_up_et_full_nam, si_up_et_roll_no, si_up_et_email, si_up_et_pass, si_up_et_con_pass, si_up_et_mob_no;
    private Spinner si_up_sp_bl_grp, si_up_sp_dist, si_up_sp_gen;
    private Button si_up_bt_sub, si_up_bt_last_bl_do_dt;
    private ProgressBar si_up_pb;
    private String si_up_last_bl_do_dt = "";
    private Switch si_up_s_don_bl,si_up_s_wan_don;
    private FirebaseAuth su_firebaseAuth;
    private boolean si_up_don_bl, si_up_wan_don;
    private FirebaseDatabase su_firebaseDatabase;
    private long si_up_last_bl_do_ms = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Sign Up");

        su_firebaseAuth = FirebaseAuth.getInstance();

        su_firebaseDatabase = FirebaseDatabase.getInstance();

        si_up_t_log = findViewById(R.id.t_si_up_log);
        si_up_et_full_nam = findViewById(R.id.et_si_up_full_nam);
        si_up_sp_gen = findViewById(R.id.sp_si_up_gen);

        si_up_et_roll_no = findViewById(R.id.et_si_up_roll_no);
        si_up_et_email = findViewById(R.id.et_si_up_email);
        si_up_et_pass = findViewById(R.id.et_si_up_pass);
        si_up_et_con_pass = findViewById(R.id.et_si_up_con_pass);
        si_up_sp_bl_grp = findViewById(R.id.sp_si_up_bl_grp);
        si_up_bt_last_bl_do_dt = findViewById(R.id.bt_si_up_last_bl_do_dt);
        si_up_et_mob_no = findViewById(R.id.et_si_up_mob_no);
        si_up_sp_dist = findViewById(R.id.sp_si_up_dist);
        si_up_bt_sub = findViewById(R.id.bt_si_up_sub);
        si_up_pb = findViewById(R.id.pb_si_up);
        si_up_s_don_bl = findViewById(R.id.s_si_up_don_bl);
        si_up_s_wan_don = findViewById(R.id.s_si_up_wan_don);

        si_up_s_don_bl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    si_up_bt_last_bl_do_dt.setVisibility(View.VISIBLE);
                }else{
                    si_up_bt_last_bl_do_dt.setVisibility(View.GONE);
                }
            }
        });

        si_up_t_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        si_up_bt_last_bl_do_dt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                DatePickerDialog picker = new DatePickerDialog(SignUpActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                si_up_last_bl_do_dt = dayOfMonth + "-" + (monthOfYear+1) + "-" + year;

                                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = null;
                                try {
                                    date = format.parse(si_up_last_bl_do_dt);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy");

                                si_up_last_bl_do_ms = date.getTime();
                                si_up_bt_last_bl_do_dt.setText(format1.format(date));
                                si_up_bt_last_bl_do_dt.setTextColor(Color.BLACK);


                            }
                        }, year, month, day);
                picker.show();
            }
        });


        si_up_bt_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });



        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                su_back();

            }
        };
        SignUpActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);


    }


    @Override
    public boolean onSupportNavigateUp() {

        su_back();

        return true;
    }


    public void su_back(){

        if(si_up_et_full_nam.getText().toString().trim().isEmpty() && si_up_sp_gen.getSelectedItemPosition() == 0 && si_up_et_roll_no.getText().toString().trim().isEmpty() && si_up_et_email.getText().toString().trim().isEmpty() && si_up_et_pass.getText().toString().trim().isEmpty() && si_up_et_con_pass.getText().toString().trim().isEmpty() && si_up_sp_bl_grp.getSelectedItemPosition() == 0 && si_up_last_bl_do_dt.equals("") && si_up_et_mob_no.getText().toString().trim().isEmpty() && si_up_sp_dist.getSelectedItemPosition() == 0){
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            intent.putExtra("key", R.id.menu_home);
            startActivity(intent);
            finish();
        }else {
            AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(SignUpActivity.this);
            alertdialog1.setMessage("Do you want to Discard?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.putExtra("key", R.id.menu_home);
                            startActivity(intent);
                            finish();

                        }})
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }})
                    .setTitle("Alert");
            alertdialog1.show();
        }

    }



    @SuppressLint("SetTextI18n")
    public void registerUser(){

        final String full_name = si_up_et_full_nam.getText().toString().trim();
        if(full_name.length()<3){
            si_up_et_full_nam.setError("At Least 3 Chars!");
            si_up_et_full_nam.requestFocus();
            return;
        }


        final String gender = si_up_sp_gen.getSelectedItem().toString();
        if(si_up_sp_gen.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select Correct Gender!", Toast.LENGTH_LONG).show();
            return;
        }

        final String roll_no = si_up_et_roll_no.getText().toString().trim();
        if(roll_no.isEmpty()){
            si_up_et_roll_no.setError("Required!");
            si_up_et_roll_no.requestFocus();
            return;
        }

        if(roll_no.length() != 7){
            si_up_et_roll_no.setError("7 Digit Roll No. Required!");
            si_up_et_roll_no.requestFocus();
            return;
        }

        if(Integer.parseInt(roll_no.substring(2,4))>13 || Integer.parseInt(roll_no.substring(4,7))>180){
            si_up_et_roll_no.setError("Invalid Roll!");
            si_up_et_roll_no.requestFocus();
            return;
        }


        final String email = si_up_et_email.getText().toString().trim();
        if(email.isEmpty()){
            si_up_et_email.setError("Required!");
            si_up_et_email.requestFocus();
            return;
        }
        if(!(Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            si_up_et_email.setError("Invalid Email!");
            si_up_et_email.requestFocus();
            return;
        }

        String password = si_up_et_pass.getText().toString().trim();
        if(password.isEmpty()){
            si_up_et_pass.setError("Required!");
            si_up_et_pass.requestFocus();
            return;
        }
        if(!PASSWORD_PATTERNS.matcher(password).matches()){
            si_up_et_pass.setError("Too Weak!");
            si_up_et_pass.requestFocus();
            return;
        }

        String confirm_password = si_up_et_con_pass.getText().toString().trim();
        if(!(confirm_password.equals(password))){
            si_up_et_con_pass.setError("Don't Match!");
            si_up_et_con_pass.requestFocus();
            return;
        }

        final String blood_group = si_up_sp_bl_grp.getSelectedItem().toString();
        if(si_up_sp_bl_grp.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select Correct Blood Group!", Toast.LENGTH_LONG).show();
            return;
        }

        if(si_up_s_don_bl.isChecked()){
            si_up_don_bl = true;
            if(si_up_last_bl_do_dt.equals("")){
                si_up_bt_last_bl_do_dt.setText("Last Blood Donation Date!");
                si_up_bt_last_bl_do_dt.setTextColor(Color.RED);
                return;
            }
        }else {
            si_up_don_bl = false;
        }

        if(si_up_s_wan_don.isChecked()){
            si_up_wan_don = true;
        }else {
            si_up_wan_don = false;
        }


        final String mobile_no = si_up_et_mob_no.getText().toString().trim();
        if(mobile_no.isEmpty()){
            si_up_et_mob_no.setError("Required!");
            si_up_et_mob_no.requestFocus();
            return;
        }

        if(mobile_no.length() != 11){
            si_up_et_mob_no.setError("11 Digit Mobile No. Required!");
            si_up_et_mob_no.requestFocus();
            return;
        }

        if(Integer.parseInt(mobile_no.substring(0,2)) != 1 || Integer.parseInt(mobile_no.substring(2,3)) < 3){
            si_up_et_mob_no.setError("Invalid Number!");
            si_up_et_mob_no.requestFocus();
            return;
        }


        final String district = si_up_sp_dist.getSelectedItem().toString();
        if(si_up_sp_dist.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select Correct District!", Toast.LENGTH_LONG).show();
            return;
        }
        si_up_pb.setVisibility(View.VISIBLE);

        su_firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    String userid = su_firebaseAuth.getCurrentUser().getUid();

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa dd-MM-yyyy");
                    Date date = Calendar.getInstance().getTime();
                    final String currentTime = simpleDateFormat.format(date);

                    User user = new User(userid, full_name, gender, roll_no, email, blood_group, si_up_last_bl_do_dt, si_up_last_bl_do_ms, mobile_no, district, si_up_don_bl, si_up_wan_don, true, currentTime);

                    su_firebaseDatabase.getReference("Users").child(userid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            si_up_pb.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUpActivity.this, "Successfully Registered.", Toast.LENGTH_LONG).show();
                            su_firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Please verify Email to Login.", Toast.LENGTH_LONG).show();
                                    su_firebaseAuth.signOut();
                                    Intent intent = new Intent(SignUpActivity.this, MobileNoVerificationActivity.class);
                                    intent.putExtra("mob_no", mobile_no);
                                    startActivity(intent);
                                    finish();

                                }
                              }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            si_up_pb.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUpActivity.this, "Error " + e, Toast.LENGTH_LONG).show();
                        }
                    });


                }else{
                    si_up_pb.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignUpActivity.this, "Error! "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

    }



}


