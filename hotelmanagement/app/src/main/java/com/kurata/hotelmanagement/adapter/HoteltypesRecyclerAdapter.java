package com.kurata.hotelmanagement.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kurata.hotelmanagement.data.model.Hoteltype;
import com.kurata.hotelmanagement.databinding.StaggereItemBinding;

import java.util.ArrayList;
import java.util.List;

public class HoteltypesRecyclerAdapter extends RecyclerView.Adapter<HoteltypesRecyclerAdapter.HoteltypeViewHolder> {

        private List<Hoteltype> list;
        private HoteltypeListener hoteltypeListener;


        public HoteltypesRecyclerAdapter (List<Hoteltype> list, HoteltypeListener hoteltypeListener) {
            this.list = list;
            this.hoteltypeListener = hoteltypeListener;
        }

    @NonNull
        @Override
        public HoteltypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HoteltypeViewHolder(
                StaggereItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
        );

        }

    @Override
    public void onBindViewHolder(@NonNull HoteltypeViewHolder holder, int position) {

            holder.setData(list.get(position),hoteltypeListener);
    }


        @Override
        public int getItemCount() {
            return list.size();
        }

        public void setFilteredList(ArrayList<Hoteltype> ListToShow){
            this.list= ListToShow;
            notifyDataSetChanged();
        }

        public static class HoteltypeViewHolder extends RecyclerView.ViewHolder {

            StaggereItemBinding binding;

            public HoteltypeViewHolder(StaggereItemBinding staggereItemBinding){
                super(staggereItemBinding.getRoot());
                binding = staggereItemBinding;
            }

            void setData(Hoteltype hoteltype, HoteltypeListener hoteltypeListener){
                binding.mTitle.setText(hoteltype.getName());
                Glide.with(binding.staggeredImages).load(hoteltype.getImg()).into(binding.staggeredImages);
                binding.getRoot().setOnClickListener(v -> {
                    Hoteltype model = new Hoteltype();
                    model.setName(hoteltype.getName());
                    model.setId(hoteltype.getId());
                    model.setImg(hoteltype.getImg());
                    model.setType(hoteltype.getType());
                    model.setStatus(hoteltype.getStatus());
                    hoteltypeListener.onUserClicked(model);
                });
            }
        }

        public interface HoteltypeListener {
            void onUserClicked(Hoteltype hoteltype);

        }


    }
