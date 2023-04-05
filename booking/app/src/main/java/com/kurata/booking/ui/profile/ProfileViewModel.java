package com.kurata.booking.ui.profile;

import androidx.lifecycle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.booking.data.model.User;
import com.kurata.booking.data.repo.AccountRepo;

public class ProfileViewModel extends ViewModel{

    private MutableLiveData<User> mUser;
    private AccountRepo mRepository;

    public void init(String uid){
        if (mUser!=null){
            return;
        }
        mRepository=AccountRepo.getInstance();
        mUser=mRepository.getData(uid);

    }

    public LiveData<User> getAccount(){
        return mUser;
    }

}
