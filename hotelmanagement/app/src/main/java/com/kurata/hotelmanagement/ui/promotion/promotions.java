package com.kurata.hotelmanagement.ui.promotion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.adapter.PromotionsRecyclerAdapter;
import com.kurata.hotelmanagement.data.model.Promotion;
import com.kurata.hotelmanagement.databinding.FragmentPromotionsBinding;

import java.util.ArrayList;

import javax.inject.Inject;

public class promotions extends Fragment implements PromotionsRecyclerAdapter.PromotionListener{


    private FragmentPromotionsBinding binding;

    //spinner adapter
    ArrayList<Promotion> list = new ArrayList<Promotion>();
    private PromotionsViewModel mViewModel;

    //RecyclerAdapter
    @Inject
    PromotionsRecyclerAdapter recyclerAdapter;

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


        mViewModel =  new ViewModelProvider(requireActivity()).get(PromotionsViewModel.class);
        mViewModel.init();

        recyclerAdapter = new PromotionsRecyclerAdapter(list, this);
        binding.RecyclerView.setHasFixedSize(true);
        binding.RecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.RecyclerView.setAdapter(recyclerAdapter);

        mViewModel.getAllPromotion().observe(getViewLifecycleOwner(), promotions -> {
            list.clear();
            list.addAll(promotions);
            recyclerAdapter.notifyDataSetChanged();
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), activity_enter_promotion.class);
                startActivityForResult(intent,1);

            }
        });
        return  view;
    }

    @Override
    public void onUserClicked(Promotion promotion) {
        Intent intent = new Intent(getActivity(), activity_enter_promotion.class);
        intent.putExtra("promotion", true);
        startActivityForResult(intent,1);
        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }
}