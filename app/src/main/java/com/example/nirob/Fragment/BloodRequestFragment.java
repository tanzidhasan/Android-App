package com.example.nirob.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.nirob.R;

import com.example.nirob.Adapter.PostAdapter;
import com.example.nirob.BloodPostActivity;
import com.example.nirob.Object.Post;
import com.example.nirob.PostDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BloodRequestFragment extends Fragment {

    private Activity activity;

    private Button bt_post_req, bt_br_filter, br_fil_bt_needed_bl;

    private Spinner br_fil_sp_bl_grp, br_fil_sp_dist;

    private TextView t_rec_post, t_br_indicator;

    private RecyclerView rv_br;

    private PostAdapter br_adapter;

    private List <Post> br_post_list;

    private LayoutInflater lf;

    private FirebaseDatabase br_firebaseDatabase;

    private ProgressBar pb_br;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_blood_request, container, false);

        br_firebaseDatabase = FirebaseDatabase.getInstance();

        activity = getActivity();

        activity.setTitle("Blood Request");

        pb_br = view.findViewById(R.id.pb_br);
        pb_br.setVisibility(View.VISIBLE);

        t_br_indicator = view.findViewById(R.id.br_t_indicator);

        t_rec_post = view.findViewById(R.id.t_br_rec_post);
        rv_br = view.findViewById(R.id.br_recyclerview);

        bt_post_req = view.findViewById(R.id.bt_br_post);
        bt_post_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, BloodPostActivity.class));
                activity.finish();
            }
        });

        bt_br_filter = view.findViewById(R.id.br_bt_filter);
        bt_br_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                br_filterfuntion();
            }
        });

        br_post_list = new ArrayList<>();


        br_firebaseDatabase.getReference("Posts").orderByChild("p14_post_mili_time_dec")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        br_post_list.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Post post = snapshot.getValue(Post.class);
                            if(!post.p15_managed){
                                br_post_list.add(post);
                            }
                        }
                        refreshData(br_post_list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                t_rec_post.setText("Recent Post");

                br_firebaseDatabase.getReference("Posts").orderByChild("p14_post_mili_time_dec")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                br_post_list.clear();
                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    Post post = snapshot.getValue(Post.class);
                                    if(!post.p15_managed){
                                        br_post_list.add(post);
                                    }
                                }

                                refreshData(br_post_list);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                pullToRefresh.setRefreshing(false);
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


    public void refreshData(List<Post> post_list){


        if(post_list.size() == 0) {
            pb_br.setVisibility(View.INVISIBLE);
            bt_post_req.setVisibility(View.VISIBLE);
            bt_br_filter.setVisibility(View.VISIBLE);
            t_rec_post.setVisibility(View.VISIBLE);
            t_br_indicator.setVisibility(View.VISIBLE);
        }else {
            br_adapter = new PostAdapter(getContext(), post_list);
            rv_br.setHasFixedSize(true);
            rv_br.setLayoutManager(new LinearLayoutManager(getContext()));
            rv_br.setAdapter(br_adapter);


            br_adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Post post, int position) {
                    Intent intent = new Intent(activity, PostDetailsActivity.class);
                    intent.putExtra("from_page", "blood_request");
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

            if(post_list.size() != 1){
                String get_text = (String) t_rec_post.getText();
                t_rec_post.setText(get_text + "s");
            }
            pb_br.setVisibility(View.GONE);
            t_br_indicator.setVisibility(View.GONE);
            bt_post_req.setVisibility(View.VISIBLE);
            bt_br_filter.setVisibility(View.VISIBLE);
            t_rec_post.setVisibility(View.VISIBLE);
            rv_br.setVisibility(View.VISIBLE);
        }


    }



    public void br_filterfuntion(){
        View view1 = lf.inflate(R.layout.filter_layout,null);
        br_fil_bt_needed_bl = view1.findViewById(R.id.bt_fil_needed_bl);
        br_fil_bt_needed_bl.setVisibility(View.GONE);

        br_fil_sp_bl_grp = view1.findViewById(R.id.sp_fil_bl_grp);
        br_fil_sp_dist = view1.findViewById(R.id.sp_fil_dist);


        AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(activity);
        alertdialog1.setView(view1)
                .setCancelable(false)
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if(br_fil_sp_bl_grp.getSelectedItemPosition() == 0 && br_fil_sp_dist.getSelectedItemPosition() == 0){

                            Toast.makeText(activity, "No Filter is Selected", Toast.LENGTH_LONG).show();

                        }else{

                            pb_br.setVisibility(View.VISIBLE);
                            bt_post_req.setVisibility(View.INVISIBLE);
                            bt_br_filter.setVisibility(View.INVISIBLE);
                            t_rec_post.setVisibility(View.INVISIBLE);
                            t_br_indicator.setVisibility(View.INVISIBLE);
                            rv_br.setVisibility(View.INVISIBLE);


                            t_rec_post.setText("Filtered Post");

                            if(br_fil_sp_bl_grp.getSelectedItemPosition() != 0 && br_fil_sp_dist.getSelectedItemPosition() != 0){

                                br_firebaseDatabase.getReference("Posts")
                                        .orderByChild("p13_post_mili_time").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        br_post_list.clear();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            Post post = snapshot.getValue(Post.class);
                                            if(!post.p15_managed
                                                    && post.p04_blood_group.equals(br_fil_sp_bl_grp.getSelectedItem())
                                                    && post.p08_district.equals(br_fil_sp_dist.getSelectedItem())){
                                                br_post_list.add(post);
                                            }
                                        }
                                        refreshData(br_post_list);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }else {
                                if(br_fil_sp_bl_grp.getSelectedItemPosition() != 0){

                                    br_firebaseDatabase.getReference("Posts")
                                            .orderByChild("p13_post_mili_time").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            br_post_list.clear();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                Post post = snapshot.getValue(Post.class);
                                                if(!post.p15_managed
                                                        && post.p04_blood_group.equals(br_fil_sp_bl_grp.getSelectedItem())){
                                                    br_post_list.add(post);
                                                }
                                            }
                                            refreshData(br_post_list);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }else {

                                    br_firebaseDatabase.getReference("Posts")
                                            .orderByChild("p13_post_mili_time").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            br_post_list.clear();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                Post post = snapshot.getValue(Post.class);
                                                if(!post.p15_managed
                                                        && post.p08_district.equals(br_fil_sp_dist.getSelectedItem())){
                                                    br_post_list.add(post);
                                                }
                                            }
                                            refreshData(br_post_list);
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
                .setTitle("Filter Posts");
        alertdialog1.show();
    }

}
