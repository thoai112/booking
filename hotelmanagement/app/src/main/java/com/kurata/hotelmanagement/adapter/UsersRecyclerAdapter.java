package com.kurata.hotelmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.data.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Context;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.UserViewHolder>  {
    //implements Filterable
    private List<User> list;
    private UserListener userListener;
    Context mcontext;


    public UsersRecyclerAdapter (List<User> list, UserListener userListener) {
        this.list = list;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_view_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User model = list.get(position);
        //animation

        //data
        holder.fullName.setText(model.getfullName());
        holder.email.setText(model.getEmail());
        Glide.with(holder.avatar).load(model.getAvatar()).into(holder.avatar);
        holder.itemView.setOnClickListener(v -> {
            userListener.onUserClicked(model);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilteredList(ArrayList<User> ListToShow){
        this.list= ListToShow;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView fullName, email;
        CircleImageView avatar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName= itemView.findViewById(R.id.fullName);
            email = itemView.findViewById(R.id.email);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }

    public interface UserListener {
        void onUserClicked(User user);
    }
}
