package com.kurata.hotelmanagement.ui.room;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.adapter.AutoCompleteHoteltypeAdapter;
import com.kurata.hotelmanagement.adapter.AutoCompleteRoomtypeAdapter;
import com.kurata.hotelmanagement.data.model.Hoteltype;
import com.kurata.hotelmanagement.data.model.Roomtype;
import com.kurata.hotelmanagement.databinding.BottomSheetDialogBinding;
import com.kurata.hotelmanagement.databinding.FragmentRoomsBinding;
import com.kurata.hotelmanagement.ui.hoteltype.HoteltypesViewModel;
import com.kurata.hotelmanagement.ui.roomtype.RoomTypeViewModel;

import java.util.ArrayList;

public class Rooms extends Fragment {

    private RoomsViewModel mViewModel;
    private BottomSheetDialogBinding Binding;
    private FragmentRoomsBinding Rbinding;

    ArrayList<Hoteltype> list = new ArrayList<Hoteltype>();
    ArrayList<Roomtype> roomtype = new ArrayList<Roomtype>();
    private HoteltypesViewModel zViewModel;
    private RoomTypeViewModel rViewModel;

    private FirebaseFirestore firestore;
    private String item;
    public Rooms () {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Rbinding = FragmentRoomsBinding.inflate(inflater, container, false);
        View view = Rbinding.getRoot();

        firestore = FirebaseFirestore.getInstance();
        //getData(firestore,"rating", adapter);


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
        getDataHotltype(firestore,"rating");
        getDataRoomtype(firestore,"rooms");



        dialog.show();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    private void getDataHotltype(FirebaseFirestore firestore,String collection){
        CollectionReference data = firestore.collection(collection);
        zViewModel =  new ViewModelProvider(requireActivity()).get(HoteltypesViewModel.class);
        zViewModel.init();

        AutoCompleteHoteltypeAdapter adapter = new AutoCompleteHoteltypeAdapter(getContext(), list);
        Binding.sphoteltype.setAdapter(adapter);

        //Binding.layoutShare.setVisibility(View.VISIBLE);
        Binding.sphoteltype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getActivity(), "tststs"+item, Toast.LENGTH_SHORT).show();
                Binding.mHotel.setVisibility(View.VISIBLE);
                Binding.layoutShare.setVisibility(View.VISIBLE);
            }
        });
        zViewModel.getAllHoteltypeData().observe(getViewLifecycleOwner(), hotelModels -> {
            list.clear();
            list.addAll(hotelModels);
            adapter.notifyDataSetChanged();
        });
    }

    private void getDataRoomtype(FirebaseFirestore firestore,String collection){
        CollectionReference data = firestore.collection(collection);
        rViewModel = new ViewModelProvider(requireActivity()).get(RoomTypeViewModel.class);
        rViewModel.init();

        AutoCompleteRoomtypeAdapter adapter = new AutoCompleteRoomtypeAdapter(getContext(), roomtype);
        Binding.sproomtype.setAdapter(adapter);


        rViewModel.getAllRoomtypeData().observe(getViewLifecycleOwner(), roomtypes  -> {
            roomtype.clear();
            roomtype.addAll(roomtypes);
            adapter.notifyDataSetChanged();
        });
    }

}