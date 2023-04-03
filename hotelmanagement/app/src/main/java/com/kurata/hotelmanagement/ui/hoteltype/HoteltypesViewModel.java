package com.kurata.hotelmanagement.ui.hoteltype;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.hotelmanagement.data.model.Hoteltype;
import com.kurata.hotelmanagement.data.repository.HotelRepository;

import java.util.List;

public class HoteltypesViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Hoteltype>> mHoteltype;
    private HotelRepository mRepository;

    public void init(){
        if (mHoteltype!=null){
            return;
        }
        mRepository = HotelRepository.getInstance();
        mHoteltype=mRepository.getAllHoteltype();
    }

    public LiveData<List<Hoteltype>> getAllHoteltypeData(){
        return mHoteltype;
    }

    public LiveData<List<Hoteltype>> getHoteltypeActivateData(){
        return mHoteltype = mRepository.getHoteltypeActivate();
    }

}