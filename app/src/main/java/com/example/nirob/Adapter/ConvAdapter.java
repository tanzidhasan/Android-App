package com.example.nirob.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirob.Object.Message;
import com.example.nirob.Object.User;
import com.example.nirob.R;
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
import java.util.List;

public class ConvAdapter extends RecyclerView.Adapter<ConvAdapter.ConvHolder> {

    private ConvAdapter.OnItemClickListener listener;

    private Context conv_context;
    private List<User> conv_user;

    public ConvAdapter(Context conv_context, List<User> conv_user){
        this.conv_context = conv_context;
        this.conv_user = conv_user;
    }

    @NonNull
    @Override
    public ConvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(conv_context).inflate(R.layout.conv_item, parent, false);

        return new ConvAdapter.ConvHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final ConvHolder holder, int position) {

        final FirebaseUser ca_firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseDatabase ca_firebaseDatabase = FirebaseDatabase.getInstance();

        final User user = conv_user.get(position);

        ca_firebaseDatabase.getReference("Conversations")
                .orderByChild("m02_sender_receiver_id").equalTo(user.u01_user_id + ca_firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int size = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Message message = snapshot.getValue(Message.class);
                            if(!message.m06_is_seen){
                                size++;
                            }
                        }
                        if(size == 0){
                            holder.full_nam.setText(user.u02_full_name);
                        }else{
                            holder.full_nam.setText(user.u02_full_name + "(" + size + ")");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        ca_firebaseDatabase.getReference("Conversations").orderByChild("m09_message_time_milisec_dec").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Message message = snapshot.getValue(Message.class);
                    if(message.m02_sender_receiver_id.equals(ca_firebaseUser.getUid() + user.u01_user_id) ||
                            message.m02_sender_receiver_id.equals(user.u01_user_id + ca_firebaseUser.getUid())){

                        if(message.m03_sender_id.equals(ca_firebaseUser.getUid())){
                            holder.last_message.setText("You : " + message.m05_message);
                            holder.last_message.setTextColor(Color.GRAY);
                            holder.last_message.setTypeface(Typeface.DEFAULT);
                        }else {
                            holder.last_message.setText(message.m05_message);
                            if(!message.m06_is_seen){
                                holder.last_message.setTextColor(Color.BLACK);
                                holder.last_message.setTypeface(null, Typeface.BOLD);
                            }else {
                                holder.last_message.setTextColor(Color.GRAY);
                                holder.last_message.setTypeface(Typeface.DEFAULT);
                            }
                        }


                        String msg_time = message.m07_message_time;
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("hh:mm aa");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMM dd, yyyy");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("hh:mm aa dd-MM-yyyy");
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat4 = new SimpleDateFormat("MMM dd");

                        Date date = null;

                        try {
                            date = simpleDateFormat3.parse(msg_time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date currenttime = Calendar.getInstance().getTime();

                        long diff = currenttime.getTime() - date.getTime();

                        long diffDays = diff / (24 * 60 * 60 * 1000);
                        long diffyear = diffDays / 365;

                        if(diffDays < 8){
                            String p_day = simpleDateFormat.format(date);
                            String c_day = simpleDateFormat.format(currenttime);
                            if(p_day.equals(c_day)){
                                msg_time = simpleDateFormat1.format(date);
                            }else {
                                msg_time = simpleDateFormat.format(date);
                            }

                        }else if(diffyear == 0){
                            msg_time = simpleDateFormat4.format(date);
                        }else{
                            msg_time = simpleDateFormat2.format(date);
                        }

                        holder.message_time.setText(msg_time);

                        break;

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return conv_user.size();
    }

    public class ConvHolder extends RecyclerView.ViewHolder{

        private TextView full_nam, last_message, message_time;

        public ConvHolder(View view){
            super(view);

            full_nam = view.findViewById(R.id.ci_t_recei);
            last_message = view.findViewById(R.id.ci_t_usdrm);
            last_message.setMaxWidth(650);
            message_time = view.findViewById(R.id.ci_t_time);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(conv_user.get(position), position);
                    }
                }
            });
        }

    }

    public interface OnItemClickListener {
        void  onItemClick(User user, int position);
    }

    public void setOnItemClickListener(ConvAdapter.OnItemClickListener listener){
        this.listener = listener;
    }



}
