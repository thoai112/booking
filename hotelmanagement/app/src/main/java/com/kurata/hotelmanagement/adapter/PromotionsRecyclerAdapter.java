package com.kurata.hotelmanagement.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kurata.hotelmanagement.data.model.Promotion;
import com.kurata.hotelmanagement.databinding.GirdItemBinding;

import java.util.ArrayList;
import java.util.List;

public class PromotionsRecyclerAdapter extends RecyclerView.Adapter<PromotionsRecyclerAdapter.PromotionViewHolder>{
    private List<Promotion> list;
    private PromotionListener promotionListener;


    public PromotionsRecyclerAdapter (List<Promotion> list, PromotionListener promotionListener) {
        this.list = list;
        this.promotionListener = promotionListener;
    }


    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PromotionViewHolder(
                GirdItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        holder.setData(list.get(position), promotionListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilteredList(ArrayList<Promotion> ListToShow){
        this.list= ListToShow;
        notifyDataSetChanged();
    }

    public static class PromotionViewHolder extends RecyclerView.ViewHolder {
        GirdItemBinding binding;

        public PromotionViewHolder(GirdItemBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }

        void setData(Promotion p, PromotionListener promotionListener){
            binding.gridCaption.setText(p.getName());
            Glide.with(binding.gridImage).load(p.getImg()).into(binding.gridImage);

            binding.getRoot().setOnClickListener(v -> {
                Promotion model = new Promotion();
                model.setName(p.getName());
                model.setId(p.getId());
                model.setUserID(p.getUserID());
                model.setHoteltypeID(p.getHoteltypeID());
                model.setCode(p.getCode());
                model.setHotelID(p.getHotelID());
                model.setRoomtypeID(p.getRoomtypeID());
                model.setRoomID(p.getRoomID());
                model.setRemai(p.getRemai());
                model.setSum(p.getSum());
                model.setDiscount_ratio(p.getDiscount_ratio());
                model.setDescription(p.getDescription());
                model.setImg(p.getImg());
                model.setTime_start(p.getTime_start());
                model.setTime_end(p.getTime_end());
                model.setCreated_at(p.getCreated_at());
                promotionListener.onUserClicked(model);
            });
        }
    }
    public interface PromotionListener {
        void onUserClicked(Promotion promotion);
    }
}
