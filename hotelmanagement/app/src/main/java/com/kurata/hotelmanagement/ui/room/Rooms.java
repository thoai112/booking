package com.kurata.hotelmanagement.ui.room;

import android.app.Dialog;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.adapter.RoomsRecyclerAdapter;
import com.kurata.hotelmanagement.data.model.Hotel;
import com.kurata.hotelmanagement.data.model.Hoteltype;
import com.kurata.hotelmanagement.data.model.Room;
import com.kurata.hotelmanagement.data.model.Roomtype;
import com.kurata.hotelmanagement.databinding.BottomSheetDialogBinding;
import com.kurata.hotelmanagement.databinding.FragmentRoomsBinding;
import com.kurata.hotelmanagement.ui.hotel.HotelViewModel;
import com.kurata.hotelmanagement.ui.hoteltype.HoteltypesViewModel;
import com.kurata.hotelmanagement.ui.roomtype.RoomTypeViewModel;
import com.kurata.hotelmanagement.utils.Preference;

import java.util.ArrayList;

import javax.inject.Inject;

public class Rooms extends Fragment implements RoomsRecyclerAdapter.RoomListener{

    //room
    private RoomsViewModel mViewModel;
    private BottomSheetDialogBinding Binding;
    private FragmentRoomsBinding Rbinding;
    ArrayList<Room> room = new ArrayList<Room>();
    ArrayList<Room> roomx = new ArrayList<Room>();

    //spinner adapter
    ArrayList<Hoteltype> list = new ArrayList<Hoteltype>();
    ArrayList<Roomtype> roomtype = new ArrayList<Roomtype>();
    ArrayList<Hotel> hotel = new ArrayList<Hotel>();
    private HoteltypesViewModel zViewModel;
    private RoomTypeViewModel rViewModel;
    private HotelViewModel hViewModel;

    //firebase
    private FirebaseFirestore firestore;

    //Value spinner filter

    private String shoteltype = null;
    private String shotel = null ;
    private String sroomtype = null;

    //save data
    private Preference preferenceManager;

    //RecyclerAdapter
    @Inject
    RoomsRecyclerAdapter recyclerAdapter;


    public Rooms () {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Rbinding = FragmentRoomsBinding.inflate(inflater, container, false);
        View view = Rbinding.getRoot();

        firestore = FirebaseFirestore.getInstance();
        //getData(firestore,"rating", adapter);
        //save data
        preferenceManager = new Preference(getActivity());

        mViewModel =  new ViewModelProvider(requireActivity()).get(RoomsViewModel.class);
        mViewModel.init();

        recyclerAdapter = new RoomsRecyclerAdapter(room, this);
        Rbinding.RecyclerView.setHasFixedSize(true);
        Rbinding.RecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        Rbinding.RecyclerView.setAdapter(recyclerAdapter);


        mViewModel.getAllRoom().observe(getViewLifecycleOwner(), roomModels -> {
            room.clear();
            room.addAll(roomModels);
            recyclerAdapter.notifyDataSetChanged();
        });


        Rbinding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        Rbinding.back.setOnClickListener(v-> getActivity().onBackPressed());


        Rbinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddRoom.class);
                intent.putExtra("id_cu", "kkt");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        Rbinding.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RoomsViewModel.class);
        // TODO: Use the ViewModel
    }


    private void filter(String text) {

        ArrayList<Room> filteredList = new ArrayList<Room>();
        for (Room item: room){
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

    private void showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Binding = BottomSheetDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(Binding.getRoot());

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        window.setAttributes(windowAttributes);

        //dialog.setCancelable(false);

        //get ID --> filter
        getDataHoteltype();
        getDataRoomtype();


        Binding.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recycleradapter();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void recycleradapter()
    {
        mViewModel =  new ViewModelProvider(requireActivity()).get(RoomsViewModel.class);
        mViewModel.init();

        recyclerAdapter = new RoomsRecyclerAdapter(room, this);
        Rbinding.RecyclerView.setHasFixedSize(true);
        Rbinding.RecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        Rbinding.RecyclerView.setAdapter(recyclerAdapter);

        if (shoteltype == null && shotel == null && sroomtype == null){
            mViewModel.getAllRoom().observe(getViewLifecycleOwner(), roomModels -> {
                room.clear();
                room.addAll(roomModels);
                recyclerAdapter.notifyDataSetChanged();
            });
        }else{
            mViewModel.getAllRoomData(shoteltype,shotel,sroomtype).observe(getViewLifecycleOwner(), roomModels -> {
                room.clear();
                room.addAll(roomModels);
                recyclerAdapter.notifyDataSetChanged();
            });
        }
    }


    private void getDataRoomtype(){
        //CollectionReference data = firestore.collection(collection);
        rViewModel = new ViewModelProvider(requireActivity()).get(RoomTypeViewModel.class);
        rViewModel.init();

//        AutoCompleteRoomtypeAdapter adapter = new AutoCompleteRoomtypeAdapter(getContext(), roomtype);
        ArrayAdapter<Roomtype> adapter = new ArrayAdapter<Roomtype>(getActivity(), R.layout.drop_down_item, roomtype);
        Binding.sproomtype.setAdapter(adapter);

        Binding.sproomtype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Roomtype item = (Roomtype) adapterView.getItemAtPosition(i);
                sroomtype = item.getId();
            }
        });

        rViewModel.getRoomtypeActivateData().observe(getViewLifecycleOwner(), roomtypes  -> {
            roomtype.clear();
            roomtype.addAll(roomtypes);
            adapter.notifyDataSetChanged();
        });

    }

    private void getDataHoteltype() {
        //CollectionReference data = firestore.collection(collection);
        zViewModel = new ViewModelProvider(requireActivity()).get(HoteltypesViewModel.class);
        zViewModel.init();

        ArrayAdapter<Hoteltype> adapter = new ArrayAdapter<Hoteltype>(getActivity(), R.layout.drop_down_item, list);
        Binding.sphoteltype.setAdapter(adapter);


        Binding.sphoteltype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hoteltype item = (Hoteltype) adapterView.getItemAtPosition(i);
                shoteltype = item.getId();
                Binding.mHotel.setVisibility(View.VISIBLE);
                Binding.layoutShare.setVisibility(View.VISIBLE);
                getDataHotel(item.getId());
            }
        });

        zViewModel.getHoteltypeActivateData().observe(getViewLifecycleOwner(), hotelModels -> {
            list.clear();
            list.addAll(hotelModels);
            adapter.notifyDataSetChanged();
        });

    }

    private void getDataHotel(String uid){
        hViewModel =  new ViewModelProvider(requireActivity()).get(HotelViewModel.class);
        hViewModel.init();

//        AutoCompleteHoteltypeAdapter adapter = new AutoCompleteHoteltypeAdapter(getContext(), list);
        ArrayAdapter<Hotel> adapter = new ArrayAdapter<Hotel>(getActivity(), R.layout.drop_down_item, hotel);
        Binding.sphotel.setAdapter(adapter);

        //Binding.layoutShare.setVisibility(View.VISIBLE);
        Binding.sphotel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Hotel item = (Hotel) adapterView.getItemAtPosition(i);
                shotel = item.getId();
            }
        });
        hViewModel.getHotelActivateData(uid).observe(getViewLifecycleOwner(), hotelModels -> {
            hotel.clear();
            hotel.addAll(hotelModels);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onUserClicked(Room room) {
        onConversationClicked(room);
    }

    public void onConversationClicked(Room room) {

        Room model = new Room();
        Intent intent = new Intent(getContext(), AddRoom.class);
        Bundle bundle = new Bundle();
        model.setName(room.getName());
        model.setId(room.getId());
        model.setAbout(room.getAbout());
        model.setImage(room.getImage());
        model.setStatus(room.getStatus());
        model.setHoteltype_id(room.getHoteltype_id());
        model.setHotel_id(room.getHotel_id());
        model.setRoomtype_id(room.getRoomtype_id());
        model.setPrice(room.getPrice());
        model.setRemai(room.getRemai());
        model.setSum(room.getSum());

        bundle.putSerializable("model",model);
        intent.putExtra("BUNDLE",bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

}