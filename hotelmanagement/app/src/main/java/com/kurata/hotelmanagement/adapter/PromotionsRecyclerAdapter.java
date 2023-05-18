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

        void setData(Promotion promotion, PromotionListener hotelListener){
            binding.gridCaption.setText(promotion.getName());
            Glide.with(binding.gridImage).load(promotion.getImg()).into(binding.gridImage);
            binding.getRoot().setOnClickListener(v -> {
                Promotion model = new Promotion();
                model.setName(promotion.getName());
                model.setId(promotion.getId());
                model.setUserID(promotion.getUserID());
                model.setHoteltypeID(promotion.getHoteltypeID());
                model.setCode(promotion.getCode());
                model.setHotelID(promotion.getHotelID());
                model.setRoomtypeID(promotion.getRoomtypeID());
                model.setRoomID(promotion.getRoomID());
                model.setRemai(promotion.getRemai());
                model.setSum(promotion.getSum());
                model.setDiscount_ratio(promotion.getDiscount_ratio());
                model.setTime_start(promotion.getTime_start());
                model.setTime_end(promotion.getTime_end());
                model.setCreated_at(promotion.getCreated_at());
                model.setDescription(promotion.getDescription());
                hotelListener.onUserClicked(model);
            });
        }
    }
    public interface PromotionListener {
        void onUserClicked(Promotion promotion);
    }
}
