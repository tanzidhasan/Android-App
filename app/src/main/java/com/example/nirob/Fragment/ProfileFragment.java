package com.example.nirob.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirob.Adapter.PostAdapter;
import com.example.nirob.MainActivity;
import com.example.nirob.Object.Message;
import com.example.nirob.Object.Post;
import com.example.nirob.Object.User;
import com.example.nirob.PostDetailsActivity;
import com.example.nirob.ProfileEditActivity;
import com.example.nirob.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {

    private Activity activity;

    private TextView t_pp_indicator, t_p_full_nam, t_p_gen, t_p_roll_no, t_p_email, t_p_bl_grp, t_p_last_bl_do_dt, t_p_mob_no, t_p_dist, t_p_don_bl, t_p_wan_don;
    private RecyclerView rv_pp;
    private PostAdapter pp_adapter;
    private FirebaseAuth p_firebaseAuth;
    private FirebaseUser p_firebaseUser;
    private FirebaseDatabase p_firebaseDatabase;
    private String userid;
    private List<Post> post_list;
    private LinearLayout lay_p_own_info, lay_p_post, lay_p_don_bl, lay_p_lat_bl_do_dt;
    private ProgressBar p_pb;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_profile, container, false);

        p_firebaseAuth = FirebaseAuth.getInstance();
        p_firebaseUser = p_firebaseAuth.getCurrentUser();

        p_firebaseDatabase = FirebaseDatabase.getInstance();

        activity = getActivity();

        assert activity != null;
        activity.setTitle("Profile");
        setHasOptionsMenu(true);

        t_pp_indicator = view.findViewById(R.id.pp_t_indicator);

        rv_pp = view.findViewById(R.id.pp_recycleview);
        rv_pp.setNestedScrollingEnabled(true);

        t_p_full_nam = view.findViewById(R.id.p_d_full_nam);
        t_p_gen = view.findViewById(R.id.p_d_gen);
        t_p_roll_no = view.findViewById(R.id.p_d_roll_no);
        t_p_email = view.findViewById(R.id.p_d_email);
        t_p_bl_grp = view.findViewById(R.id.p_d_bl_grp);
        t_p_last_bl_do_dt = view.findViewById(R.id.p_d_last_bl_do_dt);
        t_p_mob_no = view.findViewById(R.id.p_d_mob_no);
        t_p_dist = view.findViewById(R.id.p_d_dist);
        t_p_don_bl = view.findViewById(R.id.p_don_bl);
        t_p_wan_don = view.findViewById(R.id.p_wan_don);

        lay_p_don_bl = view.findViewById(R.id.p_l_don_bl);
        lay_p_lat_bl_do_dt = view.findViewById(R.id.p_l_last_bl_do_dt);

        lay_p_own_info = view.findViewById(R.id.p_lay_own_info);
        lay_p_post = view.findViewById(R.id.p_post_layout);

        p_pb = view.findViewById(R.id.pb_p);

        post_list = new ArrayList<>();


        userid = p_firebaseUser.getUid();

        p_firebaseDatabase.getReference("Users").child(userid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        t_p_full_nam.setText(user.u02_full_name);

                        t_p_gen.setText(user.u03_gender);

                        t_p_roll_no.setText(user.u04_roll_no);

                        t_p_email.setText(user.u05_email);

                        t_p_bl_grp.setText(user.u06_blood_group);

                        if(user.u11_donated_blood){
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                            Date date = null;
                            try {
                                date = format.parse(user.u07_last_blood_donation_date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy");

                            t_p_last_bl_do_dt.setText(format1.format(date));
                            lay_p_lat_bl_do_dt.setVisibility(View.VISIBLE);
                        }else{
                            t_p_don_bl.setText("No");
                            lay_p_don_bl.setVisibility(View.VISIBLE);
                        }
                        if(user.u12_wanna_donate){
                            t_p_wan_don.setText("Yes");
                        }else {
                            t_p_wan_don.setText("No");
                        }

                        t_p_mob_no.setText(user.u09_mobile_no);

                        t_p_dist.setText(user.u10_district);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        p_firebaseDatabase.getReference("Posts").orderByChild("m09_message_time_milisec_dec")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        post_list.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Post post = snapshot.getValue(Post.class);
                            if(post.p02_user_id.equals(userid)){
                                post_list.add(post);
                            }
                        }

                        if(post_list.size() == 0) {
                            t_pp_indicator.setVisibility(View.VISIBLE);
                            rv_pp.setVisibility(View.GONE);
                        }else {
                            profileposts(post_list);
                            t_pp_indicator.setVisibility(View.GONE);
                            rv_pp.setVisibility(View.VISIBLE);
                        }
                        p_pb.setVisibility(View.INVISIBLE);
                        lay_p_own_info.setVisibility(View.VISIBLE);
                        lay_p_post.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragment = getFragmentManager();
                assert fragment != null;
                fragment.beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);


        return view;
    }











    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }






    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.pro_edit:
                startActivity(new Intent(activity, ProfileEditActivity.class));
                activity.finish();
                return true;

            case R.id.pro_logout :
                final AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(getContext());
                alertdialog1.setMessage("Do you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                p_firebaseAuth.signOut();
                                FragmentManager fragment = getFragmentManager();
                                assert fragment != null;
                                fragment.beginTransaction().replace(R.id.fragment_container,
                                        new HomeFragment()).commit();

                            }})
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }})
                        .setTitle("Alert");
                alertdialog1.show();
                return true;


            case R.id.pro_delete:
                final AlertDialog.Builder alertdialog2 = new AlertDialog.Builder(getContext());
                alertdialog2.setMessage("You won't be able to log in again. Do you want to delete your account?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                p_firebaseDatabase.getReference("Conversations")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                    Message message = snapshot.getValue(Message.class);
                                                    if(message.m03_sender_id.equals(userid)
                                                            || message.m04_receiver_id.equals(userid)){
                                                        p_firebaseDatabase.getReference("Conversations").child(message.m01_message_id).removeValue();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                p_firebaseDatabase.getReference("Posts")
                                        .orderByChild("p02_user_id").equalTo(userid)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                    Post post = snapshot.getValue(Post.class);
                                                    p_firebaseDatabase.getReference("Posts").child(post.p01_post_id).removeValue();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                p_firebaseDatabase.getReference("Users").child(userid).removeValue();

                                p_firebaseUser.delete();

                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.putExtra("key", R.id.menu_home);
                                startActivity(intent);
                                activity.finish();


                            }})
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }})
                        .setTitle("Alert");
                alertdialog2.show();


                return true;



        }

        return false;
    }














    public void profileposts(List<Post> postList){


        pp_adapter = new PostAdapter(getContext(), postList);

        rv_pp.setHasFixedSize(true);
        rv_pp.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_pp.setAdapter(pp_adapter);

        pp_adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post, int position) {
                Intent intent = new Intent(activity, PostDetailsActivity.class);
                intent.putExtra("from_page", "profile");
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
                activity.finish();
            }
        });

    }


}
