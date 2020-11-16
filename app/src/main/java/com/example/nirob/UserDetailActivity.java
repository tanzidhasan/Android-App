package com.example.nirob;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserDetailActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private TextView ud_t_own_info, ud_t_full_nam, ud_t_gen, ud_t_roll_no, ud_t_bl_grp, ud_t_dist;
    private Button ud_bt_snd_msg;
    private String ud_postid, ud_userid, ud_from_page;
    private FirebaseAuth ud_firebaseAuth;
    private FirebaseUser ud_firebaseUser;
    private FirebaseDatabase ud_firebaseDatabase;
    private LinearLayout layout_ud;
    private ProgressBar ud_pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        ud_firebaseAuth = FirebaseAuth.getInstance();
        ud_firebaseUser = ud_firebaseAuth.getCurrentUser();
        ud_firebaseDatabase = FirebaseDatabase.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("User Detail");

        ud_t_own_info = findViewById(R.id.t_ud_own_info);

        ud_t_full_nam = findViewById(R.id.t_ud_full_nam);
        

        ud_t_gen = findViewById(R.id.t_ud_gen);

        ud_t_roll_no = findViewById(R.id.t_ud_roll_no);

        ud_t_bl_grp = findViewById(R.id.t_ud_bl_grp);

        ud_t_dist = findViewById(R.id.t_ud_dist);

        ud_pb = findViewById(R.id.pb_ud);

        ud_bt_snd_msg = findViewById(R.id.bt_ud_snd_msg);

        layout_ud = findViewById(R.id.ud_layout);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            ud_from_page = extras.getString("from_page");
            ud_postid = extras.getString("postid");
            ud_userid = extras.getString("userid");
        }


        ud_firebaseDatabase.getReference("Users").child(ud_userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                ud_t_own_info.setText(user.u02_full_name + "'s Info");
                ud_t_full_nam.setText(user.u02_full_name);
                ud_t_gen.setText(user.u03_gender);
                ud_t_roll_no.setText(user.u04_roll_no);
                ud_t_bl_grp.setText(user.u06_blood_group);
                ud_t_dist.setText(user.u10_district);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ud_pb.setVisibility(View.GONE);
                        layout_ud.setVisibility(View.VISIBLE);
                    }
                },500);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ud_bt_snd_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserDetailActivity.this, ChatActivity.class);
                intent.putExtra("userid", ud_userid);
                startActivity(intent);
                finish();
            }
        });


        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                ud_back();

            }
        };
        UserDetailActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);

    }


    @Override
    public boolean onSupportNavigateUp() {

        ud_back();

        return true;
    }

    public void ud_back(){
        if(ud_from_page.equals("post_detail")){

            ud_firebaseDatabase.getReference("Posts").child(ud_postid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);
                    Intent intent = new Intent(UserDetailActivity.this, PostDetailsActivity.class);
                    intent.putExtra("from_page", "user_detail");
                    intent.putExtra("postid", post.p01_post_id);
                    intent.putExtra("userid", post.p02_user_id);
                    intent.putExtra("full_nam", post.p03_full_name);
                    intent.putExtra("bl_grp", post.p04_blood_group);
                    intent.putExtra("bl_am", post.p05_blood_amount);
                    intent.putExtra("ab_dis", post.p06_about_disease);
                    intent.putExtra("hos_nam", post.p07_hospital_name);
                    intent.putExtra("dist", post.p08_district);
                    intent.putExtra("dl", post.p09_deadline);
                    intent.putExtra("mob_no", post.p11_phone_number);
                    intent.putExtra("post_time", post.p12_post_time);
                    intent.putExtra("mana", post.p15_managed);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else {
            Intent intent = new Intent(UserDetailActivity.this, MainActivity.class);
            intent.putExtra("key", R.id.menu_donor_details);
            startActivity(intent);
            finish();
        }
    }


    public void status(final boolean status){
        if(ud_firebaseUser != null){
            ud_firebaseDatabase.getReference("Users").child(ud_firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    ud_firebaseDatabase.getReference("Users").child(ud_firebaseUser.getUid()).setValue(user);
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
