package com.kurata.booking.AdapterRecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.databinding.PopularHotelBinding;

import java.util.ArrayList;
import java.util.List;

public class PopularRecyclerViewAdapter  extends  RecyclerView.Adapter<PopularRecyclerViewAdapter.PopularViewHolder>{

    private List<Hotel> list;
    private PopularListener popularListener;


    public PopularRecyclerViewAdapter (List<Hotel> list, PopularListener popularListener) {
        this.list = list;
        this.popularListener = popularListener;
    }


    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PopularViewHolder(
                PopularHotelBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {
        holder.setData(list.get(position), popularListener);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilteredList(ArrayList<Hotel> ListToShow){
        this.list= ListToShow;
        notifyDataSetChanged();
    }

    public static class PopularViewHolder extends RecyclerView.ViewHolder {
        PopularHotelBinding binding;

        public PopularViewHolder(PopularHotelBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

        void setData(Hotel hotel, PopularListener popularListener){
            ArrayList<String> arrayList = hotel.getImage();
            Glide.with(binding.image).load(arrayList.get(0).toString()).into(binding.image);
            binding.name.setText(hotel.getName());
            binding.getRoot().setOnClickListener(v -> {
                Hotel model = new Hotel();
                model.setName(hotel.getName());
                model.setId(hotel.getId());
                model.setAddress(hotel.getAddress());
                model.setImage(hotel.getImage());
                model.setAbout(hotel.getAbout());
                model.setStatus(hotel.getStatus());
                popularListener.onUserClicked(model);
            });
        }
    }

    public interface PopularListener {
        void onUserClicked(Hotel hotel);
    }


}
