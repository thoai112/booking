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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.data.model.Hoteltype;
import com.kurata.hotelmanagement.databinding.BottomSheetDialogBinding;
import com.kurata.hotelmanagement.databinding.FragmentRoomsBinding;
import com.kurata.hotelmanagement.ui.hoteltype.HoteltypesViewModel;

import java.util.ArrayList;

public class Rooms extends Fragment {

    private RoomsViewModel mViewModel;
    private BottomSheetDialogBinding Binding;
    private FragmentRoomsBinding Rbinding;

    ArrayList<Hoteltype> list = new ArrayList<Hoteltype>();
    private HoteltypesViewModel zViewModel;

    public static Rooms newInstance() {
        return new Rooms();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Rbinding = FragmentRoomsBinding.inflate(inflater, container, false);
        View view = Rbinding.getRoot();

        zViewModel =  new ViewModelProvider(requireActivity()).get(HoteltypesViewModel.class);
        zViewModel.init();

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



        dialog.show();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

}