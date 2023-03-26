package com.kurata.hotelmanagement.ui.roomtype;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.hotelmanagement.data.model.Roomtype;
import com.kurata.hotelmanagement.data.repository.RoomRepository;

import java.util.List;

public class RoomTypeViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<List<Roomtype>> mRoomtype;
    private RoomRepository mRepository;

    public void init(){
        if (mRoomtype!=null){
            return;
        }
        mRepository = RoomRepository.getInstance();
        mRoomtype=mRepository.getAllRoomtype();

    }

    public LiveData<List<Roomtype>> getAllHoteltypeData(){
        return mRoomtype;
    }
}