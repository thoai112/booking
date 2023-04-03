package com.kurata.hotelmanagement.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.data.model.User;
import com.kurata.hotelmanagement.databinding.UsersViewItemBinding;

import java.util.ArrayList;
import java.util.List;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.UserViewHolder>  {
    //implements Filterable
    private List<User> list;
    private UserListener userListener;


    public UsersRecyclerAdapter (List<User> list, UserListener userListener) {
        this.list = list;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(
                UsersViewItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User model = list.get(position);
        //animation
        //data
        holder.setData(list.get(position),userListener);
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

        UsersViewItemBinding binding;

        public UserViewHolder(UsersViewItemBinding usersViewItemBinding){
            super(usersViewItemBinding.getRoot());
            binding = usersViewItemBinding;
        }

        void setData(User user, UserListener userListener){
            binding.fullName.setText(user.getfullName());
            binding.email.setText(user.getEmail());
            Glide.with(binding.avatar).load(user.getAvatar()).into(binding.avatar);

            if(user.isStatus()){
                Glide.with(binding.status).load(R.drawable.activate).into(binding.status);
            }
            else{
                Glide.with(binding.status).load(R.drawable.disable).into(binding.status);
            }
            binding.getRoot().setOnClickListener(v -> {
                User model = new User();
                model.setfullName(user.getfullName());
                model.setEmail(user.getEmail());
                model.setAddress(user.getAddress());
                model.setMobile(user.getMobile());
                model.setRole(user.getRole());
                model.setAvatar(user.getAvatar());
                model.setId(user.getId());
                model.setStatus(user.isStatus());
                userListener.onUserClicked(model);
            });
        }

    }

    public interface UserListener {
        void onUserClicked(User user);
    }
}
