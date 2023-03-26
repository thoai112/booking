package com.kurata.hotelmanagement.ui.hotel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.hotelmanagement.data.model.Hoteltype;
import com.kurata.hotelmanagement.data.repository.HotelRepository;

import java.util.List;

public class HotelsViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<List<Hoteltype>> mHoteltypes;
    private HotelRepository mRepository;

    public void init(){
        if (mHoteltypes!=null){
            return;
        }
        mRepository = HotelRepository.getInstance();
        mHoteltypes=mRepository.getAllHoteltype();
    }

    public LiveData<List<Hoteltype>> getAllHoteltypeData(){
        return mHoteltypes;
    }
}