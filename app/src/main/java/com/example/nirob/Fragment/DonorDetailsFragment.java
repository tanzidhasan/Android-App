package com.example.nirob.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nirob.Adapter.DonorAdapter;
import com.example.nirob.Object.User;
import com.example.nirob.R;
import com.example.nirob.UserDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DonorDetailsFragment extends Fragment {

    private Activity activity;

    private TextView t_dd_indicator, dd_t_avail_don;

    private Button dd_bt_filter, dd_fil_bt_needed_bl;

    private RecyclerView rv_dd;

    private DonorAdapter dd_donorAdapter;

    private Spinner dd_fil_sp_bl_grp, dd_fil_sp_dist;

    private String dd_str_needed_bl = "";

    private LayoutInflater lf;

    private long dd_lg_needed_bl = 0;

    private FirebaseDatabase dd_firebaseDatabase;

    private List<User> dd_user_list;

    private ProgressBar dd_pb;

    private FirebaseUser dd_firebaseUser;

    private ConstraintLayout dd_lay_text;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        lf = getActivity().getLayoutInflater();
        final View view =  lf.inflate(R.layout.fragment_donor_details, container, false);


        dd_firebaseDatabase = FirebaseDatabase.getInstance();

        dd_firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        activity = getActivity();
        activity.setTitle("Donor Details");

        dd_bt_filter = view.findViewById(R.id.bt_dd_filter);
        dd_bt_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dd_filterfuntion();
            }
        });

        dd_t_avail_don = view.findViewById(R.id.t_dd_avail_don);
        dd_lay_text = view.findViewById(R.id.lay_dd_text);
        t_dd_indicator = view.findViewById(R.id.dd_t_indicator);
        dd_pb = view.findViewById(R.id.pb_dd);

        rv_dd = view.findViewById(R.id.dd_recyclerview);

        dd_user_list = new ArrayList<>();

        final long lt_bl_do = System.currentTimeMillis() - Long.valueOf("10368000000");

        dd_firebaseDatabase.getReference("Users").orderByChild("u08_last_blood_donation_milisec")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        dd_user_list.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            User user = snapshot.getValue(User.class);
                            if(user.u12_wanna_donate && user.u08_last_blood_donation_milisec <= lt_bl_do){
                                dd_user_list.add(user);
                            }
                        }
                        finddonor(dd_user_list);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        final SwipeRefreshLayout dd_pullToRefresh = view.findViewById(R.id.dd_swiperefresh);
        dd_pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onRefresh() {

                final long srl_lt_bl_do = System.currentTimeMillis() - Long.valueOf("10368000000");
                dd_firebaseDatabase.getReference("Users").orderByChild("u08_last_blood_donation_milisec")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                dd_user_list.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    User user = snapshot.getValue(User.class);
                                    if(user.u12_wanna_donate && user.u08_last_blood_donation_milisec <= srl_lt_bl_do){
                                        dd_user_list.add(user);
                                    }
                                }
                                dd_t_avail_don.setText("Available Donor");
                                finddonor(dd_user_list);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                dd_pullToRefresh.setRefreshing(false);
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













    @SuppressLint("SetTextI18n")
    public void finddonor(List<User> donor_list){

        if(donor_list.size() == 0){
            dd_pb.setVisibility(View.INVISIBLE);
            dd_bt_filter.setVisibility(View.VISIBLE);
            dd_t_avail_don.setVisibility(View.VISIBLE);
            dd_lay_text.setVisibility(View.INVISIBLE);
            t_dd_indicator.setVisibility(View.VISIBLE);
            rv_dd.setVisibility(View.GONE);

        }else{

            dd_donorAdapter = new DonorAdapter(getContext(), donor_list);
            rv_dd.setHasFixedSize(true);
            rv_dd.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_dd.setAdapter(dd_donorAdapter);

            dd_donorAdapter.setOnItemClickListener(new DonorAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(User user, int position) {
                    String dd_userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if(dd_userid.equals(user.u01_user_id)){
                        FragmentManager fragment = getFragmentManager();
                        assert fragment != null;
                        fragment.beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();
                    }else {
                        Intent intent = new Intent(activity, UserDetailActivity.class);
                        intent.putExtra("from_page", "donor_detail");
                        intent.putExtra("userid", user.u01_user_id);
                        startActivity(intent);
                        activity.finish();
                    }

                }
            });

            if(donor_list.size() != 1){
                String get_text = (String) dd_t_avail_don.getText();
                dd_t_avail_don.setText(get_text + "s");
            }
            dd_pb.setVisibility(View.GONE);
            dd_bt_filter.setVisibility(View.VISIBLE);
            dd_t_avail_don.setVisibility(View.VISIBLE);
            dd_lay_text.setVisibility(View.VISIBLE);
            t_dd_indicator.setVisibility(View.GONE);
            rv_dd.setVisibility(View.VISIBLE);

        }

    }


















    public void dd_filterfuntion(){
        View view1 = lf.inflate(R.layout.filter_layout,null);
        dd_fil_bt_needed_bl = view1.findViewById(R.id.bt_fil_needed_bl);
        dd_fil_sp_bl_grp = view1.findViewById(R.id.sp_fil_bl_grp);
        dd_fil_sp_dist = view1.findViewById(R.id.sp_fil_dist);

        dd_fil_bt_needed_bl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                final int day = cldr.get(Calendar.DAY_OF_MONTH);
                final int month = cldr.get(Calendar.MONTH);
                final int year = cldr.get(Calendar.YEAR);

                DatePickerDialog picker = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int yearofdecade, int monthOfYear, int dayOfMonth) {

                                if(yearofdecade > year || (yearofdecade == year && monthOfYear > month) || (yearofdecade == year && monthOfYear == month && dayOfMonth >= day)){

                                    dd_str_needed_bl = dayOfMonth + "-" + (monthOfYear+1) + "-" + yearofdecade;

                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                                    Date date = null;
                                    try {
                                        date = format.parse(dd_str_needed_bl);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy");

                                    dd_lg_needed_bl = date.getTime();
                                    dd_fil_bt_needed_bl.setText(format1.format(date));
                                    dd_fil_bt_needed_bl.setTextColor(Color.BLACK);

                                }else {
                                    dd_str_needed_bl = "";
                                    dd_fil_bt_needed_bl.setText("Not Before Today");
                                    dd_fil_bt_needed_bl.setTextColor(Color.RED);
                                }

                            }
                        }, year, month, day);
                picker.show();
            }
        });

        AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(activity);
        alertdialog1.setView(view1)
                .setCancelable(false)
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if(dd_fil_sp_bl_grp.getSelectedItemPosition() == 0 && dd_str_needed_bl.equals("") && dd_fil_sp_dist.getSelectedItemPosition() == 0){

                            Toast.makeText(activity, "No Filter is Selected", Toast.LENGTH_LONG).show();

                        }else{

                            dd_pb.setVisibility(View.VISIBLE);
                            dd_bt_filter.setVisibility(View.INVISIBLE);
                            dd_t_avail_don.setVisibility(View.INVISIBLE);
                            dd_lay_text.setVisibility(View.INVISIBLE);
                            t_dd_indicator.setVisibility(View.INVISIBLE);
                            rv_dd.setVisibility(View.INVISIBLE);

                            dd_t_avail_don.setText("Available Filtered Donor");

                            final long blood;

                            if(!dd_str_needed_bl.equals("")){
                                blood = dd_lg_needed_bl - Long.valueOf("10368000000");
                            }else {
                                blood = Calendar.getInstance().getTimeInMillis() - Long.valueOf("10368000000");
                            }

                            if(dd_fil_sp_bl_grp.getSelectedItemPosition() != 0 && dd_fil_sp_dist.getSelectedItemPosition() != 0){

                                dd_firebaseDatabase.getReference("Users").orderByChild("u08_last_blood_donation_milisec")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                dd_user_list.clear();
                                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                    User user = snapshot.getValue(User.class);
                                                    if(user.u12_wanna_donate
                                                            && user.u06_blood_group.equals(dd_fil_sp_bl_grp.getSelectedItem())
                                                            && user.u10_district.equals(dd_fil_sp_dist.getSelectedItem())
                                                            && user.u08_last_blood_donation_milisec <= blood){
                                                        dd_user_list.add(user);
                                                    }
                                                }
                                                finddonor(dd_user_list);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                            }else {
                                if(dd_fil_sp_bl_grp.getSelectedItemPosition() != 0){

                                    dd_firebaseDatabase.getReference("Users").orderByChild("u08_last_blood_donation_milisec")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dd_user_list.clear();
                                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                        User user = snapshot.getValue(User.class);
                                                        if(user.u12_wanna_donate
                                                                && user.u06_blood_group.equals(dd_fil_sp_bl_grp.getSelectedItem())
                                                                && user.u08_last_blood_donation_milisec <= blood){
                                                            dd_user_list.add(user);
                                                        }
                                                    }
                                                    finddonor(dd_user_list);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                }else if(dd_fil_sp_dist.getSelectedItemPosition() != 0){

                                    dd_firebaseDatabase.getReference("Users").orderByChild("u08_last_blood_donation_milisec")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dd_user_list.clear();
                                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                        User user = snapshot.getValue(User.class);
                                                        if(user.u12_wanna_donate
                                                                && user.u10_district.equals(dd_fil_sp_dist.getSelectedItem())
                                                                && user.u08_last_blood_donation_milisec <= blood){
                                                            dd_user_list.add(user);
                                                        }
                                                    }
                                                    finddonor(dd_user_list);

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                } else {

                                    dd_firebaseDatabase.getReference("Users").orderByChild("u08_last_blood_donation_milisec")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dd_user_list.clear();
                                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                        User user = snapshot.getValue(User.class);
                                                        if(user.u12_wanna_donate
                                                                && user.u08_last_blood_donation_milisec <= blood){
                                                            dd_user_list.add(user);
                                                        }
                                                    }
                                                    finddonor(dd_user_list);

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                }
                            }


                        }

                    }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }})
                .setTitle("Filter Donor");
        alertdialog1.show();
    }



}
