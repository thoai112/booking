package com.kurata.hotelmanagement.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kurata.hotelmanagement.data.model.Roomtype;
import com.kurata.hotelmanagement.databinding.StaggereItemBinding;

import java.util.ArrayList;
import java.util.List;

public class RoomtypesRecyclerAdapter extends RecyclerView.Adapter<RoomtypesRecyclerAdapter.RoomtypeViewHolder>{

    private List<Roomtype> list;
    private RoomtypeListener roomtypeListener;


    public RoomtypesRecyclerAdapter (List<Roomtype> list, RoomtypeListener roomtypeListener) {
        this.list = list;
        this.roomtypeListener = roomtypeListener;
    }

    @NonNull
    @Override
    public RoomtypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomtypeViewHolder(
                StaggereItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staggere_item, parent, false);
//            return new HoteltypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomtypeViewHolder holder, int position) {

        holder.setData(list.get(position),roomtypeListener);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilteredList(ArrayList<Roomtype> ListToShow){
        this.list= ListToShow;
        notifyDataSetChanged();
    }

    public static class RoomtypeViewHolder extends RecyclerView.ViewHolder {

        StaggereItemBinding binding;

        public RoomtypeViewHolder(StaggereItemBinding staggereItemBinding){
            super(staggereItemBinding.getRoot());
            binding = staggereItemBinding;
        }

        void setData(Roomtype roomtype, RoomtypeListener roomtypeListener){
            binding.mTitle.setText(roomtype.getName());
            Glide.with(binding.staggeredImages).load(roomtype.getImg()).into(binding.staggeredImages);
            binding.getRoot().setOnClickListener(v -> {
                Roomtype model = new Roomtype();
                model.setName(roomtype.getName());
                model.setId(roomtype.getId());
                model.setImg(roomtype.getImg());
                model.setStatus(roomtype.isStatus());
                roomtypeListener.onUserClicked(model);
            });
        }
    }

    public interface RoomtypeListener {
        void onUserClicked(Roomtype roomtype);

    }

}
