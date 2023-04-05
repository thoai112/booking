package com.kurata.booking.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.booking.data.model.Hotel;
import com.kurata.booking.data.repo.HotelRepo;

import java.util.List;

public class HomeViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Hotel>> mHotels;
    private HotelRepo mRepository;

    public void init(){
        if (mHotels!=null){
            return;
        }
        mRepository = HotelRepo.getInstance();
    }

    public LiveData<List<Hotel>> getAllHotelPopular(){
        return mHotels=mRepository.getHotelActivate();
    }

}