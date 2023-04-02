package com.kurata.hotelmanagement.ui.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.adapter.HotelsRecyclerAdapter;
import com.kurata.hotelmanagement.data.model.Hotel;
import com.kurata.hotelmanagement.databinding.FragmentHotelBinding;

import java.util.ArrayList;

import javax.inject.Inject;

public class HotelMana extends Fragment implements HotelsRecyclerAdapter.HotelListener {

    private static final String TAG = "HotelsFragment_Tag";
    ArrayList<Hotel> list = new ArrayList<Hotel>();
    private HotelViewModel mViewModel;
    private FragmentHotelBinding binding;
    private String Hoteltype_id;

    @Inject
    HotelsRecyclerAdapter recyclerAdapter;


    public HotelMana() {

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        binding = FragmentHotelBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mViewModel =  new ViewModelProvider(requireActivity()).get(HotelViewModel.class);
        mViewModel.init();

        Log.d("UUID", bundle.getString("id"));
        Hoteltype_id = bundle.getString("id");

        recyclerAdapter = new HotelsRecyclerAdapter(list,this);
        binding.gridView.setHasFixedSize(true);
        binding.gridView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.gridView.setAdapter(recyclerAdapter);

        mViewModel.getAllHotelData(bundle.getString("id")).observe(getViewLifecycleOwner(), hotels -> {
            list.clear();
            list.addAll(hotels);
            recyclerAdapter.notifyDataSetChanged();
        });

        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }

        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), AddHotel.class);
                intent.putExtra("id_cu", bundle.getString("id"));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HotelViewModel.class);
        // TODO: Use the ViewModel
    }

    private void filter(String text) {

        ArrayList<Hotel> filteredList = new ArrayList<Hotel>();
        for (Hotel item: list){
            Log.e("item", "item is: " +item.getName() +"  \n\n");
            if (item.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()){
            filteredList.clear();
            recyclerAdapter.setFilteredList(filteredList);
        }
        else {
            recyclerAdapter.setFilteredList(filteredList);
        }


    }

    @Override
    public void onUserClicked(Hotel hotel) {
        onConversationClicked(hotel);
    }

    public void onConversationClicked(Hotel hotel) {

        Hotel model = new Hotel();
        Intent intent = new Intent(getContext(), AddHotel.class);
        Bundle bundle = new Bundle();
        model.setId(hotel.getId());
        model.setName(hotel.getName());
        model.setAddress(hotel.getAddress());
        model.setAbout(hotel.getAbout());
        model.setStatus(hotel.getStatus());
        model.setImage(hotel.getImage());

        bundle.putSerializable("model",model);
        intent.putExtra("ht_id", Hoteltype_id);
        intent.putExtra("BUNDLE",bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }
}