package com.kurata.hotelmanagement.ui.popular;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.adapter.RoomsRecyclerAdapter;
import com.kurata.hotelmanagement.data.model.Popula;
import com.kurata.hotelmanagement.data.model.Room;
import com.kurata.hotelmanagement.databinding.FragmentPopularBinding;
import com.kurata.hotelmanagement.databinding.PopupHoteltypeBinding;
import com.kurata.hotelmanagement.ui.room.RoomsViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;


public class Room_pop extends Fragment implements RoomsRecyclerAdapter.RoomListener{
    //value hotel
    ArrayList<Room> list = new ArrayList<Room>();
    ArrayList<Popula> pop = new ArrayList<Popula>();
    //model view
    private PopularViewModel mViewModel;
    private RoomsViewModel zViewModel;

    //binding
    private FragmentPopularBinding binding;
    private PopupHoteltypeBinding UBinding;

    //recycler adapter
    @Inject
    RoomsRecyclerAdapter recyclerAdapter;

    //spinner
    ArrayAdapter<String> adapterItems;
    private String[] items =  {"Activate","Disable"};
    String item;

    //value hotel_id pop
    @Inject
    List<String> room_id = new ArrayList<String>();
    List<String> new_room_id = new ArrayList<>();

    public Room_pop () {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentPopularBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //mViewModel =  new ViewModelProvider(requireActivity()).get(PopularViewModel.class);

        zViewModel =  new ViewModelProvider(requireActivity()).get(RoomsViewModel.class);
        zViewModel.init();

        //getHotel_id();
        recyclerAdapter = new RoomsRecyclerAdapter(list,this);
        binding.RecyclerView.setHasFixedSize(true);
        binding.RecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.RecyclerView.setAdapter(recyclerAdapter);


        zViewModel.getAllRoom().observe(getViewLifecycleOwner(), hotels -> {
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

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PopularViewModel.class);
        mViewModel.init();

        mViewModel.getPopRoomData().observe(getViewLifecycleOwner(), new Observer<List<String>>() {

            @Override
            public void onChanged(List<String> strings) {
                room_id.clear();
                room_id.addAll(strings);
            }

        });

    }

    private void filter(String text) {
        ArrayList<Room> filteredList = new ArrayList<Room>();
        for (Room item : list) {
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

                Log.d("ID STATUS", room_id.toString());
                updateStatus(id, check(id));
                Log.d("ID STATUS", room_id.toString());

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

    //TODO - check Status
    public Boolean check(String id){
        for (String value : room_id)
        {
            if(value.equals(id)){
                Log.d("hhuuu", value);
                return TRUE;
            }
        }
        return FALSE;
    }
    //todo - update status;
    public void updateStatus(String id,Boolean check){
        if (check){
            if(item.equals(items[1])){
                room_id.remove(id);
                Update((ArrayList<String>) room_id);
            }
        }
        else{
            if(item.equals(items[0])){
                room_id.add(id);
                Update((ArrayList<String>) room_id);
            }
        }
    }

    //todo - update status --> firestore

    private void Update(ArrayList<String> urlsList) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        if (!TextUtils.isEmpty(UBinding.autoCompleteTxt.getText())) {

            HashMap<String, Object> data = new HashMap<>();
            data.put("room_id", urlsList);
            DocumentReference reference = firestore.collection("popular").document("pop");
            reference.update(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Update Success", Toast.LENGTH_SHORT).show();
                    //dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    // todo - click
    @Override
    public void onUserClicked(Room room) {
        Popup(Gravity.CENTER, room.getId());
    }
}