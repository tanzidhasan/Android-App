package com.example.nirob;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.nirob.Fragment.AboutFragment;
import com.example.nirob.Fragment.BloodRequestFragment;
import com.example.nirob.Fragment.ConversationsFragment;
import com.example.nirob.Fragment.DonorDetailsFragment;
import com.example.nirob.Fragment.HomeFragment;
import com.example.nirob.Fragment.InfoFragment;
import com.example.nirob.Fragment.ProfileFragment;
import com.example.nirob.Object.Message;
import com.example.nirob.Object.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;

    private TextView m_full_nam, m_email;

    private FirebaseDatabase m_firebaseDatabase;
    private FirebaseAuth m_firebaseAuth;
    private FirebaseUser m_firebaseUser;
    private List<String> messagelists;
    private Menu menu;
    private ValueEventListener ref_valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_firebaseAuth = FirebaseAuth.getInstance();
        m_firebaseUser = m_firebaseAuth.getCurrentUser();

        m_firebaseDatabase = FirebaseDatabase.getInstance();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();
        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        m_full_nam = view.findViewById(R.id.t_full_nam);
        m_email = view.findViewById(R.id.t_email);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navi_drawer_open, R.string.navi_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        refreshdata();

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("fijg", true);
            HomeFragment homeFragment = new HomeFragment();
            homeFragment.setArguments(bundle);
            navigationView.setCheckedItem(R.id.menu_home);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    homeFragment).commit();

        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int itemId = extras.getInt("key");
            navigationView.setCheckedItem(itemId);
            selectMenuItem(itemId);
        }



    }


    public void refreshdata(){

        if (m_firebaseUser == null) {
            menu.findItem(R.id.menu_profile).setVisible(false);
            menu.findItem(R.id.menu_blood_request).setVisible(false);
            menu.findItem(R.id.menu_donor_details).setVisible(false);
            menu.findItem(R.id.menu_conversations).setVisible(false);
        } else {

            String userid = m_firebaseUser.getUid();

            ref_valueEventListener = m_firebaseDatabase.getReference("Conversations")
                    .orderByChild("m04_receiver_id").equalTo(userid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    messagelists = new ArrayList<>();
                    messagelists.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Message message = snapshot.getValue(Message.class);

                        if(!message.m06_is_seen){
                            int flag = 0;
                            if(messagelists.size() != 0){
                                for(String id : messagelists){
                                    if(id.equals(message.m03_sender_id)){
                                        flag = 1;
                                        break;
                                    }
                                }
                                if(flag == 0){
                                    messagelists.add(message.m03_sender_id);
                                }

                            }else {
                                messagelists.add(message.m03_sender_id);
                            }
                        }
                    }

                    if(messagelists.size() != 0){
                        menu.findItem(R.id.menu_conversations).setTitle("Conversations" + "(" + messagelists.size() + ")");
                    }else {
                        menu.findItem(R.id.menu_conversations).setTitle("Conversations");
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            m_firebaseDatabase.getReference("Users").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    m_full_nam.setText(user.u02_full_name);
                    m_email.setText(user.u05_email);
                    m_full_nam.setVisibility(View.VISIBLE);
                    m_email.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectMenuItem(item.getItemId());

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


    public void selectMenuItem(int menuItemId){

        switch (menuItemId) {
            case R.id.menu_home:
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();
                }break;


            case R.id.menu_profile:
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                }break;

            case R.id.menu_blood_request:
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new BloodRequestFragment()).commit();
                }break;


            case R.id.menu_donor_details:
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new DonorDetailsFragment()).commit();
                }break;


            case R.id.menu_conversations:
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ConversationsFragment()).commit();
                }break;


            case R.id.menu_about:
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new AboutFragment()).commit();
                }break;


            case R.id.menu_info:
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new InfoFragment()).commit();
                }break;

        }
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }





    public void status(final boolean status){
        if(m_firebaseUser != null){
            m_firebaseDatabase.getReference("Users").child(m_firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    m_firebaseDatabase.getReference("Users").child(m_firebaseUser.getUid()).setValue(user);
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

    @Override
    protected void onStop() {
        super.onStop();
        if(ref_valueEventListener != null){
            m_firebaseDatabase.getReference("Conversation").removeEventListener(ref_valueEventListener);
        }

    }
}
