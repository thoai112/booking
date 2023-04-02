package com.kurata.hotelmanagement.ui.popular;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.hotelmanagement.data.repository.Repository;

import java.util.List;

public class PopularViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<String>> mPop;
    private Repository mRepository;

    public void init(){
        if (mPop!=null){
            return;
        }
        mRepository = Repository.getInstance();
    }

    public LiveData<List<String>> getPopData(){
        return mPop = mRepository.getPopz();
    }

    public LiveData<List<String>> getPopRoomData(){
        return mPop = mRepository.getPoproom();
    }

}