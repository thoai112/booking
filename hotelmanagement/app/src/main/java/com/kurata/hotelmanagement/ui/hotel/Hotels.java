package com.kurata.hotelmanagement.ui.hotel;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.adapter.HoteltypesRecyclerAdapter;
import com.kurata.hotelmanagement.data.model.Hoteltype;
import com.kurata.hotelmanagement.databinding.FragmentHotelsBinding;
import com.kurata.hotelmanagement.databinding.PopupHoteltypeBinding;
import com.kurata.hotelmanagement.ui.hoteltype.HoteltypesViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

public class Hotels extends Fragment implements HoteltypesRecyclerAdapter.HoteltypeListener {

    private static final String TAG = "HotelsFragment_Tag";
    ArrayList<Hoteltype> list = new ArrayList<Hoteltype>();

    private HotelsViewModel mViewModel;
    private HoteltypesViewModel zViewModel;
    private FragmentHotelsBinding binding;
    private PopupHoteltypeBinding UBinding;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    @Inject
    HoteltypesRecyclerAdapter recyclerAdapter;



    public Hotels () {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
            binding = FragmentHotelsBinding.inflate(inflater, container, false);
            View view = binding.getRoot();

            zViewModel =  new ViewModelProvider(requireActivity()).get(HoteltypesViewModel.class);
            zViewModel.init();

            recyclerAdapter = new HoteltypesRecyclerAdapter(list,this);
            binding.RecyclerView.setHasFixedSize(true);
            binding.RecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            binding.RecyclerView.setAdapter(recyclerAdapter);

            zViewModel.getAllHoteltypeData().observe(getViewLifecycleOwner(), hotelModels -> {
                list.clear();
                list.addAll(hotelModels);
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

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HotelsViewModel.class);
        // TODO: Use the ViewModel

    }

    @Override
    public void onUserClicked(Hoteltype hoteltype) {
        Bundle bundle = new Bundle();
        bundle.putString("id", hoteltype.getId());
        Log.d("ID", bundle.getString("id"));
        HotelMana fragmentHotelMana = new HotelMana();
        fragmentHotelMana.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentHotelMana).addToBackStack(null).commit();

    }

    private void filter(String text) {

        ArrayList<Hoteltype> filteredList = new ArrayList<Hoteltype>();
        for (Hoteltype item: list){
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

}