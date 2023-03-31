package com.kurata.hotelmanagement.ui.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.hotelmanagement.data.model.Room;
import com.kurata.hotelmanagement.data.repository.RoomRepository;

import java.util.List;

public class RoomsViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Room>> mRoom;
    private RoomRepository mRepository;
    //String hoteltype_id, String hotel_id, String room_id
    public void init(){
        if (mRoom!=null){
            return;
        }
        mRepository = RoomRepository.getInstance();
        mRoom=mRepository.getAllRooms();
        //hoteltype_id, hotel_id, room_id

    }

    public LiveData<List<Room>> getAllRoomData(){
        return mRoom;
    }
}