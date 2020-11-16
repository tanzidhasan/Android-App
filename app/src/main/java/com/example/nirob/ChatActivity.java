package com.example.nirob;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirob.Adapter.MessageAdapter;
import com.example.nirob.Fragment.APIService;
import com.example.nirob.Notification.Client;
import com.example.nirob.Notification.Data;
import com.example.nirob.Notification.MyResponse;
import com.example.nirob.Notification.Sender;
import com.example.nirob.Notification.Token;
import com.example.nirob.Object.Message;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {


    private ActionBar actionBar;
    private FirebaseAuth c_firebaseAuth;
    private FirebaseUser c_firebaseUser;
    private String c_userid;
    private FirebaseDatabase c_firebaseDatabase;
    private MessageAdapter c_adapter;
    private List<Message> message_list;
    private EditText c_et;
    private ImageButton c_ib;
    private RecyclerView rv_c;
    private LinearLayoutManager linearLayoutManager;
    private APIService c_apiService;
    private boolean notify = false;
    private int c = 0;
    private ValueEventListener chat_valueEventListener, check_valueEventListener, seen_valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("");

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            c_userid = extras.getString("userid");
        }

        message_list = new ArrayList<>();


        c_apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        c_firebaseAuth = FirebaseAuth.getInstance();
        c_firebaseUser = c_firebaseAuth.getCurrentUser();
        c_firebaseDatabase = FirebaseDatabase.getInstance();

        c_et = findViewById(R.id.et_c);
        c_ib = findViewById(R.id.ib_c);
        rv_c = findViewById(R.id.c_recyclerview);

        c_firebaseDatabase.getReference("Users").child(c_userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        actionBar.setTitle(user.u02_full_name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        c_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String message = c_et.getText().toString().trim();

                if(message.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Empty Message can't be sent", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    sendmessage(message);
                }
                c_et.setText("");

            }
        });


        checkstatus();

        chat_valueEventListener = c_firebaseDatabase.getReference("Conversations").orderByChild("m08_message_time_milisec")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        message_list.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Message message = snapshot.getValue(Message.class);
                            if(message.m02_sender_receiver_id.equals(c_userid + c_firebaseUser.getUid())
                                    || message.m02_sender_receiver_id.equals(c_firebaseUser.getUid() + c_userid)){
                                message_list.add(message);
                            }
                        }
                        chats(message_list);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        seenmessage();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                intent.putExtra("key", R.id.menu_conversations);
                startActivity(intent);
                finish();
            }
        };
        ChatActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);


    }






    @Override
    public boolean onSupportNavigateUp() {

        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        intent.putExtra("key", R.id.menu_conversations);
        startActivity(intent);
        finish();

        return true;
    }






    public void chats(List<Message> messageList){


        c_adapter = new MessageAdapter(this, messageList);
        rv_c.setHasFixedSize(true);
        rv_c.setAdapter(c_adapter);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rv_c.setLayoutManager(linearLayoutManager);

        c_adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                linearLayoutManager.smoothScrollToPosition(rv_c, null, c_adapter.getItemCount());
            }
        });


        c_adapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TextView time, TextView isseen) {
                if(time.isShown()){
                    time.setVisibility(View.GONE);
                    isseen.setVisibility(View.GONE);
                }else {
                    time.setVisibility(View.VISIBLE);
                    isseen.setVisibility(View.VISIBLE);
                }
            }
        });


    }







    public void checkstatus(){

        check_valueEventListener = c_firebaseDatabase.getReference("Users").child(c_userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if(user.u13_status){
                    actionBar.setSubtitle("Online");
                }else{

                    String last_seen = user.u14_last_seen;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm aa");
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMM dd, yyyy");

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("hh:mm aa dd-MM-yyyy");

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat4 = new SimpleDateFormat("MMM dd");

                    Date date = null;

                    try {
                        date = simpleDateFormat3.parse(last_seen);
                    } catch (ParseException pe) {
                        pe.printStackTrace();
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
                                last_seen = diffMinutes + " min ago";
                            }else {
                                last_seen = diffMinutes + " mins ago";
                            }
                        }else if(diffHours == 1){
                            last_seen = diffHours + " hr ago";
                        }else{
                            last_seen = diffHours + " hrs ago";
                        }
                    }else if(diffyear == 0){
                        last_seen = simpleDateFormat4.format(date) + " at " + simpleDateFormat1.format(date);
                    }else{
                        last_seen = simpleDateFormat2.format(date) + " at " + simpleDateFormat1.format(date);
                    }

                    actionBar.setSubtitle(last_seen);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }












    private void sendmessage(String msg){

        final String chatid = c_firebaseDatabase.getReference("Conversations").push().getKey();


        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa dd-MM-yyyy");
        Date date = Calendar.getInstance().getTime();
        long ct = date.getTime();
        long ct_dec = -(date.getTime());
        final String cTime = simpleDateFormat.format(date);

        Message message = new Message(chatid, c_firebaseUser.getUid() + c_userid, c_firebaseUser.getUid(), c_userid, msg, false, cTime, ct, ct_dec);

        final String mess = msg;

        c_firebaseDatabase.getReference("Conversations").child(chatid).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        c_firebaseDatabase.getReference("Users").child(c_firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(notify){
                    sendNotification( c_userid, user.u02_full_name, mess);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }






    public void sendNotification(String receiver, final String user_name, final String message){

        c_firebaseDatabase.getReference("Tokens").child(receiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Token token = dataSnapshot.getValue(Token.class);
                Data data = new Data(c_firebaseUser.getUid(), R.mipmap.ic_app_logo_round, message, user_name, c_userid);

                Sender sender = new Sender(data, token.token_data);

                c_apiService.sendNotification(sender)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.code() == 200){
                                    if(response.body().success == 1){
                                        c++;
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }










    public void seenmessage(){

        seen_valueEventListener = c_firebaseDatabase.getReference("Conversations").orderByChild("m02_sender_receiver_id").equalTo(c_userid + c_firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Message message = snapshot.getValue(Message.class);
                            message.m06_is_seen = true;
                            c_firebaseDatabase.getReference("Conversations").child(message.m01_message_id).setValue(message);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }



    public void currentUser(String userid){
        SharedPreferences.Editor sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        sharedPreferences.putString("currentuser", userid);
        sharedPreferences.apply();
    }



    public void status(final boolean status){
        if(c_firebaseUser != null){
            c_firebaseDatabase.getReference("Users").child(c_firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    c_firebaseDatabase.getReference("Users").child(c_firebaseUser.getUid()).setValue(user);
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
        currentUser(c_userid);
        status(true);
    }


    @Override
    protected void onPause() {
        super.onPause();
        currentUser("none");
        status(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(chat_valueEventListener != null){
            c_firebaseDatabase.getReference("Conversations").removeEventListener(chat_valueEventListener);
        }

        if(seen_valueEventListener != null){
            c_firebaseDatabase.getReference("Conversations").removeEventListener(seen_valueEventListener);
        }

        if(check_valueEventListener != null){
            c_firebaseDatabase.getReference("Users").removeEventListener(check_valueEventListener);
        }

    }
}

