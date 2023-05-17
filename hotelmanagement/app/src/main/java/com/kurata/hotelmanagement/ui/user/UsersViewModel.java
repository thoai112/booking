package com.kurata.hotelmanagement.ui.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.hotelmanagement.data.model.User;
import com.kurata.hotelmanagement.data.repository.Repository;

import java.util.List;

public class UsersViewModel extends ViewModel{
    // TODO: Implement the ViewModel


        private MutableLiveData<List<User>> mUser;
        private Repository mRepository;

        public void init(){
            if (mUser!=null){
                return;
            }
            mRepository=Repository.getInstance();
        }
        public LiveData<List<User>> getAllUsersData(String myId, String role){
            return mUser=mRepository.getUser(myId, role);
        }



}


