package com.example.nirob;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

public class ProfileEditActivity extends AppCompatActivity {

    private Button bt_p_save, bt_p_last_bl_do_dt;
    private EditText et_p_full_nam, et_p_roll_no;
    private Spinner s_p_gen, s_p_bl_grp, s_p_dist;
    private Switch pe_s_don_bl, pe_s_wan_don;
    private FirebaseAuth p_firebaseAuth;
    private FirebaseUser p_firebaseUser;
    private FirebaseDatabase pe_firebaseDatabase;

    private LinearLayout lay_pe_don_bl;

    private ProgressBar p_e_pb;

    private String p_last_bl_do_dt = "", pe_email, pe_mob_no;

    private boolean pe_don_bl, pe_wan_don;

    private long pe_last_bl_do_ms = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Edit Profile");

        et_p_full_nam = findViewById(R.id.p_e_full_nam);
        et_p_roll_no = findViewById(R.id.p_e_roll_no);
        bt_p_last_bl_do_dt = findViewById(R.id.bt_p_e_last_bl_do_dt);

        s_p_gen = findViewById(R.id.e_gen);
        s_p_bl_grp = findViewById(R.id.e_bl_grp);
        s_p_dist = findViewById(R.id.e_dist);
        pe_s_don_bl = findViewById(R.id.s_p_e_don_bl);
        pe_s_wan_don = findViewById(R.id.s_p_e_wan_don);
        lay_pe_don_bl = findViewById(R.id.e_don_bl);

        p_e_pb = findViewById(R.id.pb_p_e);

        p_firebaseAuth = FirebaseAuth.getInstance();
        p_firebaseUser = p_firebaseAuth.getCurrentUser();

        pe_firebaseDatabase = FirebaseDatabase.getInstance();

        final String userid = p_firebaseUser.getUid();


        pe_firebaseDatabase.getReference("Users").child(userid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        et_p_full_nam.setText(user.u02_full_name);


                        ArrayAdapter<String> gen_adapter = (ArrayAdapter<String>) s_p_gen.getAdapter();
                        int gen_position = gen_adapter.getPosition(user.u03_gender);
                        s_p_gen.setSelection(gen_position);

                        et_p_roll_no.setText(user.u04_roll_no);

                        pe_email = user.u05_email;

                        pe_mob_no = user.u09_mobile_no;

                        ArrayAdapter<String> bl_adapter = (ArrayAdapter<String>) s_p_bl_grp.getAdapter();
                        int bl_position = bl_adapter.getPosition(user.u06_blood_group);
                        s_p_bl_grp.setSelection(bl_position);

                        if(user.u11_donated_blood){
                            pe_don_bl = true;
                            pe_s_don_bl.setChecked(true);
                            p_last_bl_do_dt = user.u07_last_blood_donation_date;

                            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                            Date date = null;
                            try {
                                date = format.parse(user.u07_last_blood_donation_date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy");

                            pe_last_bl_do_ms = date.getTime();
                            bt_p_last_bl_do_dt.setText(format1.format(date));
                            bt_p_last_bl_do_dt.setVisibility(View.VISIBLE);

                        }else {
                            pe_don_bl = false;
                            lay_pe_don_bl.setVisibility(View.VISIBLE);
                        }

                        if(user.u12_wanna_donate){
                            pe_wan_don = true;
                            pe_s_wan_don.setChecked(true);
                        }else {
                            pe_wan_don = false;
                            pe_s_wan_don.setChecked(false);
                        }

                        ArrayAdapter<String> dist_adapter = (ArrayAdapter<String>) s_p_dist.getAdapter();
                        int dist_position = dist_adapter.getPosition(user.u10_district);
                        s_p_dist.setSelection(dist_position);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        pe_s_don_bl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bt_p_last_bl_do_dt.setVisibility(View.VISIBLE);
                }else {
                    bt_p_last_bl_do_dt.setVisibility(View.GONE);
                }
            }
        });


        bt_p_last_bl_do_dt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                DatePickerDialog picker = new DatePickerDialog(ProfileEditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                p_last_bl_do_dt = dayOfMonth + "-" + (monthOfYear+1) + "-" + year;

                                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                Date date = null;
                                try {
                                    date = format.parse(p_last_bl_do_dt);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy");

                                pe_last_bl_do_ms = date.getTime();
                                bt_p_last_bl_do_dt.setText(format1.format(date));
                                bt_p_last_bl_do_dt.setTextColor(Color.BLACK);


                            }
                        }, year, month, day);
                picker.show();

            }
        });


        bt_p_save = findViewById(R.id.e_save);

        bt_p_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUser();

            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                proe_back();

            }
        };
        ProfileEditActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);

    }


    @Override
    public boolean onSupportNavigateUp() {

        proe_back();

        return true;
    }



    public void proe_back(){

        AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(ProfileEditActivity.this);
        alertdialog1.setMessage("Do you want to Save?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        updateUser();

                    }})
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }})
                .setTitle("Alert");
        alertdialog1.show();

    }



    private void updateUser(){

        final String full_name = et_p_full_nam.getText().toString().trim();
        if(full_name.length()<3){
            et_p_full_nam.setError("At Least 3 Chars!");
            et_p_full_nam.requestFocus();
            return;
        }


        final String gender = s_p_gen.getSelectedItem().toString();
        if(s_p_gen.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select Correct Gender!", Toast.LENGTH_LONG).show();
            return;
        }

        final String roll_no = et_p_roll_no.getText().toString().trim();
        if(roll_no.isEmpty()){
            et_p_roll_no.setError("Required!");
            et_p_roll_no.requestFocus();
            return;
        }

        if(roll_no.length() != 7 || Integer.parseInt(roll_no.substring(2,4))>13 || Integer.parseInt(roll_no.substring(4,7))>180){
            et_p_roll_no.setError("Invalid Roll!");
            et_p_roll_no.requestFocus();
            return;
        }


        final String blood_group = s_p_bl_grp.getSelectedItem().toString();
        if(s_p_bl_grp.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select Correct Blood Group!", Toast.LENGTH_LONG).show();
            return;
        }

        if(pe_s_don_bl.isChecked()){
            pe_don_bl = true;
            if(p_last_bl_do_dt.equals("")){
                bt_p_last_bl_do_dt.setText("Last Blood Donation Date!");
                bt_p_last_bl_do_dt.setTextColor(Color.RED);
                return;
            }
        }else {
            pe_don_bl = false;
        }

        if(pe_s_wan_don.isChecked()){
            pe_wan_don = true;
        }else {
            pe_wan_don = false;
        }


        final String district = s_p_dist.getSelectedItem().toString();
        if(s_p_dist.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Select Correct District!", Toast.LENGTH_LONG).show();
            return;
        }

        p_e_pb.setVisibility(View.VISIBLE);

        String upuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa dd-MM-yyyy");
        Date date = Calendar.getInstance().getTime();
        final String currentTime = simpleDateFormat.format(date);

        User userd = new User(upuserid, full_name, gender, roll_no, pe_email, blood_group, p_last_bl_do_dt, pe_last_bl_do_ms, pe_mob_no, district, pe_don_bl, pe_wan_don, true, currentTime);

        pe_firebaseDatabase.getReference("Users").child(upuserid).setValue(userd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                p_e_pb.setVisibility(View.INVISIBLE);
                Toast.makeText(ProfileEditActivity.this, "Updated Successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ProfileEditActivity.this, MainActivity.class);
                intent.putExtra("key", R.id.menu_profile);
                startActivity(intent);
                finish();
            }
        });


    }


}
