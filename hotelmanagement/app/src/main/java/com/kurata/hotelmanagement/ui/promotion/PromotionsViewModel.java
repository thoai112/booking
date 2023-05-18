package com.kurata.hotelmanagement.ui.promotion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kurata.hotelmanagement.data.model.Promotion;
import com.kurata.hotelmanagement.data.repository.Repository;

import java.util.List;

public class PromotionsViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Promotion>> mPromotion;
    private Repository mRepository;

    public void init(){
        if (mPromotion!=null){
            return;
        }
        mRepository = Repository.getInstance();
        mPromotion=mRepository.getAllPromotion();

    }

    public LiveData<List<Promotion>> getAllPromotion(){
        return mPromotion;
    }

}