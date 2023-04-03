package com.kurata.hotelmanagement.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.data.model.Room;
import com.kurata.hotelmanagement.databinding.StaggereItemBinding;

import java.util.ArrayList;
import java.util.List;

public class RoomsRecyclerAdapter extends RecyclerView.Adapter<RoomsRecyclerAdapter.RoomViewHolder>{

    private List<Room> list;
    private RoomListener roomListener;

    public RoomsRecyclerAdapter (List<Room> list, RoomListener roomListener) {
        this.list = list;
        this.roomListener = roomListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomViewHolder(
                StaggereItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );

    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {

        holder.setData(list.get(position),roomListener);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilteredList(ArrayList<Room> ListToShow){
        this.list= ListToShow;
        notifyDataSetChanged();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {

        StaggereItemBinding binding;

        public RoomViewHolder(StaggereItemBinding staggereItemBinding){
            super(staggereItemBinding.getRoot());
            binding = staggereItemBinding;
        }

        void setData(Room room, RoomListener roomListener){
            binding.mTitle.setText(room.getName());
            ArrayList<String> arrayList = room.getImage();
            Glide.with(binding.staggeredImages).load(arrayList.get(0).toString()).into(binding.staggeredImages);
            if(room.getStatus().equals("Activate")){
                Glide.with(binding.status).load(R.drawable.activate).into(binding.status);
            }
            else{
                Glide.with(binding.status).load(R.drawable.disable).into(binding.status);
            }
            binding.getRoot().setOnClickListener(v -> {
                Room model = new Room();
                model.setName(room.getName());
                model.setId(room.getId());
                model.setAbout(room.getAbout());
                model.setImage(room.getImage());
                model.setStatus(room.getStatus());
                model.setHoteltype_id(room.getHoteltype_id());
                model.setHotel_id(room.getHotel_id());
                model.setRoomtype_id(room.getRoomtype_id());
                model.setPrice(room.getPrice());
                roomListener.onUserClicked(model);
            });
        }
    }

    public interface RoomListener {
        void onUserClicked(Room room);
    }
}
