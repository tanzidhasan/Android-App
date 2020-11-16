package com.example.nirob;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nirob.Object.User;
import com.google.android.gms.tasks.OnFailureListener;
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

public class PostDetailsActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private TextView t_pd_full_nam, t_pd_post_time, t_pd_bl_grp, t_pd_bl_am, t_pd_ab_dis, t_pd_hos_nam, t_pd_dist, t_pd_dl, t_pd_mob_no;
    private String pd_postid, pd_userid, userid1, pd_full_nam, pd_post_time, pd_bl_grp, pd_bl_am, pd_ab_dis, pd_hos_nam, pd_dist, pd_dl, pd_mob_no;
    private String from_page;
    private boolean pd_man, pd_menu;

    private FirebaseAuth pd_firebaseAuth;
    private FirebaseUser pd_firebaseUser;
    private FirebaseDatabase pd_firebaseDatabase;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        pd_firebaseAuth = FirebaseAuth.getInstance();
        pd_firebaseUser = pd_firebaseAuth.getCurrentUser();
        pd_firebaseDatabase = FirebaseDatabase.getInstance();

        userid1 = FirebaseAuth.getInstance().getCurrentUser().getUid();

        t_pd_full_nam = findViewById(R.id.pd_t_full_nam);
        t_pd_post_time = findViewById(R.id.pd_t_post_time);
        t_pd_bl_grp = findViewById(R.id.pd_t_bl_grp);
        t_pd_bl_am = findViewById(R.id.pd_t_bl_am);
        t_pd_ab_dis = findViewById(R.id.pd_t_ab_dis);
        t_pd_hos_nam = findViewById(R.id.pd_t_hos_nam);
        t_pd_dist = findViewById(R.id.pd_t_dist);
        t_pd_dl = findViewById(R.id.pd_t_dl);
        t_pd_mob_no = findViewById(R.id.pd_t_mob_no);


        Bundle extras = getIntent().getExtras();
        if(extras != null){

            from_page = extras.getString("from_page");

            pd_postid = extras.getString("postid");

            pd_userid = extras.getString("userid");

            if(pd_userid.equals(userid1)){
                pd_menu = true;
            }else {
                pd_menu = false;
            }

            pd_full_nam = extras.getString("full_nam");

            actionBar.setTitle(pd_full_nam + "'s Post");

            t_pd_full_nam.setText(pd_full_nam);


            pd_post_time = extras.getString("post_time");
            String post_time = pd_post_time;

            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd-MM-yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm aa");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMM dd, yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("hh:mm aa dd-MM-yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat4 = new SimpleDateFormat("MMM dd");

            Date date = null;

            try {
                date = simpleDateFormat3.parse(post_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date currenttime = Calendar.getInstance().getTime();

            long diff = currenttime.getTime() - date.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            long diffyear = diffDays / 365;

            if(diffDays < 1){
                if(diffHours == 0){
                    if(diffMinutes == 1){
                        post_time = diffMinutes + " min";
                    }else {
                        post_time = diffMinutes + " mins";
                    }
                }else if(diffHours == 1){
                    post_time = diffHours + " hr";
                }else{
                    post_time = diffHours + " hrs";
                }
            }else if(diffyear == 0){
                post_time = simpleDateFormat4.format(date) + " at " + simpleDateFormat1.format(date);
            } else{
                post_time = simpleDateFormat2.format(date) + " at " + simpleDateFormat1.format(date);
            }

            t_pd_post_time.setText(post_time);

            pd_bl_grp = extras.getString("bl_grp");
            t_pd_bl_grp.setText(pd_bl_grp);

            pd_bl_am = extras.getString("bl_am");
            t_pd_bl_am.setText(pd_bl_am);


            pd_ab_dis = extras.getString("ab_dis");
            t_pd_ab_dis.setText(pd_ab_dis);

            pd_hos_nam = extras.getString("hos_nam");
            t_pd_hos_nam.setText(pd_hos_nam);

            pd_dist = extras.getString("dist");
            t_pd_dist.setText(pd_dist);

            pd_dl = extras.getString("dl");
            String deadline = pd_dl;

            Date date1 = null;
            try {
                date1 = simpleDateFormat.parse(deadline);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            t_pd_dl.setText(simpleDateFormat2.format(date1) + " at " + simpleDateFormat1.format(date1));

            pd_mob_no = extras.getString("mob_no");
            t_pd_mob_no.setText(pd_mob_no);

            pd_man = extras.getBoolean("mana");

        }


        t_pd_full_nam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(!pd_userid.equals(userid1)){
                    intent = new Intent(PostDetailsActivity.this, UserDetailActivity.class);
                    intent.putExtra("from_page", "post_detail");
                    intent.putExtra("userid", pd_userid);
                    intent.putExtra("postid", pd_postid);
                }else {
                    intent = new Intent(PostDetailsActivity.this, MainActivity.class);
                    intent.putExtra("key", R.id.menu_profile);
                }
                startActivity(intent);
                finish();
            }
        });



        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                pd_back();

            }
        };
        PostDetailsActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);


    }

    @Override
    public boolean onSupportNavigateUp() {

        pd_back();

        return true;

    }



    public void pd_back(){

        if(from_page.equals("profile")){
            Intent intent = new Intent(PostDetailsActivity.this, MainActivity.class);
            intent.putExtra("key", R.id.menu_profile);
            startActivity(intent);
            finish();

        }else{

            Intent intent = new Intent(PostDetailsActivity.this, MainActivity.class);
            intent.putExtra("key", R.id.menu_blood_request);
            startActivity(intent);
            finish();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);

        return pd_menu;
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.pst_edit :
                Intent intent = new Intent(PostDetailsActivity.this, PostEditActivity.class);
                intent.putExtra("postid", pd_postid);
                intent.putExtra("userid", pd_userid);
                intent.putExtra("full_nam", pd_full_nam);
                intent.putExtra("bl_grp", pd_bl_grp);
                intent.putExtra("bl_am", pd_bl_am);
                intent.putExtra("ab_dis", pd_ab_dis);
                intent.putExtra("hos_nam", pd_hos_nam);
                intent.putExtra("dist", pd_dist);
                intent.putExtra("dl", pd_dl);
                intent.putExtra("mob_no", pd_mob_no);
                intent.putExtra("post_time", pd_post_time);
                intent.putExtra("mana", pd_man);
                startActivity(intent);
                finish();
                return true;


            case R.id.pst_delete :
                final AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(PostDetailsActivity.this);
                alertdialog1.setMessage("Do you want to delete the post?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                pd_firebaseDatabase.getReference("Posts").child(pd_postid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(PostDetailsActivity.this, "Post Deleted.", Toast.LENGTH_LONG).show();
                                        if(from_page.equals("profile")){
                                            Intent intent = new Intent(PostDetailsActivity.this, MainActivity.class);
                                            intent.putExtra("key", R.id.menu_profile);
                                            startActivity(intent);
                                            finish();

                                        }else{

                                            Intent intent = new Intent(PostDetailsActivity.this, MainActivity.class);
                                            intent.putExtra("key", R.id.menu_blood_request);
                                            startActivity(intent);
                                            finish();

                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PostDetailsActivity.this, "Error! " + e, Toast.LENGTH_LONG).show();
                                    }
                                });



                            }})
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }})
                        .setTitle("Alert");
                alertdialog1.show();
                return true;

        }

        return false;
    }



    public void status(final boolean status){
        if(pd_firebaseUser != null){
            pd_firebaseDatabase.getReference("Users").child(pd_firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    pd_firebaseDatabase.getReference("Users").child(pd_firebaseUser.getUid()).setValue(user);
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
