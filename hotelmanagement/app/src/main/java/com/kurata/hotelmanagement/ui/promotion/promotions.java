package com.kurata.hotelmanagement.ui.promotion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.kurata.hotelmanagement.databinding.FragmentPromotionsBinding;

public class promotions extends Fragment {


    private FragmentPromotionsBinding binding;

    public promotions() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPromotionsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.promotionAddroomCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), activity_enter_promotion.class);
                startActivityForResult(intent,1);

            }
        });
        return  view;
    }
}