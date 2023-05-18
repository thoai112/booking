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
import java.util.Date;

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
        Promotion model = new Promotion();
        Bundle bundle = new Bundle();
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
        model.setDescription(promotion.getDescription());
        model.setImg(promotion.getImg());

        Date dateStart = promotion.getTime_start().toDate();
        Date dateEnd = promotion.getTime_end().toDate();
        Date createAt = promotion.getCreated_at().toDate();

        bundle.putSerializable("model",model);
        bundle.putLong("dateStart",dateStart.getTime());
        bundle.putLong("dateEnd",dateEnd.getTime());
        bundle.putLong("creatAt",createAt.getTime());
        intent.putExtra("promotion", true);
        intent.putExtra("BUNDLE",bundle);

        startActivityForResult(intent,1);
        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }
}