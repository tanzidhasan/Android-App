package com.example.nirob.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nirob.Adapter.ConvAdapter;
import com.example.nirob.ChatActivity;
import com.example.nirob.Notification.Token;
import com.example.nirob.Object.Message;
import com.example.nirob.Object.User;
import com.example.nirob.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

public class ConversationsFragment extends Fragment {

    private LinearLayout conv_lay_indi, conv_lay_chat;

    private Activity activity;

    private TextView conv_t_indi;

    private ProgressBar conv_pb;

    private RecyclerView conv_recyclerView;

    private FirebaseDatabase conv_firebaseDatabase;

    private FirebaseAuth conv_firebaseAuth;

    private FirebaseUser conv_firebaseUser;

    private ConvAdapter convAdapter;

    private List<User> conv_users;

    private List<String> users_list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LayoutInflater lf = getActivity().getLayoutInflater();
        View view =  lf.inflate(R.layout.fragment_conversations, container, false);

        activity = getActivity();
        activity.setTitle("Conversations");

        conv_firebaseAuth = FirebaseAuth.getInstance();
        conv_firebaseUser = conv_firebaseAuth.getCurrentUser();
        conv_firebaseDatabase = FirebaseDatabase.getInstance();

        conv_lay_indi = view.findViewById(R.id.lay_conv_indi);
        conv_lay_chat = view.findViewById(R.id.lay_conv_chat);

        conv_t_indi = view.findViewById(R.id.t_conv_indi);
        conv_pb = view.findViewById(R.id.pb_conv);

        conv_recyclerView = view.findViewById(R.id.conv_recyclerview);
        conv_recyclerView.setHasFixedSize(true);
        conv_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        users_list = new ArrayList<>();
        conv_users = new ArrayList<>();


        loaddata();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                updateToken(task.getResult().getToken());

            }
        });

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.conv_swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                conv_lay_chat.setVisibility(View.GONE);
                conv_t_indi.setVisibility(View.GONE);
                conv_pb.setVisibility(View.VISIBLE);
                conv_lay_indi.setVisibility(View.VISIBLE);

                loaddata();
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


    public void updateToken(String ut_token){
        Token token = new Token(ut_token);
        conv_firebaseDatabase.getReference("Tokens").child(conv_firebaseUser.getUid()).setValue(token);
    }



    public void loaddata(){

        conv_firebaseDatabase.getReference("Conversations").orderByChild("m09_message_time_milisec_dec")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        users_list.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Message message = snapshot.getValue(Message.class);

                            if(message.m03_sender_id.equals(conv_firebaseUser.getUid())){
                                int flag = 0;
                                if(users_list.size() != 0){
                                    for(String uid : users_list){
                                        if(uid.equals(message.m04_receiver_id)){
                                            flag = 1;
                                            break;
                                        }
                                    }
                                    if(flag == 0){
                                        users_list.add(message.m04_receiver_id);
                                    }
                                }else {

                                    users_list.add(message.m04_receiver_id);
                                }

                            }


                            if(message.m04_receiver_id.equals(conv_firebaseUser.getUid())){
                                int flag = 0;
                                if(users_list.size() != 0){
                                    for(String uid : users_list){
                                        if(uid.equals(message.m03_sender_id)){
                                            flag = 1;
                                            break;
                                        }
                                    }
                                    if(flag == 0){
                                        users_list.add(message.m03_sender_id);
                                    }
                                }else {
                                    users_list.add(message.m03_sender_id);
                                }
                            }

                        }


                        if(users_list.size() == 0){
                            conv_pb.setVisibility(View.GONE);
                            conv_t_indi.setVisibility(View.VISIBLE);
                            conv_lay_indi.setVisibility(View.VISIBLE);
                        }else {

                            conv_firebaseDatabase.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    conv_users.clear();
                                    for(String uid : users_list){
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            User user = snapshot.getValue(User.class);
                                            if(user.u01_user_id.equals(uid)){
                                                conv_users.add(user);
                                                break;
                                            }
                                        }
                                    }


                                    getconversations(conv_users);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }





    public void getconversations(List<User> convusers){

        convAdapter = new ConvAdapter(getContext(), convusers);
        conv_recyclerView.setAdapter(convAdapter);

        convAdapter.setOnItemClickListener(new ConvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user, int position) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("userid", user.u01_user_id);
                startActivity(intent);
                activity.finish();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                conv_lay_indi.setVisibility(View.GONE);
                conv_lay_chat.setVisibility(View.VISIBLE);
            }
        },1000);


    }


}
