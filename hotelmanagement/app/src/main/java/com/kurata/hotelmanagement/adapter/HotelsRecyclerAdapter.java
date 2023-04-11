package com.kurata.hotelmanagement.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kurata.hotelmanagement.data.model.Hotel;
import com.kurata.hotelmanagement.databinding.GirdItemBinding;

import java.util.ArrayList;
import java.util.List;

public class HotelsRecyclerAdapter extends RecyclerView.Adapter<HotelsRecyclerAdapter.HotelViewHolder> {
    private List<Hotel> list;
    private HotelListener hotelListener;


    public HotelsRecyclerAdapter (List<Hotel> list, HotelListener hotelListener) {
        this.list = list;
        this.hotelListener = hotelListener;
    }


    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HotelViewHolder(
                GirdItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        //Hotel model = list.get(position);
        holder.setData(list.get(position), hotelListener);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilteredList(ArrayList<Hotel> ListToShow){
        this.list= ListToShow;
        notifyDataSetChanged();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        GirdItemBinding binding;

        public HotelViewHolder(GirdItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

        void setData(Hotel hotel, HotelListener hotelListener){
            binding.gridCaption.setText(hotel.getName());
            ArrayList<String> arrayList = hotel.getImage();
            Glide.with(binding.gridImage).load(arrayList.get(0).toString()).into(binding.gridImage);
            binding.getRoot().setOnClickListener(v -> {
                Hotel model = new Hotel();
                model.setName(hotel.getName());
                model.setId(hotel.getId());
                model.setAddress(hotel.getAddress());
                model.setImage(hotel.getImage());
                model.setAbout(hotel.getAbout());
                model.setStatus(hotel.getStatus());
                model.setLocation(hotel.getLocation());
                hotelListener.onUserClicked(model);
            });
        }
    }

    public interface HotelListener {
        void onUserClicked(Hotel hotel);
    }
}
