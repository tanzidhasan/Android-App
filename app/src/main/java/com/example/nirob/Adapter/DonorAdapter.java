package com.example.nirob.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nirob.Object.User;
import com.example.nirob.R;

import java.util.List;


public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.DonorHolder> {


    private DonorAdapter.OnItemClickListener listener;

    private Context don_context;
    private List<User> don_user;


    public DonorAdapter(Context don_context, List<User> don_user){
        this.don_context = don_context;
        this.don_user = don_user;
    }


    @NonNull
    @Override
    public DonorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.donor_layout, parent, false);

        return new DonorHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorHolder holder, int position) {

        User model = don_user.get(position);

        holder.d_full_nam.setText(model.u02_full_name);

        holder.d_bl_grp.setText(model.u06_blood_group);

        holder.d_dist.setText(model.u10_district);
    }

    @Override
    public int getItemCount() {
        return don_user.size();
    }


    class DonorHolder extends RecyclerView.ViewHolder {
        TextView d_full_nam, d_bl_grp, d_dist;

        public DonorHolder(View view){
            super(view);

            d_full_nam = view.findViewById(R.id.dl_t_full_nam);
            d_bl_grp = view.findViewById(R.id.dl_t_bl_grp);
            d_dist = view.findViewById(R.id.dl_t_dist);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(don_user.get(position),position);
                    }
                }
            });

        }
    }



    public interface OnItemClickListener {
        void  onItemClick(User user, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


}





