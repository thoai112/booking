package com.kurata.hotelmanagement.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.hotelmanagement.data.model.User;
import com.kurata.hotelmanagement.data.repository.Repository;

public class ProfileViewModel extends ViewModel{

    private MutableLiveData<User> mUser;
    private Repository mRepository;

    public void init(String uid){
        if (mUser!=null){
            return;
        }
        mRepository=Repository.getInstance();
        mUser=mRepository.getData(uid);

    }

    public LiveData<User> getProfile(){
        return mUser;
    }

}
