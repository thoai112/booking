package com.kurata.booking.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.kurata.booking.AdapterRecyclerView.PopularRecyclerViewAdapter;
import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.databinding.FragmentHomeBinding;

import java.util.ArrayList;

import javax.inject.Inject;

public class Home extends Fragment implements  PopularRecyclerViewAdapter.PopularListener{

    private static final String TAG = "PopularsFragment_Tag";
    ArrayList<Hotel> list = new ArrayList<Hotel>();
    private HomeViewModel mViewModel;
    private FragmentHomeBinding binding;

    //RecyclerAdapter --> Hotel
    @Inject
    PopularRecyclerViewAdapter recyclerAdapter;

    public  Home () {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        mViewModel =  new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        mViewModel.init();

        recyclerAdapter = new PopularRecyclerViewAdapter(list,this);
        binding.RecyclerView.setHasFixedSize(true);
        binding.RecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        binding.RecyclerView.setAdapter(recyclerAdapter);

        mViewModel.getAllHotelPopular().observe(getViewLifecycleOwner(), hotels -> {
            list.clear();
            list.addAll(hotels);
            Log.d("ashgjdhd", list.toString());
            recyclerAdapter.notifyDataSetChanged();
        });



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onUserClicked(Hotel hotel) {

    }
}