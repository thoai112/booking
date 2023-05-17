package com.kurata.hotelmanagement.ui.hotel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.hotelmanagement.data.model.City;
import com.kurata.hotelmanagement.data.model.Hotel;
import com.kurata.hotelmanagement.data.repository.HotelRepository;

import java.util.List;

public class HotelViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Hotel>> mHotels;
    private MutableLiveData<List<City>> mCities;
    private HotelRepository mRepository;

    public void init(){
        if (mHotels!=null){
            return;
        }
        mRepository = HotelRepository.getInstance();
    }

    public LiveData<List<Hotel>> getAllHotel(){
        return mHotels=mRepository.getAllHotel();
    }

    public LiveData<List<Hotel>> getAllHotelData(String uid){
        return mHotels=mRepository.getAllHotelsID(uid);
    }

    public LiveData<List<Hotel>> getAllHotelUID(String uid){
        return mHotels=mRepository.getHotelsUID(uid);
    }

    public LiveData<List<Hotel>> getHotelActivateData(String uid){
        return mHotels=mRepository.getHotelActivate(uid);
    }

    public LiveData<List<Hotel>> getHotelActivateCustomer(String uid){
        return mHotels=mRepository.getHotelActivateCus(uid);
    }

    public LiveData<List<City>> getCities(){
        return mCities=mRepository.getCity();
    }
}