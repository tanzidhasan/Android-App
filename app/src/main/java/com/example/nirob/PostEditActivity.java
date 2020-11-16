package com.example.nirob;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
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

public class PostEditActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private EditText et_pe_bl_am, et_pe_ab_dis, et_pe_hos_nam, et_pe_mob_no, et_pe_full_name;
    private Spinner sp_pe_bl_grp, sp_pe_dist;
    private Button bt_pe_edit, bt_pe_dd;
    private Switch s_pe_man;
    private ProgressBar pb_pe;
    private String pe_deadline = "", pe_deadline_error = "";
    private String pe_postid, pe_userid, pe_full_nam;
    private boolean pe_man = false;
    private long pe_deadline_mili = 0;

    private FirebaseAuth poe_firebaseAuth;
    private FirebaseUser poe_firebaseUser;
    private FirebaseDatabase poe_firebaseDatabase;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);

        poe_firebaseAuth = FirebaseAuth.getInstance();
        poe_firebaseUser = poe_firebaseAuth.getCurrentUser();
        poe_firebaseDatabase = FirebaseDatabase.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Edit Post");

        et_pe_bl_am = findViewById(R.id.et_post_edit_bl_am);
        et_pe_ab_dis = findViewById(R.id.et_post_edit_ab_dis);
        et_pe_hos_nam = findViewById(R.id.et_post_edit_hos_nam);
        et_pe_mob_no = findViewById(R.id.et_post_edit_mob_no);

        sp_pe_bl_grp = findViewById(R.id.sp_post_edit_bl_grp);
        sp_pe_dist = findViewById(R.id.sp_post_edit_dist);

        s_pe_man = findViewById(R.id.pe_man);

        bt_pe_edit = findViewById(R.id.bt_post_edit);
        bt_pe_dd = findViewById(R.id.bt_post_edit_dd);

        pb_pe = findViewById(R.id.pb_post_edit);


        Bundle extras = getIntent().getExtras();
        if(extras != null){

            pe_postid = extras.getString("postid");

            pe_userid = extras.getString("userid");

            pe_full_nam = extras.getString("full_nam");

            ArrayAdapter<String> bl_adapter = (ArrayAdapter<String>) sp_pe_bl_grp.getAdapter();
            int bl_position = bl_adapter.getPosition(extras.getString("bl_grp"));
            sp_pe_bl_grp.setSelection(bl_position);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd-MM-yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm aa");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMM dd, yyyy");

            et_pe_bl_am.setText(extras.getString("bl_am"));

            et_pe_ab_dis.setText(extras.getString("ab_dis"));

            et_pe_hos_nam.setText(extras.getString("hos_nam"));

            ArrayAdapter<String> dist_adapter = (ArrayAdapter<String>) sp_pe_dist.getAdapter();
            int dist_position = dist_adapter.getPosition(extras.getString("dist"));
            sp_pe_dist.setSelection(dist_position);

            String deadline = extras.getString("dl");
            pe_deadline = extras.getString("dl");

            Date date1 = null;
            try {
                date1 = simpleDateFormat.parse(deadline);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            pe_deadline_mili = date1.getTime();
            bt_pe_dd.setText(simpleDateFormat2.format(date1) + " at " + simpleDateFormat1.format(date1));

            et_pe_mob_no.setText(extras.getString("mob_no"));

            if(extras.getBoolean("mana")){
                pe_man = true;
                s_pe_man.setChecked(true);
            }

        }


        bt_pe_dd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                final int day = cldr.get(Calendar.DAY_OF_MONTH);
                final int month = cldr.get(Calendar.MONTH);
                final int year = cldr.get(Calendar.YEAR);
                final int hour = cldr.get(Calendar.HOUR_OF_DAY);
                final int minute = cldr.get(Calendar.MINUTE);

                final DatePickerDialog picker = new DatePickerDialog(PostEditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int yearofdecade, int monthOfYear, int dayOfMonth) {

                                if(yearofdecade > year || (yearofdecade == year && monthOfYear > month) || (yearofdecade == year && monthOfYear == month && dayOfMonth >= day)){

                                    if(!pe_deadline_error.equals("") && (yearofdecade == year && monthOfYear == month && dayOfMonth == day)){
                                        pe_deadline = "";
                                        bt_pe_dd.setText("Not Before Now!");
                                        bt_pe_dd.setTextColor(Color.RED);

                                    }else {

                                        pe_deadline += " "+dayOfMonth + "-" + (monthOfYear+1) + "-" + yearofdecade;

                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm dd-MM-yyyy");
                                        Date date = null;
                                        try {
                                            date = format.parse(pe_deadline);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        pe_deadline_mili = date.getTime();

                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy");
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format2 = new SimpleDateFormat("hh:mm aa");

                                        bt_pe_dd.setText(format1.format(date) + " at " + format2.format(date));
                                        bt_pe_dd.setTextColor(Color.BLACK);

                                    }

                                }else {
                                    pe_deadline = "";
                                    bt_pe_dd.setText("Not Before Now!");
                                    bt_pe_dd.setTextColor(Color.RED);
                                }

                            }
                        }, year, month, day);
                picker.show();


                TimePickerDialog timePickerDialog = new TimePickerDialog(PostEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteofhour) {
                        if(hourOfDay > hour || (hourOfDay == hour && minuteofhour > minute+2)){
                            pe_deadline = (hourOfDay + ":" +minuteofhour);
                        }else {
                            pe_deadline_error = "Now";
                            pe_deadline = (hourOfDay + ":" +minuteofhour);
                        }
                    }
                }, hour, minute, false);
                timePickerDialog.show();



            }
        });

        bt_pe_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePost();
            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                pe_back();
            }
        };
        PostEditActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);

    }


    @Override
    public boolean onSupportNavigateUp() {

        pe_back();

        return true;
    }


    public void pe_back(){

        AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(PostEditActivity.this);
        alertdialog1.setMessage("Do you want to Save?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updatePost();
                    }})
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }})
                .setTitle("Alert");
        alertdialog1.show();

    }



    public void updatePost(){

        final String blood_group = sp_pe_bl_grp.getSelectedItem().toString();
        if(sp_pe_bl_grp.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select Correct Blood Group!", Toast.LENGTH_LONG).show();
            return;
        }

        final String blood_amount = et_pe_bl_am.getText().toString().trim();
        if(blood_amount.isEmpty()){
            et_pe_bl_am.setError("Required!");
            et_pe_bl_am.requestFocus();
            return;
        }

        final String about_disease = et_pe_ab_dis.getText().toString().trim();
        if(about_disease.isEmpty()){
            et_pe_ab_dis.setError("Required!");
            et_pe_ab_dis.requestFocus();
            return;
        }

        final String hospital_name = et_pe_hos_nam.getText().toString().trim();
        if(hospital_name.isEmpty()){
            et_pe_hos_nam.setError("Required!");
            et_pe_hos_nam.requestFocus();
            return;
        }

        final String district = sp_pe_dist.getSelectedItem().toString();
        if(sp_pe_dist.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select Correct District!", Toast.LENGTH_LONG).show();
            return;
        }

        if(pe_deadline.equals("")){
            bt_pe_dd.setText("Deadline!");
            bt_pe_dd.setTextColor(Color.RED);
            return;
        }


        final String mobile_no = et_pe_mob_no.getText().toString().trim();
        if(mobile_no.isEmpty()){
            et_pe_mob_no.setError("Required!");
            et_pe_mob_no.requestFocus();
            return;
        }

        if(mobile_no.length() != 11){
            et_pe_mob_no.setError("11 Digit Mobile No. Required!");
            et_pe_mob_no.requestFocus();
            return;
        }

        if(Integer.parseInt(mobile_no.substring(0,2)) != 1 || Integer.parseInt(mobile_no.substring(2,3)) < 3){
            et_pe_mob_no.setError("Invalid Number!");
            et_pe_mob_no.requestFocus();
            return;
        }

        if(s_pe_man.isChecked()){
            pe_man = true;
        }else{
            pe_man = false;
        }


        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa dd-MM-yyyy");
        Date ct = Calendar.getInstance().getTime();
        long ctml = ct.getTime();
        long ctml_dec = -(ct.getTime());
        final String currentTime = simpleDateFormat.format(ct);

        Post post = new Post(pe_postid, pe_userid, pe_full_nam, blood_group, blood_amount, about_disease, hospital_name, district, pe_deadline, pe_deadline_mili, mobile_no, currentTime, ctml, ctml_dec,  pe_man);

        poe_firebaseDatabase.getReference("Posts").child(pe_postid).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(PostEditActivity.this, PostDetailsActivity.class);
                intent.putExtra("from_page", "post_edit");
                intent.putExtra("postid", pe_postid);
                intent.putExtra("userid", pe_userid);
                intent.putExtra("full_nam", pe_full_nam);
                intent.putExtra("bl_grp", blood_group);
                intent.putExtra("bl_am", blood_amount);
                intent.putExtra("ab_dis", about_disease);
                intent.putExtra("hos_nam", hospital_name);
                intent.putExtra("dist", district);
                intent.putExtra("dl", pe_deadline);
                intent.putExtra("mob_no", mobile_no);
                intent.putExtra("post_time", currentTime);
                intent.putExtra("mana", pe_man);
                startActivity(intent);
                finish();
            }
        });

    }


    public void status(final boolean status){
        if(poe_firebaseUser != null){
            poe_firebaseDatabase.getReference("Users").child(poe_firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    poe_firebaseDatabase.getReference("Users").child(poe_firebaseUser.getUid()).setValue(user);
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
