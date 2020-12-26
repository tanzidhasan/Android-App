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
import com.example.nirob.Object.Message;
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


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private MessageAdapter.OnItemClickListener listener;

    private Context msg_context;
    private List<Message> msg_message;

    public MessageAdapter(Context msg_context, List<Message> msg_message){
        this.msg_context = msg_context;
        this.msg_message = msg_message;
    }


    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 1){
            View v = LayoutInflater.from(msg_context).inflate(R.layout.chat_right_item, parent, false);
            return new MessageAdapter.MessageHolder(v);
        }else {
            View v = LayoutInflater.from(msg_context).inflate(R.layout.chat_left_item, parent, false);
            return new MessageAdapter.MessageHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {

        Message model = msg_message.get(position);

        if(model.m06_is_seen){
            holder.c_is.setText("Seen");
        }else {
            holder.c_is.setText("Delivered");
        }

        holder.c_msg.setText(model.m05_message);


        String msg_time = model.m07_message_time;
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
                msg_time = simpleDateFormat.format(date) + " at " + simpleDateFormat1.format(date);
            }

        }else if(diffyear == 0){
            msg_time = simpleDateFormat4.format(date) + " at " + simpleDateFormat1.format(date);
        }else{
            msg_time = simpleDateFormat2.format(date) + " at " + simpleDateFormat1.format(date);
        }

        holder.c_time.setText(msg_time);

    }



    class MessageHolder extends RecyclerView.ViewHolder{
        TextView c_time, c_msg, c_is;

        private MessageHolder(View view){
            super(view);

            c_time = view.findViewById(R.id.c_i_time);
            c_msg = view.findViewById(R.id.c_i_msg);
            c_is = view.findViewById(R.id.c_i_is);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(c_time, c_is);
                    }
                }
            });

        }

    }



    public interface OnItemClickListener {
        void  onItemClick(TextView time, TextView isseen);
    }

    public void setOnItemClickListener(MessageAdapter.OnItemClickListener listener){
        this.listener = listener;
    }


    @Override
    public int getItemViewType(int position) {

        FirebaseUser me_firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Message message = msg_message.get(position);

        if(message.m03_sender_id.equals(me_firebaseUser.getUid())){
            return 1;
        }else {
            return 0;
        }

    }

    @Override
    public int getItemCount() {
        return msg_message.size();
    }
}
