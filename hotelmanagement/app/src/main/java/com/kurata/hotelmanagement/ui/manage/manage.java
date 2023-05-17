package com.kurata.hotelmanagement.ui.manage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.databinding.FragmentManageBinding;
import com.kurata.hotelmanagement.ui.hotel.HotelMana;
import com.kurata.hotelmanagement.ui.hotel.Hotels;
import com.kurata.hotelmanagement.ui.hoteltype.Hoteltypes;
import com.kurata.hotelmanagement.ui.popular.Popular;
import com.kurata.hotelmanagement.ui.room.Rooms;
import com.kurata.hotelmanagement.ui.roomtype.RoomType;
import com.kurata.hotelmanagement.ui.user.Users;
import com.kurata.hotelmanagement.utils.Constants;
import com.kurata.hotelmanagement.utils.Preference;


public class manage extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentManageBinding binding;
    private Preference preferenceManager;

    public manage() {
        // Required empty public constructor
    }


    public static manage newInstance(String param1, String param2) {
        manage fragment = new manage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentManageBinding.inflate(inflater, container,false);
        View view = binding.getRoot();

        preferenceManager = new Preference(getContext());

        if (preferenceManager.getString(Constants.P_ROLE).equals("Admin")){
            binding.row4.setVisibility(View.GONE);
        }
        else{
            binding.row2.setVisibility(View.GONE);
            binding.row1.setVisibility(View.GONE);
        }

        binding.mUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Users users = new Users();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,users).addToBackStack(null).commit();
            }
        });

        binding.mHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Hoteltypes hoteltypes = new Hoteltypes();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,hoteltypes).addToBackStack(null).commit();

            }
        });

        binding.mRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rooms rooms = new Rooms();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,rooms).addToBackStack(null).commit();

            }
        });

        binding.mhotels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferenceManager.getString(Constants.P_ROLE).equals("Admin")){
                    Hotels hotels = new Hotels();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,hotels).addToBackStack(null).commit();
                }else{
                    HotelMana hotels = new HotelMana();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,hotels).addToBackStack(null).commit();
                }


            }
        });

        binding.mRoomtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RoomType roomtype = new RoomType();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,roomtype).addToBackStack(null).commit();
            }
        });

        binding.mPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Popular popular = new Popular();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,popular).addToBackStack(null).commit();
            }
        });

        return view;
    }


}