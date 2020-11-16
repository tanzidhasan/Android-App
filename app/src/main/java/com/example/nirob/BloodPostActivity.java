package com.example.nirob;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nirob.Object.Post;
import com.example.nirob.Object.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BloodPostActivity extends AppCompatActivity {

    private EditText et_bp_bl_am, et_bp_ab_dis, et_bp_hos_nam, et_bp_mob_no, et_bp_full_name;
    private Spinner sp_bp_bl_grp, sp_bp_dist;
    private Button bt_bp_post, bt_bp_dd;
    private ProgressBar pb_bp;
    private String bl_post_deadline = "", bl_post_deadline_error = "";
    private FirebaseDatabase bp_firebaseDatabase;
    private FirebaseAuth bp_firebaseAuth;
    private FirebaseUser bp_firebaseUser;
    private long bl_post_deadline_mili = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_post);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Post New Request");

        bp_firebaseAuth = FirebaseAuth.getInstance();
        bp_firebaseUser = bp_firebaseAuth.getCurrentUser();
        bp_firebaseDatabase = FirebaseDatabase.getInstance();

        et_bp_bl_am = findViewById(R.id.et_bl_post_bl_am);
        et_bp_ab_dis = findViewById(R.id.et_bl_post_ab_dis);
        et_bp_hos_nam = findViewById(R.id.et_bl_post_hos_nam);
        et_bp_mob_no  =findViewById(R.id.et_bl_post_mob_no);

        sp_bp_bl_grp = findViewById(R.id.sp_bl_post_bl_grp);
        sp_bp_dist = findViewById(R.id.sp_bl_post_dist);

        bt_bp_dd = findViewById(R.id.bt_bl_post_dd);

        bt_bp_post = findViewById(R.id.bt_bl_post);

        pb_bp = findViewById(R.id.pb_bl_post);

        et_bp_full_name = findViewById(R.id.bp_full_name);

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        bp_firebaseDatabase.getReference("Users").child(userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                et_bp_full_name.setText(user.u02_full_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        bt_bp_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRequest();
            }
        });


        bt_bp_dd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                final int day = cldr.get(Calendar.DAY_OF_MONTH);
                final int month = cldr.get(Calendar.MONTH);
                final int year = cldr.get(Calendar.YEAR);
                final int hour = cldr.get(Calendar.HOUR_OF_DAY);
                final int minute = cldr.get(Calendar.MINUTE);

                final DatePickerDialog picker = new DatePickerDialog(BloodPostActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int yearofdecade, int monthOfYear, int dayOfMonth) {

                                if(yearofdecade > year || (yearofdecade == year && monthOfYear > month) || (yearofdecade == year && monthOfYear == month && dayOfMonth >= day)){

                                    if(!bl_post_deadline_error.equals("") && (yearofdecade == year && monthOfYear == month && dayOfMonth == day)){
                                        bl_post_deadline = "";
                                        bt_bp_dd.setText("Not Before Now!");
                                        bt_bp_dd.setTextColor(Color.RED);

                                    }else {

                                        bl_post_deadline += " "+dayOfMonth + "-" + (monthOfYear+1) + "-" + yearofdecade;

                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm dd-MM-yyyy");
                                        Date date = null;
                                        try {
                                            date = format.parse(bl_post_deadline);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        bl_post_deadline_mili = date.getTime();

                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy");
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format2 = new SimpleDateFormat("hh:mm aa");

                                        bt_bp_dd.setText(format1.format(date) + " at " + format2.format(date));
                                        bt_bp_dd.setTextColor(Color.BLACK);

                                    }

                                }else {
                                    bl_post_deadline = "";
                                    bt_bp_dd.setText("Not Before Now!");
                                    bt_bp_dd.setTextColor(Color.RED);
                                }

                            }
                        }, year, month, day);
                picker.show();


                TimePickerDialog timePickerDialog = new TimePickerDialog(BloodPostActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteofhour) {
                        if(hourOfDay > hour || (hourOfDay == hour && minuteofhour > minute+2)){
                            bl_post_deadline = (hourOfDay + ":" +minuteofhour);
                        }else {
                            bl_post_deadline_error = "Now";
                            bl_post_deadline = (hourOfDay + ":" +minuteofhour);
                        }

                    }
                }, hour, minute, false);
                timePickerDialog.show();

            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                bp_back();

            }
        };
        BloodPostActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public boolean onSupportNavigateUp() {

        bp_back();

        return true;
    }


    public void bp_back(){

        if(sp_bp_bl_grp.getSelectedItemPosition() == 0 && et_bp_bl_am.getText().toString().trim().isEmpty() && et_bp_ab_dis.getText().toString().trim().isEmpty() && et_bp_hos_nam.getText().toString().trim().isEmpty() && sp_bp_dist.getSelectedItemPosition() == 0 && bl_post_deadline.equals("") && et_bp_mob_no.getText().toString().trim().isEmpty()){
            Intent intent = new Intent(BloodPostActivity.this, MainActivity.class);
            intent.putExtra("key", R.id.menu_blood_request);
            startActivity(intent);
            finish();
        }else {
            AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(BloodPostActivity.this);
            alertdialog1.setMessage("Do you want to Discard?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(BloodPostActivity.this, MainActivity.class);
                            intent.putExtra("key", R.id.menu_blood_request);
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


    public void postRequest(){

        final String blood_group = sp_bp_bl_grp.getSelectedItem().toString();
        if(sp_bp_bl_grp.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select Correct Blood Group!", Toast.LENGTH_LONG).show();
            return;
        }

        final String blood_amount = et_bp_bl_am.getText().toString().trim();
        if(blood_amount.isEmpty()){
            et_bp_bl_am.setError("Required!");
            et_bp_bl_am.requestFocus();
            return;
        }

        final String about_disease = et_bp_ab_dis.getText().toString().trim();
        if(about_disease.isEmpty()){
            et_bp_ab_dis.setError("Required!");
            et_bp_ab_dis.requestFocus();
            return;
        }

        final String hospital_name = et_bp_hos_nam.getText().toString().trim();
        if(hospital_name.isEmpty()){
            et_bp_hos_nam.setError("Required!");
            et_bp_hos_nam.requestFocus();
            return;
        }

        final String district = sp_bp_dist.getSelectedItem().toString();
        if(sp_bp_dist.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select Correct District!", Toast.LENGTH_LONG).show();
            return;
        }

        if(bl_post_deadline.equals("")){
            bt_bp_dd.setText("Deadline!");
            bt_bp_dd.setTextColor(Color.RED);
            return;
        }


        final String mobile_no = et_bp_mob_no.getText().toString().trim();
        if(mobile_no.isEmpty()){
            et_bp_mob_no.setError("Required!");
            et_bp_mob_no.requestFocus();
            return;
        }

        if(mobile_no.length() != 11){
            et_bp_mob_no.setError("11 Digit Mobile No. Required!");
            et_bp_mob_no.requestFocus();
            return;
        }

        if(Integer.parseInt(mobile_no.substring(0,2)) != 1 || Integer.parseInt(mobile_no.substring(2,3)) < 3){
            et_bp_mob_no.setError("Invalid Number!");
            et_bp_mob_no.requestFocus();
            return;
        }

        pb_bp.setVisibility(View.VISIBLE);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa dd-MM-yyyy");

        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Date ct = Calendar.getInstance().getTime();
        long ctml = ct.getTime();
        long ctml_dec = -(ct.getTime());
        final String currentTime = simpleDateFormat.format(ct);

        final String full_name = et_bp_full_name.getText().toString().trim();


        final String postid = bp_firebaseDatabase.getReference("Posts").push().getKey();


        Post post = new Post(postid, userid, full_name, blood_group, blood_amount, about_disease, hospital_name, district, bl_post_deadline, bl_post_deadline_mili, mobile_no, currentTime, ctml, ctml_dec, false);

        bp_firebaseDatabase.getReference("Posts").child(postid).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pb_bp.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(BloodPostActivity.this, PostDetailsActivity.class);
                intent.putExtra("from_page", "blood_post");
                intent.putExtra("postid", postid);
                intent.putExtra("userid", userid);
                intent.putExtra("full_nam", full_name);
                intent.putExtra("bl_grp", blood_group);
                intent.putExtra("bl_am", blood_amount);
                intent.putExtra("ab_dis", about_disease);
                intent.putExtra("hos_nam", hospital_name);
                intent.putExtra("dist", district);
                intent.putExtra("dl", bl_post_deadline);
                intent.putExtra("mob_no", mobile_no);
                intent.putExtra("post_time", currentTime);
                intent.putExtra("mana", false);
                startActivity(intent);
                finish();
            }
        });


    }


    public void status(final boolean status){
        if(bp_firebaseUser != null){
            bp_firebaseDatabase.getReference("Users").child(bp_firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    user.u13_status = status;

                    if(status){
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa dd-MM-yyyy");
                        Date date = Calendar.getInstance().getTime();
                        final String currentTime = simpleDateFormat.format(date);
                        user.u14_last_seen = currentTime;
                    }

                    bp_firebaseDatabase.getReference("Users").child(bp_firebaseUser.getUid()).setValue(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        status(true);
    }


    @Override
    protected void onPause() {
        super.onPause();
        status(false);
    }



}
