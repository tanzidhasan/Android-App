package com.example.nirob.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nirob.LoginActivity;
import com.example.nirob.Object.Post;
import com.example.nirob.Object.User;
import com.example.nirob.R;
import com.example.nirob.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {
    private Activity activity;

    private FirebaseAuth h_firebaseAuth;
    private FirebaseUser h_firebaseUser;

    private Button b_h_signup, b_h_login;
    private TextView t_hom_wel, t_don_bl, t_h_or;
    private ProgressBar pb_hom;

    private FirebaseDatabase h_firebaseDatabase;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_home, container, false);

        h_firebaseDatabase = FirebaseDatabase.getInstance();

        activity = getActivity();

        activity.setTitle("Home");


        b_h_signup = view.findViewById(R.id.home_b_signup);

        b_h_login = view.findViewById(R.id.home_b_login);

        final Bundle bundle = getArguments();


        t_h_or = view.findViewById(R.id.home_or);
        t_hom_wel = view.findViewById(R.id.home_wel);
        t_don_bl = view.findViewById(R.id.home_bldo);
        pb_hom = view.findViewById(R.id.hom_pb);


        h_firebaseAuth = FirebaseAuth.getInstance();
        h_firebaseUser = h_firebaseAuth.getCurrentUser();

        if(h_firebaseUser != null){
            pb_hom.setVisibility(View.VISIBLE);
            b_h_signup.setVisibility(View.GONE);
            b_h_login.setVisibility(View.GONE);
            t_h_or.setText("");

            final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            h_firebaseDatabase.getReference("Users").child(userid).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    t_h_or.setText("Hi, " + user.u02_full_name);

                    if(bundle != null){
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                pb_hom.setVisibility(View.INVISIBLE);
                                t_hom_wel.setVisibility(View.VISIBLE);
                                t_h_or.setVisibility(View.VISIBLE);
                                t_don_bl.setVisibility(View.VISIBLE);
                            }
                        },500);
                    }else {
                        pb_hom.setVisibility(View.INVISIBLE);
                        t_hom_wel.setVisibility(View.VISIBLE);
                        t_h_or.setVisibility(View.VISIBLE);
                        t_don_bl.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            h_firebaseDatabase.getReference("Posts")
                    .orderByChild("p10_deadline_mili").equalTo(System.currentTimeMillis()-86400000)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Post post = snapshot.getValue(Post.class);
                                if(!post.p15_managed){
                                    post.p15_managed = true;
                                    h_firebaseDatabase.getReference("Posts").child(post.p01_post_id).setValue(post);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        }else {
            t_hom_wel.setVisibility(View.VISIBLE);
            b_h_signup.setVisibility(View.VISIBLE);
            t_h_or.setVisibility(View.VISIBLE);
            b_h_login.setVisibility(View.VISIBLE);
            t_don_bl.setVisibility(View.VISIBLE);
        }

        b_h_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, SignUpActivity.class));
                activity.finish();
            }
        });

        b_h_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, LoginActivity.class));
                activity.finish();
            }
        });



        return view;
    }

}
