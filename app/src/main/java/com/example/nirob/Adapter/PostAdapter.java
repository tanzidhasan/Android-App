package com.example.nirob.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirob.Object.Post;
import com.example.nirob.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder>{

    private PostAdapter.OnItemClickListener listener;

    private Context post_context;
    private List<Post> post_post;


    public PostAdapter(Context post_context, List<Post> post_post){
        this.post_context = post_context;
        this.post_post = post_post;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);

        return new PostHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        Post model = post_post.get(position);

        holder.p_full_nam.setText(model.p03_full_name);

        String post_time = model.p12_post_time;
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
        }else{
            post_time = simpleDateFormat2.format(date) + " at " + simpleDateFormat1.format(date);
        }

        holder.p_post_time.setText(post_time);

        holder.p_bl_grp.setText(model.p04_blood_group);

        holder.p_ab_dis.setText(model.p06_about_disease);

        holder.p_dist.setText(model.p08_district);

        String deadline = model.p09_deadline;

        Date date1 = null;

        try {
            date1 = simpleDateFormat.parse(deadline);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.p_dl.setText(simpleDateFormat2.format(date1) + " at " + simpleDateFormat1.format(date1));


    }

    @Override
    public int getItemCount() {
        return post_post.size();
    }


    class PostHolder extends RecyclerView.ViewHolder {
        TextView p_full_nam, p_post_time, p_bl_grp, p_ab_dis, p_dist, p_dl;

        public PostHolder(View view){
            super(view);
            p_full_nam = view.findViewById(R.id.t_br_full_nam);
            p_post_time = view.findViewById(R.id.t_br_post_time);
            p_bl_grp = view.findViewById(R.id.t_br_bl_grp);
            p_ab_dis = view.findViewById(R.id.t_br_ab_dis);
            p_dist = view.findViewById(R.id.t_br_dist);
            p_dl = view.findViewById(R.id.t_br_dl);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(post_post.get(position),position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void  onItemClick(Post post, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


}
