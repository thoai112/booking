package com.kurata.hotelmanagement.ui.popular;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.adapter.HotelsRecyclerAdapter;
import com.kurata.hotelmanagement.data.model.Hotel;
import com.kurata.hotelmanagement.data.model.Popula;
import com.kurata.hotelmanagement.databinding.FragmentPopularBinding;
import com.kurata.hotelmanagement.databinding.PopupHoteltypeBinding;
import com.kurata.hotelmanagement.ui.hotel.HotelViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class Popular extends Fragment implements HotelsRecyclerAdapter.HotelListener{

    //value hotel
    ArrayList<Hotel> list = new ArrayList<Hotel>();
    ArrayList<Popula> pop = new ArrayList<Popula>();
    //model view
    private PopularViewModel mViewModel;
    private HotelViewModel zViewModel;

    //binding
    private FragmentPopularBinding binding;
    private PopupHoteltypeBinding UBinding;

    //recycler adapter
    @Inject
    HotelsRecyclerAdapter recyclerAdapter;

    //spinner
    ArrayAdapter<String> adapterItems;
    private String[] items =  {"Activate","Disable"};
    String item;

    //value hotel_id pop
    @Inject
    List<String> hotel_id = new ArrayList<String>();;

    public Popular () {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPopularBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //mViewModel =  new ViewModelProvider(requireActivity()).get(PopularViewModel.class);


        zViewModel =  new ViewModelProvider(requireActivity()).get(HotelViewModel.class);
        zViewModel.init();

        //getHotel_id();
        recyclerAdapter = new HotelsRecyclerAdapter(list,this);
        binding.RecyclerView.setHasFixedSize(true);
        binding.RecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.RecyclerView.setAdapter(recyclerAdapter);

        zViewModel.getAllHotel().observe(getViewLifecycleOwner(), hotels -> {
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
        binding.hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Popular popular = new Popular();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,popular).addToBackStack(null).commit();
            }
        });

        binding.room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Room_pop room_pop = new Room_pop();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,room_pop).addToBackStack(null).commit();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PopularViewModel.class);
        mViewModel.init();

        mViewModel.getPopData().observe(getViewLifecycleOwner(), new Observer<List<String>>() {

            @Override
            public void onChanged(List<String> strings) {
                hotel_id.clear();
                hotel_id.addAll(strings);
            }

        });

    }

    private void filter(String text) {

        ArrayList<Hotel> filteredList = new ArrayList<Hotel>();
        for (Hotel item : list) {
            Log.e("item", "item is: " + item.getName() + "  \n\n");
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            filteredList.clear();
            recyclerAdapter.setFilteredList(filteredList);
        } else {
            recyclerAdapter.setFilteredList(filteredList);
        }
    }


    private void Popup(int gravity, String id) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        UBinding = PopupHoteltypeBinding.inflate(getLayoutInflater());
        dialog.setContentView(UBinding.getRoot());

        init();

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(false);


        if(check(id)){
            UBinding.autoCompleteTxt.setText(items[0]);
        }
        else{
            UBinding.autoCompleteTxt.setText(items[1]);
        }

        adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.drop_down_item, items);
        UBinding.autoCompleteTxt.setAdapter(adapterItems);

        UBinding.autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
            }
        });


        UBinding.mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        UBinding.mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "test "+ hotel_id.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

    }
    public void init(){
        UBinding.image.setVisibility(View.GONE);
        UBinding.rate.setVisibility(View.GONE);
        UBinding.mDelete.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = UBinding.main.getLayoutParams();
        params.height = 550;
        UBinding.main.setLayoutParams(params);
    }

    public Boolean check(String id){
        for (String value : hotel_id)
        {
            if(value.equals(id)){
                Log.d("hhuuu", value);
                return TRUE;
            }
        }
        return FALSE;
    }

    public void getHotel_id(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference applicationsRef = firestore.collection("popular");
        DocumentReference applicationIdRef = applicationsRef.document("pop");
        applicationIdRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    hotel_id = (List<String>) document.get("hotel_id");
                    Log.d("test hotel_id", hotel_id.toString());
                }
            }
        });
    }

    @Override
    public void onUserClicked(Hotel hotel) {
        Popup(Gravity.CENTER, hotel.getId());
    }
}